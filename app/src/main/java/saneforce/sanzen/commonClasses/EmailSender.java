package saneforce.sanzen.commonClasses;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Multipart;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;

public class EmailSender {

    private static final String TAG = "EmailSender";
    private final String senderEmail = "sanzen.dev@gmail.com"; // Replace with your email
    private final String senderPassword = "SANZEN@2024";     // Replace with your password
    private final String recipientEmail = "saneforceapps@gmail.com";         // Replace with developer email

//    public void sendCrashLog(final Context context, final File logFile, final String stackTrace) {
//        new Thread(() -> {
//            try {
//                Properties props = new Properties();
//                props.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your SMTP host
//                props.put("mail.smtp.socketFactory.port", "465");
//                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//                props.put("mail.smtp.auth", "true");
//                props.put("mail.smtp.port", "465");
//                System.out.println(props);
//
//                Session session = Session.getDefaultInstance(props,
//                                                             new Authenticator() {
//                                                                 protected PasswordAuthentication getPasswordAuthentication() {
//                                                                     return new PasswordAuthentication(senderEmail, senderPassword);
//                                                                 }
//                                                             });
//
//                Message message = new MimeMessage(session);
//                message.setFrom(new InternetAddress(senderEmail));
//                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
//                message.setSubject("App Crash Report - Version " + getAppVersionName(context));
//
//                System.out.println(message);
//                // Create the message body
//                MimeBodyPart messageBodyPart = new MimeBodyPart();
//                messageBodyPart.setText("App crashed with the following stack trace:\n\n" + stackTrace + "\n\nAttached is the log file for more details.");
//
//                System.out.println(messageBodyPart);
//                // Create attachment
//                MimeBodyPart attachmentPart = new MimeBodyPart();
//                if (logFile != null && logFile.exists()) {
//                    attachmentPart.attachFile(logFile);
//                    attachmentPart.setFileName(logFile.getName());
//                } else {
//                    messageBodyPart.setText("App crashed with the following stack trace:\n\n" + stackTrace + "\n\nAttached is the log file for more details.\n\nLog file not found.");
//                }
//
//                Multipart multipart = new MimeMultipart();
//                multipart.addBodyPart(messageBodyPart);
//                if (logFile != null && logFile.exists()) {
//                    multipart.addBodyPart(attachmentPart);
//                }
//
//                message.setContent(multipart);
//
//                System.out.println(message);
//
//                Transport.send(message);
//
//                Log.i(TAG, "Crash log email sent successfully!");
//                System.out.println("Crash log email sent successfully!");
//
//            } catch (MessagingException e) {
//                Log.e(TAG, "Error sending crash log email: " + e.getMessage());
//                e.printStackTrace();
//            } catch (Exception e){
//                Log.e(TAG, "Error attaching log file: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }).start();
//    }

    private String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return "N/A";
        }
    }
}