package saneforce.sanzen.ai;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import saneforce.sanzen.utility.TimeUtils;

public class CustomerVisitAnalyzer {

    public static class CustomerVisit {
        private String customerCode;
        private String customerName;
        private String visitDateString;
        private Date visitDate;
        private String customerType;

        public CustomerVisit(String customerCode, String customerName, String visitDateString, Date visitDate, String customerType) {
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.visitDateString = visitDateString;
            this.visitDate = visitDate;
            this.customerType = customerType;
        }

        public String getCustomerCode() { return customerCode; }
        public String getCustomerName() { return customerName; }
        public String getVisitDateString() { return visitDateString; }
        public Date getVisitDate() { return visitDate; }
        public String getCustomerType() { return customerType; }
    }

    public static class Customer {
        private String code;
        private String name;
        private String townCode;
        private double latitude;
        private double longitude;
        private String customerType;
        private String dateOfBirth; // Added Date of Birth field
        private String weddingDate; // Added Wedding Date field


        public Customer(String code, String name, String townCode, double latitude, double longitude, String customerType, String dateOfBirth, String weddingDate) {
            this.code = code;
            this.name = name;
            this.townCode = townCode;
            this.latitude = latitude;
            this.longitude = longitude;
            this.customerType = customerType;
            this.dateOfBirth = dateOfBirth;
            this.weddingDate = weddingDate;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getTownCode() { return townCode; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getCustomerType() { return customerType; }
        public String getDateOfBirth() { return dateOfBirth; }
        public String getWeddingDate() { return weddingDate; }
    }

    private static class SuggestedCustomer {
        Customer customer;
        float distance;

        SuggestedCustomer(Customer customer, float distance) {
            this.customer = customer;
            this.distance = distance;
        }

        public Customer getCustomer() { return customer; }
        public float getDistance() { return distance; }
    }

    // --- New data class to hold Nearest Customer information ---
    public static class NearestCustomer {
        private Customer customer;
        private float distance;

        public NearestCustomer(Customer customer, float distance) {
            this.customer = customer;
            this.distance = distance;
        }

        public Customer getCustomer() { return customer; }
        public float getDistance() { return distance; }
    }

    // --- New data class to hold Birthday/Anniversary information ---
    public static class SpecialDateCustomer {
        private Customer customer;
        private String dateType; // "Birthday" or "Anniversary"
        private String date; // The actual date string

        public SpecialDateCustomer(Customer customer, String dateType, String date) {
            this.customer = customer;
            this.dateType = dateType;
            this.date = date;
        }

        public Customer getCustomer() { return customer; }
        public String getDateType() { return dateType; }
        public String getDate() { return date; }
    }


    // --- New data class to hold the daily visit recommendation ---
    public static class DailyVisitRecommendation {
        private int totalCustomers;
        private int estimatedWorkingDays;
        private int recommendedVisitsPerDayOverall; // Overall recommendation
        private Map<String, Integer> recommendedVisitsPerDayPerType; // Recommendation per customer type
        private Map<String, Integer> totalCustomersPerType; // Total customers per type

        public DailyVisitRecommendation(int totalCustomers, int estimatedWorkingDays, int recommendedVisitsPerDayOverall, Map<String, Integer> recommendedVisitsPerDayPerType, Map<String, Integer> totalCustomersPerType) {
            this.totalCustomers = totalCustomers;
            this.estimatedWorkingDays = estimatedWorkingDays;
            this.recommendedVisitsPerDayOverall = recommendedVisitsPerDayOverall;
            this.recommendedVisitsPerDayPerType = recommendedVisitsPerDayPerType;
            this.totalCustomersPerType = totalCustomersPerType;
        }

        public int getTotalCustomers() { return totalCustomers; }
        public int getEstimatedWorkingDays() { return estimatedWorkingDays; }
        public int getRecommendedVisitsPerDayOverall() { return recommendedVisitsPerDayOverall; }
        public Map<String, Integer> getRecommendedVisitsPerDayPerType() { return recommendedVisitsPerDayPerType; }
        public Map<String, Integer> getTotalCustomersPerType() { return totalCustomersPerType; }
    }

    // --- New data class to hold the overall analysis result ---
    public static class VisitAnalysisResult {
        private DailyVisitRecommendation dailyRecommendation;
        private Map<String, List<String>> recentVisitsGrouped;
        private Map<String, List<String>> unvisitedType1Grouped; // Specific grouping for unvisited Type 1
        private List<NearestCustomer> nearestCustomers; // List of nearest customers within 1km
        private List<SpecialDateCustomer> specialDateCustomers; // Customers with birthdays/anniversaries
        private List<String> fallbackUnvisitedCustomers; // For the fallback list (all unvisited)
        private String errorMessage;

        // Constructor
        public VisitAnalysisResult(DailyVisitRecommendation dailyRecommendation, Map<String, List<String>> recentVisitsGrouped, Map<String, List<String>> unvisitedType1Grouped, List<NearestCustomer> nearestCustomers, List<SpecialDateCustomer> specialDateCustomers, List<String> fallbackUnvisitedCustomers, String errorMessage) {
            this.dailyRecommendation = dailyRecommendation;
            this.recentVisitsGrouped = recentVisitsGrouped;
            this.unvisitedType1Grouped = unvisitedType1Grouped;
            this.nearestCustomers = nearestCustomers;
            this.specialDateCustomers = specialDateCustomers;
            this.fallbackUnvisitedCustomers = fallbackUnvisitedCustomers;
            this.errorMessage = errorMessage;
        }

        // Getters
        public DailyVisitRecommendation getDailyRecommendation() { return dailyRecommendation; }
        public Map<String, List<String>> getRecentVisitsGrouped() { return recentVisitsGrouped; }
        public Map<String, List<String>> getUnvisitedType1Grouped() { return unvisitedType1Grouped; }
        public List<NearestCustomer> getNearestCustomers() { return nearestCustomers; }
        public List<SpecialDateCustomer> getSpecialDateCustomers() { return specialDateCustomers; }
        public List<String> getFallbackUnvisitedCustomers() { return fallbackUnvisitedCustomers; }
        public String getErrorMessage() { return errorMessage; }
    }


    private static final SimpleDateFormat JSON_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat JSON_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
    private static final SimpleDateFormat JSON_MONTH_DAY_FORMAT = new SimpleDateFormat("MM-dd", Locale.US); // For comparing month and day


    public static VisitAnalysisResult analyzeRecentCustomerVisits(String visitJsonArrayString, String customerJsonArrayString, int recentDaysThreshold, double currentLatitude, double currentLongitude) {
        Map<String, Customer> customerMap = new HashMap<>();
        Map<String, CustomerVisit> latestVisitsMap = new HashMap<>();
        Set<String> currentWorkTownCodes = new HashSet<>();
        Customer latestVisitedCustomerToday = null; // Keep for potential future use, but not for nearest
        Date latestVisitDateTimeToday = null;
        String errorMessage = null; // To capture any parsing or analysis errors

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date currentDate = today.getTime();
        String currentDateString = JSON_DATE_FORMAT.format(currentDate);
        String currentMonthDayString = JSON_MONTH_DAY_FORMAT.format(currentDate);


        final float MAX_SUGGESTION_DISTANCE_METERS = 20 * 1000; // Still used for overall suggestions if fallback
        final float NEAREST_CUSTOMER_DISTANCE_METERS = 1000; // 1 km for nearest customer
        final int ESTIMATED_WORKING_DAYS_PER_MONTH = 25; // As per user's suggestion
        final int DAYS_GROUP_INTERVAL = 10; // Interval for grouping days ago
        final int UNVISITED_TYPE1_MAX_DAYS_AGO = 90; // Max days ago for unvisited Type 1
        final int UNVISITED_TYPE1_MIN_DAYS_AGO = 10; // Min days ago for unvisited Type 1
        final int SPECIAL_DATE_LOOKAHEAD_DAYS = 7; // Look ahead for birthdays/anniversaries


        try {
            JSONArray customerJsonArray = new JSONArray(customerJsonArrayString);
            for (int i = 0; i<customerJsonArray.length(); i++) {
                JSONObject jsonObject = customerJsonArray.getJSONObject(i);
                String code = jsonObject.optString("Code", "");
                String name = jsonObject.optString("Name", "Unknown Customer");
                String townCode = jsonObject.optString("Town_Code", "");
                String custType = jsonObject.optString("CustType", "Unknown"); // Get CustType from customer data
                String dateOfBirth = jsonObject.optString("DOB", null); // Assuming DOB field
                String weddingDate = jsonObject.optString("DOA", null); // Assuming DOA field
                double latitude = 0.0;
                double longitude = 0.0;

                latitude = jsonObject.optDouble("Lat", 0.0);
                longitude = jsonObject.optDouble("Long", 0.0);

                if(!code.isEmpty()) {
                    customerMap.put(code, new Customer(code, name, townCode, latitude, longitude, custType, dateOfBirth, weddingDate));
                }
            }
        } catch (JSONException e) {
            Log.e("VisitAnalyzer", "Error parsing customer details JSON", e);
            errorMessage = "Error parsing customer details data: " + e.getMessage();
        }


        DailyVisitRecommendation dailyRecommendation = null;
        Map<String, List<String>> groupedRecentVisits = null;
        Map<String, List<String>> unvisitedType1Grouped = null;
        List<NearestCustomer> nearestCustomers = null;
        List<SpecialDateCustomer> specialDateCustomers = null;
        List<String> fallbackUnvisitedCustomers = null;
        try {
            JSONArray visitJsonArray = new JSONArray(visitJsonArrayString);

            for (int i = 0; i<visitJsonArray.length(); i++) {
                JSONObject jsonObject = visitJsonArray.getJSONObject(i);

                String custCode = jsonObject.optString("CustCode", "");
                String dcrDtString = jsonObject.optString("Dcr_dt", null);
                String custType = jsonObject.optString("CustType", "");
                String dayStatus = jsonObject.optString("day_status", "");
                String townCode = jsonObject.optString("town_code", "");
                String vtmString = jsonObject.optString("vtm", null);
                String fwIndicator = jsonObject.optString("FW_Indicator", "");


                // --- Identify Current Work Towns (based on any date with specific criteria) ---
                // Criteria: CustCode empty, CustType "0", day_status "1", FW_Indicator "F"
                if(custCode.isEmpty() && "0".equals(custType) && "0".equals(dayStatus) && "F".equals(fwIndicator) && dcrDtString != null) {
                    if(!townCode.isEmpty()) {
                        String[] townCodes = townCode.split(",");
                        for (String code : townCodes) {
                            if(!code.trim().isEmpty()) {
                                currentWorkTownCodes.add(code.trim());
                            }
                        }
                    }
                    // Continue processing the rest of the entries as they might be customer visits
                }


                // --- Process Customer Visits ---
                // Skip entries without a customer code or visit date
                if(custCode.isEmpty() || dcrDtString == null || dcrDtString.isEmpty()) {
                    continue;
                }

                Customer customer = customerMap.get(custCode);
                String custName = (customer != null) ? customer.getName() : jsonObject.optString("CustName", "Unknown Customer");
                String visitCustType = (customer != null) ? customer.getCustomerType() : jsonObject.optString("CustType", "Unknown"); // Get CustType for the visit

                try {
                    Date visitDate = JSON_DATE_FORMAT.parse(dcrDtString);

                    CustomerVisit currentVisit = new CustomerVisit(custCode, custName, dcrDtString, visitDate, visitCustType);

                    if(latestVisitsMap.containsKey(custCode)) {
                        Date existingVisitDate = latestVisitsMap.get(custCode).getVisitDate();
                        if(visitDate != null && visitDate.after(existingVisitDate)) {
                            latestVisitsMap.put(custCode, currentVisit);
                        }
                    }else {
                        latestVisitsMap.put(custCode, currentVisit);
                    }

                    // --- Identify the last visited customer today in current work towns (for potential future use) ---
                    // This is still calculated but NOT used for nearest customer suggestions anymore
                    if(customer != null && !custCode.isEmpty() && currentWorkTownCodes.contains(customer.getTownCode())) {
                        Date visitDateTime = visitDate;
                        if(vtmString != null && !vtmString.isEmpty()) {
                            String fullDateTimeString = null;
                            try {
                                fullDateTimeString = dcrDtString + " " + vtmString;
                                visitDateTime = JSON_DATETIME_FORMAT.parse(fullDateTimeString);

                            } catch (ParseException e) {
                                Log.e("VisitAnalyzer", "Error parsing visit date and time: " + fullDateTimeString, e);
                                visitDateTime = visitDate;
                            }
                        }


                        if(latestVisitedCustomerToday == null || (latestVisitDateTimeToday != null && (visitDateTime != null && visitDateTime.after(latestVisitDateTimeToday)))) {
                            latestVisitedCustomerToday = customer;
                            latestVisitDateTimeToday = visitDateTime;
                        }
                    }


                } catch (ParseException e) {
                    Log.e("VisitAnalyzer", "Error parsing date for CustCode " + custCode + ": " + dcrDtString, e);
                }
            }

            // --- Calculate Daily Visit Recommendation Per Customer Type ---
            Map<String, Integer> totalCustomersPerType = new HashMap<>();
            Map<String, Integer> recommendedVisitsPerDayPerType = new HashMap<>();

            // Count total customers per type
            for (Customer customer : customerMap.values()) {
                String type = customer.getCustomerType();
                totalCustomersPerType.put(type, totalCustomersPerType.getOrDefault(type, 0) + 1);
            }

            // Calculate recommended visits per day per type
            if(ESTIMATED_WORKING_DAYS_PER_MONTH>0) {
                for (Map.Entry<String, Integer> entry : totalCustomersPerType.entrySet()) {
                    String type = entry.getKey();
                    int count = entry.getValue();
                    int recommended = (int) Math.ceil((double) count / ESTIMATED_WORKING_DAYS_PER_MONTH);
                    recommendedVisitsPerDayPerType.put(type, recommended);
                }
            }

            // Calculate Overall Daily Visit Recommendation
            int totalCustomers = customerMap.size();
            int recommendedVisitsPerDayOverall = 0;
            if(totalCustomers>0 && ESTIMATED_WORKING_DAYS_PER_MONTH>0) {
                recommendedVisitsPerDayOverall = (int) Math.ceil((double) totalCustomers / ESTIMATED_WORKING_DAYS_PER_MONTH);
            }

            dailyRecommendation = new DailyVisitRecommendation(totalCustomers, ESTIMATED_WORKING_DAYS_PER_MONTH, recommendedVisitsPerDayOverall, recommendedVisitsPerDayPerType, totalCustomersPerType);


            Calendar recentThresholdCal = Calendar.getInstance();
            recentThresholdCal.setTime(currentDate);
            recentThresholdCal.add(Calendar.DAY_OF_YEAR, -recentDaysThreshold);
            Date recentThresholdDate = recentThresholdCal.getTime();

            List<CustomerVisit> recentVisitsList = new ArrayList<>();
            Set<String> visitedCustomerCodesRecent = new HashSet<>();

            for (CustomerVisit latestVisit : latestVisitsMap.values()) {
                Date visitDate = latestVisit.getVisitDate();

                if(visitDate != null && (visitDate.after(recentThresholdDate) || visitDate.equals(recentThresholdDate))) {
                    recentVisitsList.add(latestVisit);
                    visitedCustomerCodesRecent.add(latestVisit.getCustomerCode());
                }
            }

            Collections.sort(recentVisitsList, new Comparator<CustomerVisit>() {
                @Override
                public int compare(CustomerVisit v1, CustomerVisit v2) {
                    return v1.getVisitDate().compareTo(v2.getVisitDate());
                }
            });


            // --- Group Recent Visits by Days Ago (10-day intervals) ---
            groupedRecentVisits = new HashMap<>();

            for (CustomerVisit sortedVisit : recentVisitsList) {
                Date visitDate = sortedVisit.getVisitDate();
                long diffInMillies = currentDate.getTime() - visitDate.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                String visitSummaryLine = sortedVisit.getCustomerName(); // Just the name for the list

                String groupKey;
                if(diffInDays == 0) {
                    groupKey = "Today";
                }else if(diffInDays == 1) {
                    groupKey = "Yesterday";
                }else {
                    // Calculate the start of the 10-day interval
                    int intervalStart = ((int) diffInDays - 2) / DAYS_GROUP_INTERVAL * DAYS_GROUP_INTERVAL + 2;
                    int intervalEnd = intervalStart + DAYS_GROUP_INTERVAL - 1;
                    if(intervalEnd>recentDaysThreshold) {
                        intervalEnd = recentDaysThreshold; // Cap at the threshold
                    }
                    groupKey = "Days Ago: " + intervalStart + " - " + intervalEnd;
                }

                // Add the customer to the appropriate group
                if(!groupedRecentVisits.containsKey(groupKey)) {
                    groupedRecentVisits.put(groupKey, new ArrayList<>());
                }
                groupedRecentVisits.get(groupKey).add(visitSummaryLine + (diffInDays>1 ? " (" + diffInDays + " days ago)" : "")); // Add days ago in parentheses

            }


            // --- Group Unvisited Customers (Type 1) by Days Ago (10-day intervals, 10-90 days) ---
            unvisitedType1Grouped = new HashMap<>();
            List<String> allUnvisitedType1 = new ArrayList<>(); // List to hold all unvisited Type 1 for grouping

            for (Customer customer : customerMap.values()) {
                // Check if the customer is Type 1 AND was NOT visited within the recent threshold
                if("1".equals(customer.getCustomerType()) && !visitedCustomerCodesRecent.contains(customer.getCode())) {
                    allUnvisitedType1.add(customer.getCode()); // Add customer code for later lookup/grouping
                }
            }

            // Now, for the unvisited Type 1 customers, find their *last* visit date to group them
            for (String customerCode : allUnvisitedType1) {
                Date lastVisitDate = null;
                if(latestVisitsMap.containsKey(customerCode)) {
                    lastVisitDate = latestVisitsMap.get(customerCode).getVisitDate();
                }

                long diffInDays = -1; // Indicate never visited or error
                if(lastVisitDate != null) {
                    diffInDays = TimeUnit.DAYS.convert(currentDate.getTime() - lastVisitDate.getTime(), TimeUnit.MILLISECONDS);
                }

                // Only group if the last visit was within the 10-90 days range
                if(diffInDays>=UNVISITED_TYPE1_MIN_DAYS_AGO && diffInDays<=UNVISITED_TYPE1_MAX_DAYS_AGO) {
                    Customer customer = customerMap.get(customerCode); // Get the full customer object
                    if(customer != null) {
                        String customerName = customer.getName();

                        // Calculate the start of the 10-day interval (adjusted for 10-90 range)
                        int intervalStart = ((int) diffInDays - UNVISITED_TYPE1_MIN_DAYS_AGO) / DAYS_GROUP_INTERVAL * DAYS_GROUP_INTERVAL + UNVISITED_TYPE1_MIN_DAYS_AGO;
                        int intervalEnd = intervalStart + DAYS_GROUP_INTERVAL - 1;
                        if(intervalEnd>UNVISITED_TYPE1_MAX_DAYS_AGO) {
                            intervalEnd = UNVISITED_TYPE1_MAX_DAYS_AGO; // Cap at the max threshold
                        }

                        String groupKey = "Days Ago: " + intervalStart + " - " + intervalEnd;

                        if(!unvisitedType1Grouped.containsKey(groupKey)) {
                            unvisitedType1Grouped.put(groupKey, new ArrayList<>());
                        }
                        unvisitedType1Grouped.get(groupKey).add(customerName + " (" + diffInDays + " days ago)");
                    }
                }else if(diffInDays == -1) {
                    // Handle customers who have never been visited (could add to a separate group if needed)
                    // For now, they won't appear in the 10-90 days grouping
                }
            }


            // --- Find Nearest Customers (within 1 km) based on CURRENT LOCATION ---
            nearestCustomers = new ArrayList<>();
            // Check if current location is valid
            if(currentLatitude != 0.0 || currentLongitude != 0.0) {
                Location currentLocation = new Location("");
                currentLocation.setLatitude(currentLatitude);
                currentLocation.setLongitude(currentLongitude);

                for (Customer customer : customerMap.values()) {
                    // Check if the customer has valid coordinates and is within the nearest customer distance
                    if((customer.getLatitude() != 0.0 || customer.getLongitude() != 0.0)) {

                        Location customerLocation = new Location("");
                        customerLocation.setLatitude(customer.getLatitude());
                        customerLocation.setLongitude(customer.getLongitude());

                        float distanceInMeters = currentLocation.distanceTo(customerLocation);

                        if(distanceInMeters<=NEAREST_CUSTOMER_DISTANCE_METERS) {
                            nearestCustomers.add(new NearestCustomer(customer, distanceInMeters));
                        }
                    }
                }
                // Sort nearest customers by distance
                Collections.sort(nearestCustomers, new Comparator<NearestCustomer>() {
                    @Override
                    public int compare(NearestCustomer n1, NearestCustomer n2) {
                        return Float.compare(n1.getDistance(), n2.getDistance());
                    }
                });
            }


            // --- Find Customers with Birthdays/Anniversaries ---
            specialDateCustomers = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            for (Customer customer : customerMap.values()) {
                // Check Birthday
                String dob = customer.getDateOfBirth();
                if(dob != null && !dob.isEmpty()) {
                    try {
                        Date birthDate = JSON_DATE_FORMAT.parse(dob);
                        calendar.setTime(birthDate);
                        int birthMonth = calendar.get(Calendar.MONTH);
                        int birthDay = calendar.get(Calendar.DAY_OF_MONTH);

                        calendar.setTime(currentDate);
                        int currentMonth = calendar.get(Calendar.MONTH);
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                        // Check if birthday is today or in the next 7 days (ignoring year)
                        calendar.setTime(currentDate);
                        calendar.add(Calendar.DAY_OF_YEAR, SPECIAL_DATE_LOOKAHEAD_DAYS);
                        Date lookaheadDate = calendar.getTime();

                        calendar.setTime(birthDate);
                        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR)); // Set to current year for comparison

                        if(!calendar.getTime().before(currentDate) && !calendar.getTime().after(lookaheadDate)) {
                            specialDateCustomers.add(new SpecialDateCustomer(customer, "Birthday", dob));
                        }

                    } catch (ParseException e) {
                        Log.e("VisitAnalyzer", "Error parsing DOB for customer " + customer.getCode() + ": " + dob, e);
                    }
                }

                // Check Wedding Date
                String doa = customer.getWeddingDate();
                if(doa != null && !doa.isEmpty()) {
                    try {
                        Date weddingDate = JSON_DATE_FORMAT.parse(doa);
                        calendar.setTime(weddingDate);
                        int weddingMonth = calendar.get(Calendar.MONTH);
                        int weddingDay = calendar.get(Calendar.DAY_OF_MONTH);

                        calendar.setTime(currentDate);
                        int currentMonth = calendar.get(Calendar.MONTH);
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                        // Check if wedding date is today or in the next 7 days (ignoring year)
                        calendar.setTime(currentDate);
                        calendar.add(Calendar.DAY_OF_YEAR, SPECIAL_DATE_LOOKAHEAD_DAYS);
                        Date lookaheadDate = calendar.getTime();

                        calendar.setTime(weddingDate);
                        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR)); // Set to current year for comparison

                        if(!calendar.getTime().before(currentDate) && !calendar.getTime().after(lookaheadDate)) {
                            specialDateCustomers.add(new SpecialDateCustomer(customer, "Anniversary", doa));
                        }

                    } catch (ParseException e) {
                        Log.e("VisitAnalyzer", "Error parsing DOA for customer " + customer.getCode() + ": " + doa, e);
                    }
                }
            }


            // --- Fallback: If current work area or today's last visit is not determined, suggest ALL unvisited customers ---
            // This fallback remains, but the "Not Visited (Type 1)" and "Nearest Customers" cards will take precedence if applicable.
            fallbackUnvisitedCustomers = new ArrayList<>();
            if(latestVisitedCustomerToday == null || currentWorkTownCodes.isEmpty()) {
                for (Customer customer : customerMap.values()) {
                    if(!visitedCustomerCodesRecent.contains(customer.getCode())) {
                        String custName = customer.getName();
                        if(custName != null && !custName.isEmpty()) {
                            fallbackUnvisitedCustomers.add(custName);
                        }else {
                            fallbackUnvisitedCustomers.add("Unknown Customer (Code: " + customer.getCode() + ")");
                        }
                    }
                }
                Collections.sort(fallbackUnvisitedCustomers); // Sort the fallback list alphabetically
            }else {
                // If current work area IS determined, filter fallback to only include customers
                // NOT in the current work towns and NOT visited recently.
                for (Customer customer : customerMap.values()) {
                    if(!visitedCustomerCodesRecent.contains(customer.getCode()) && !currentWorkTownCodes.contains(customer.getTownCode())) {
                        String custName = customer.getName();
                        if(custName != null && !custName.isEmpty()) {
                            fallbackUnvisitedCustomers.add(custName);
                        }else {
                            fallbackUnvisitedCustomers.add("Unknown Customer (Code: " + customer.getCode() + ")");
                        }
                    }
                }
                Collections.sort(fallbackUnvisitedCustomers);
            }


        } catch (JSONException e) {
            Log.e("VisitAnalyzer", "Error parsing visit data: ", e);
            if(errorMessage == null) { // Only set if no previous error
                errorMessage = "Error parsing visit data: " + e.getMessage();
            }else {
                errorMessage += "\nError parsing visit data: " + e.getMessage();
            }
        } catch (Exception e) {
            Log.e("VisitAnalyzer", "An unexpected error occurred during analysis: ", e);
            if(errorMessage == null) { // Only set if no previous error
                errorMessage = "An unexpected error occurred during analysis: " + e.getMessage();
            }else {
                errorMessage += "\nAn unexpected error occurred during analysis: " + e.getMessage();
            }
        }

        // Return the structured result object
        return new VisitAnalysisResult(dailyRecommendation, groupedRecentVisits, unvisitedType1Grouped, nearestCustomers, specialDateCustomers, fallbackUnvisitedCustomers, errorMessage);
    }
}
