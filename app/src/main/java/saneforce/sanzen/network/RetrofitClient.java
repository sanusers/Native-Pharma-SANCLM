package saneforce.sanzen.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okio.Buffer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ============================================================
 * <p>
 * RetrofitClient
 * <p>
 * ============================================================
 * <p>
 * A singleton-style utility class that configures and provides
 * a Retrofit instance for making HTTP API calls throughout the app.
 * <p>
 * Features:
 *   - SSL certificate validation bypass (for self-signed/internal servers)
 *   - HTTP request/response logging
 *   - SQL injection sanitization interceptor (second layer of defence)
 *   - Configurable base URL per call
 *   - RxJava3 + Gson support
 * <p>
 * Usage:
 *   ApiInterface api = RetrofitClient.getRetrofit(context, baseUrl);
 *   Call<JsonElement> call = api.getJSONElement(url, map, jsonString);
 * <p>
 * Security Notes:
 *   - SSL validation is disabled — only use on trusted internal networks.
 *   - SQL sanitization here is a CLIENT-SIDE second layer of defence.
 *     The primary defence must be prepared statements on the server (PHP/MySQL).
 * <p>
 * ============================================================
 */
public class RetrofitClient {

    private static final String TAG = "RetrofitClient";

    // ============================================================
    // Public API
    // ============================================================

    /**
     * Creates and returns a configured Retrofit instance.
     * <p>
     * Includes:
     *   - Unsafe OkHttpClient (SSL bypass for internal servers)
     *   - Gson converter for JSON serialization/deserialization
     *   - RxJava3 call adapter for reactive streams
     *
     * @param context  Application or Activity context
     * @param baseUrl  Base URL of the API server (e.g. "https://api.example.com/")
     * @return Configured Retrofit instance, or null if setup fails
     */
    public static Retrofit getClient(Context context, String baseUrl) {
        try {
            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(GetConfigUnsafeOkHttpClient().build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convenience method that returns a ready-to-use ApiInterface.
     * <p>
     * Combines getClient() + create(ApiInterface.class) in one call.
     * <p>
     * Example:
     *   ApiInterface api = RetrofitClient.getRetrofit(context, baseUrl);
     *   api.getJSONElement(url, queryMap, jsonBody).enqueue(...);
     *
     * @param context  Application or Activity context
     * @param baseUrl  Base URL of the API server
     * @return Configured ApiInterface ready to make API calls
     */
    public static ApiInterface getRetrofit(Context context, String baseUrl) {
        return getClient(context, baseUrl).create(ApiInterface.class);
    }

    // ============================================================
    // SQL Injection Sanitization
    // Do NOT modify these methods without understanding the impact.
    // These are a second layer of defence against SQL injection attacks.
    // Primary defence must be prepared statements on the server side.
    // ============================================================

    /**
     * Entry point for sanitizing the request body before it is sent to the server.
     * <p>
     * Skips sanitization entirely for multipart/binary bodies (image uploads)
     * because binary data contains raw bytes with '%' characters that are NOT
     * valid URL-encoded sequences — URLDecoder would throw IllegalArgumentException.
     * <p>
     * Handles two types of text request bodies:
     * <p>
     *   1. Raw JSON (@Body JsonObject) — Content-Type: application/json
     *      Body arrives as plain JSON string e.g. {"key":"value"}
     *      Detected by checking if trimmed body starts with '{' or '['
     *      No URL decoding needed.
     * <p>
     *   2. Form URL Encoded (@Field) — Content-Type: application/x-www-form-urlencoded
     *      Body arrives as key=value e.g. data=%7B%22key%22%3A%22value%22%7D
     *      Split on first '=' to separate field name from value.
     *      Value is URL decoded, parsed as JSON, sanitized, then re-encoded.
     * <p>
     * In both cases, only STRING VALUES inside the JSON are sanitized.
     * JSON keys, numbers, booleans, and nulls are left untouched.
     *
     * @param body        Raw request body string read from OkHttp Buffer
     * @param contentType MediaType of the request body used to detect multipart/binary
     * @return Sanitized body string safe to send to server,
     *         or original body if parsing fails or body is binary (fail-safe)
     */
    private static String sanitizeBody(String body, MediaType contentType) {
        if (body == null || body.isEmpty()) return body;
        try {
            // Skip sanitization for multipart/binary bodies (image/file uploads)
            // These contain raw binary data that will break URL decoding
            if (contentType != null) {
                String type = contentType.toString();
                if (type.contains("multipart/form-data") || type.contains("image/") || type.contains("application/octet-stream")) {
                    Log.d(TAG, "Skipping sanitization for multipart/binary body");
                    return body;
                }
            }
            String trimmed = body.trim();
            // Case 1: Raw JSON body (@Body JsonObject)
            // Content-Type: application/json
            // Body is already plain JSON — no URL decoding needed
            if (trimmed.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(trimmed);
                sanitizeJsonObject(jsonObject);
                return jsonObject.toString();
            } else if (trimmed.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(trimmed);
                sanitizeJsonArray(jsonArray);
                return jsonArray.toString();
            } else {
                // Case 2: FormUrlEncoded body (@Field)
                // Content-Type: application/x-www-form-urlencoded
                int separatorIndex = trimmed.indexOf('=');
                if (separatorIndex != -1) {
                    String fieldKey = trimmed.substring(0, separatorIndex);     // "data"
                    String fieldValue = trimmed.substring(separatorIndex + 1);  // URL encoded JSON
                    // URL decode the value part only
                    String decoded;
                    try {
                        decoded = URLDecoder.decode(fieldValue, "UTF-8");
                    } catch (Exception e) {
                        // URL decoding failed — likely binary/malformed data
                        // Skip sanitization and return original body safely
                        Log.d(TAG, "URL decode failed — skipping sanitization: " + e.getMessage());
                        return body;
                    }
                    String decodedTrimmed = decoded.trim();
                    if (decodedTrimmed.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(decodedTrimmed);
                        sanitizeJsonObject(jsonObject);
                        // Re-encode and reconstruct: data=<encoded sanitized JSON>
                        return fieldKey + "=" + URLEncoder.encode(jsonObject.toString(), "UTF-8");
                    } else if (decodedTrimmed.startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(decodedTrimmed);
                        sanitizeJsonArray(jsonArray);
                        return fieldKey + "=" + URLEncoder.encode(jsonArray.toString(), "UTF-8");
                    } else {
                        // Plain string value — sanitize and re-encode
                        return fieldKey + "=" + URLEncoder.encode(sanitizeValue(decoded), "UTF-8");
                    }
                } else {
                    // No '=' separator found — sanitize as plain string
                    return sanitizeValue(body);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fail-safe: return original body if anything goes wrong
            // so the API call is never blocked due to sanitization failure
            return body;
        }
    }

    /**
     * Recursively traverses and sanitizes all String values inside a JSONObject.
     * <p>
     * Only String values are sanitized — numbers, booleans, and nulls
     * are left unchanged to avoid breaking data types.
     * Nested JSONObject and JSONArray values are recursively processed.
     *
     * @param jsonObject The JSONObject whose string values will be sanitized in-place
     */
    private static void sanitizeJsonObject(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                Object value = jsonObject.get(key);
                if (value instanceof String) {
                    // Sanitize string values only
                    jsonObject.put(key, sanitizeValue((String) value));
                } else if (value instanceof JSONObject) {
                    // Recurse into nested objects
                    sanitizeJsonObject((JSONObject) value);
                } else if (value instanceof JSONArray) {
                    // Recurse into nested arrays
                    sanitizeJsonArray((JSONArray) value);
                }
                // Numbers, booleans, nulls — left untouched
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Recursively traverses and sanitizes all String values inside a JSONArray.
     * <p>
     * Handles mixed arrays containing strings, nested objects, or nested arrays.
     *
     * @param jsonArray The JSONArray whose string values will be sanitized in-place
     */
    private static void sanitizeJsonArray(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Object value = jsonArray.get(i);
                if (value instanceof String) {
                    jsonArray.put(i, sanitizeValue((String) value));
                } else if (value instanceof JSONObject) {
                    sanitizeJsonObject((JSONObject) value);
                } else if (value instanceof JSONArray) {
                    sanitizeJsonArray((JSONArray) value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Strips SQL injection characters from a single string value.
     * <p>
     * Removes both raw characters and their URL-encoded equivalents.
     * URL-encoded equivalents are removed as a safety measure in case
     * any encoded characters slip through before URL decoding.
     * <p>
     * Characters removed:
     *   '   single quote        breaks string literals in SQL
     *   "   double quote        breaks string literals in SQL
     *   ;   semicolon           terminates and chains SQL queries
     *   --  double dash         SQL line comment
     *   #   hash                MySQL line comment
     *   /*  block comment open  SQL block comment
     *   * / block comment close SQL block comment
     * <p>
     * URL-encoded equivalents also removed:
     *   %27 → '    %22 → "    %3B/%3b → ;
     *   %2D%2D/%2d%2d → --   %23 → #
     *   %2F%2A/%2f%2a → /*   %2A%2F/%2a%2f → * /
     *
     * @param value The raw string value to sanitize
     * @return Sanitized string with SQL injection characters removed
     */
    private static String sanitizeValue(String value) {
        if (value == null || value.isEmpty()) return value;
        return value
                // --- Raw special characters ---
                .replace("'",      "")   // single quote
                .replace("\"",     "")   // double quote
                .replace(";",      "")   // semicolon
                .replace("--",     "")   // SQL line comment
                .replace("#",      "")   // MySQL line comment
                .replace("/*",     "")   // block comment open
                .replace("*/",     "")   // block comment close

                // --- URL encoded equivalents ---
                .replace("%27",    "")   // ' single quote
                .replace("%22",    "")   // " double quote
                .replace("%3B",    "")   // ; semicolon (uppercase)
                .replace("%3b",    "")   // ; semicolon (lowercase)
                .replace("%2D%2D", "")   // -- double dash (uppercase)
                .replace("%2d%2d", "")   // -- double dash (lowercase)
                .replace("%23",    "")   // # hash
                .replace("%2F%2A", "")   // /* block comment (uppercase)
                .replace("%2f%2a", "")   // /* block comment (lowercase)
                .replace("%2A%2F", "")   // */ block comment close (uppercase)
                .replace("%2a%2f", "");  // */ block comment close (lowercase)
    }

    // ============================================================
    // OkHttpClient Configuration
    // ============================================================

    /**
     * Builds and returns a configured OkHttpClient.Builder with:
     * <p>
     *   1. SSL certificate validation bypass
     *      Required for servers using self-signed or internal certificates.
     *      Do NOT use on public-facing production servers.
     * <p>
     *   2. Timeout configuration
     *      Connect / Write / Read timeout: 35 seconds each
     * <p>
     *   3. HTTP Logging Interceptor
     *      Logs full request and response body for debugging.
     *      Disable or reduce log level in production builds.
     * <p>
     *   4. SQL Injection Sanitizer Interceptor
     *      Automatically sanitizes all outgoing POST request bodies.
     *      Applied to every API call without any per-call changes needed.
     *      Works for both @Body JsonObject and @Field FormUrlEncoded requests.
     *      Automatically skips multipart/binary bodies (image uploads).
     *      Fail-safe: if sanitization fails, original request is sent as-is.
     * <p>
     * Interceptor execution order:
     *   Request:  Logging → Sanitizer → Server
     *   Response: Server  → Sanitizer → Logging
     * <p>
     * Body type handling in sanitizer:
     *   multipart/form-data      → Skipped (image/file uploads)
     *   image/*                  → Skipped (binary data)
     *   application/octet-stream → Skipped (binary data)
     *   application/json         → JSON values sanitized
     *   application/x-www-form-urlencoded → Decoded → sanitized → re-encoded
     *
     * @return Configured OkHttpClient.Builder
     * @throws RuntimeException if SSL context initialization fails
     */
    private static OkHttpClient.Builder GetConfigUnsafeOkHttpClient() {
        try {
            // Trust manager that bypasses SSL certificate validation
            @SuppressLint("CustomX509TrustManager")
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Initialize SSL context with trust-all manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Logging interceptor — logs full request/response body
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(35, TimeUnit.SECONDS)
                    .writeTimeout(35, TimeUnit.SECONDS)
                    .readTimeout(35, TimeUnit.SECONDS)
                    // Interceptor 1: Logging
                    .addInterceptor(loggingInterceptor)
                    // Interceptor 2: SQL Injection Sanitizer
                    // Reads the request body, sanitizes string values,
                    // rebuilds the request and forwards it to the server.
                    // Automatically skips multipart/binary bodies (image uploads).
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        RequestBody body = original.body();
                        if (body != null) {
                            try {
                                // Read body into buffer
                                // (RequestBody has no direct getString() method)
                                Buffer buffer = new Buffer();
                                body.writeTo(buffer);
                                String bodyString = buffer.readUtf8();
                                // Pass contentType to detect and skip multipart/binary
                                String sanitized = sanitizeBody(bodyString, body.contentType());
                                Log.d(TAG, "Sanitized request body: " + sanitized);
                                // Rebuild request with sanitized body
                                RequestBody newBody = RequestBody.create(sanitized, body.contentType());
                                Request newRequest = original.newBuilder().post(newBody).build();
                                return chain.proceed(newRequest);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Fail-safe: send original request if sanitization fails
                                return chain.proceed(original);
                            }
                        }
                        // GET requests or empty body — pass through unchanged
                        return chain.proceed(original);
                    });
            // Apply SSL bypass settings
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

//   public static Retrofit getClient(Context context, String baseUrl) {
//        try {
////            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
////            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
////            OkHttpClient client = new OkHttpClient.Builder()
////                    .connectTimeout(60, TimeUnit.SECONDS)
////                    .writeTimeout(30, TimeUnit.SECONDS)
////                    .readTimeout(30, TimeUnit.SECONDS)
////                    .addInterceptor(interceptor)
////                    .build();
////            return retrofit = new Retrofit.Builder()
////                    .baseUrl(baseUrl)
////                    .addConverterFactory(GsonConverterFactory.create())
////                    .client(client)
////                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
////                    .build();
//            return new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .client(GetConfigUnsafeOkHttpClient().build())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
//                    .build();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static ApiInterface getRetrofit(Context context,String baseUrl) {
//        return getClient(context,baseUrl).create(ApiInterface.class);
//    }
//
//    private static OkHttpClient.Builder GetConfigUnsafeOkHttpClient() {
//        try {
//// Create a trust manager that does not validate certificate chains :-
//            @SuppressLint("CustomX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                @SuppressLint("TrustAllX509TrustManager")
//                @Override
//                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                }
//
//                @SuppressLint("TrustAllX509TrustManager")
//                @Override
//                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                }
//
//                @Override
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return new java.security.cert.X509Certificate[]{};
//                }
//            }};
//
//// Install the all-trusting trust manager :-
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//
//// Create an ssl socket factory with our all-trusting manager :-
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                    .connectTimeout(35, TimeUnit.SECONDS)
//                    .writeTimeout(35, TimeUnit.SECONDS)
//                    .readTimeout(35, TimeUnit.SECONDS)
//                    .addInterceptor(interceptor);
//            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
//            builder.hostnameVerifier((hostname, session) -> true);
//            return builder;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
