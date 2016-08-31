package com.example.giannis.cusum_android;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static android.R.attr.key;
import static android.R.id.message;

public class GMailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;
    String name ;
    String mobile ;
    String email ;
    String longitude ;
    String latitude ;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender(final String user, final String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");

        props.setProperty("mail.smtp.quitwait", "false");
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body,
                                      String sender, String recipients) throws Exception {
        String geoInfo = "I fell and I am in need of assistance.<br>";
        String staticMap = "";
        JSONObject info;
        Log.i("@@@@@@ body",body);
        try {
            info = new JSONObject(body);
            name = info.getString("name");
            mobile = info.getString("mobile");
            email = info.getString("email");
            longitude = info.getString("longitude");
            latitude = info.getString("latitude");
            Log.i("####",longitude);
            Log.i("****",latitude);
            if(longitude != null && latitude != null){
                geoInfo ="I fell and I am in need of assistance.<br>My coordinates are  Lat: "+latitude+" ,long:  "+longitude+"<br> <a href=\"http://maps.google.com/?q="+ latitude+","+longitude +"\">My google map location</a>";
                staticMap = "<img src=\"http://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+"&zoom=15&size=300x230&markers=color:red%7Clabel:F%7C"+latitude+","+longitude+"&key=AIzaSyDOxeeyOuWZKg_Mu2UcsUJJexGtthJ6RwU\" alt=\"map\" width=\"300\" height=\"230\" style=\"display: block;\" /><h6>My location</h6>";
            }
            Log.i("**** StaticMap",staticMap);
        }catch (JSONException ex){
            Log.i("exception on json",ex.toString());
        }


        MimeMessage message = new MimeMessage(session);

        String msg = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> <title>Need Help!</title> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" /></head><body style=\"margin: 0; padding: 0;\"> <table border=\"0\" bgcolor=\"#70bbd9\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"> <tr> <td> <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> <tr> <td align=\"center\" bgcolor=\"#ee4c50\" style=\"padding: 20px 0 30px 0;\"><!-- <img src=\"https://www.alert-1.com/images/what-happens-1.png\" alt=\"alert\" width=\"300\" height=\"230\" style=\"display: block;\" /> --> <img src=\"https://www.welchallyn.com/content/dam/welchallyn/images/microsites/ccscfm/ccscfm-fallsIcon.png\" alt=\"alert\" width=\"400\" height=\"175\" style=\"display: block;\" /><br> <h1 style=\"font-family:helvetica; color:#fafbfc;\">Fall Detected</h1> </td> </tr> <tr> <td bgcolor=\"#ffffff\" style=\"padding: 40px 30px 40px 30px;\"> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"> <tr> <td> Hi "+ name +", </td> </tr> <tr> <td style=\"padding: 20px 0 30px 0;\">"+geoInfo+" </td> </tr> <tr> <td align=\"center\">"+staticMap+" </td> </tr> </table> </td> </tr> <tr> <td bgcolor=\"#ee4c50\" style=\"padding: 5px 10px 5px 10px;\"> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"> <tr> <td width=\"65%\"> <img src=\"https://upload.wikimedia.org/wikipedia/en/9/95/Hellenic_Open_University_logo.jpg\" alt=\"eap\" width=\"40\" height=\"40\" style=\"display: block;\" /> </td> <td align=\"right\" style=\"font-family:helvetica; color:#fafbfc;\"> Fall Detection App 2016 </td> </tr> </table> </td> </tr> </table> </td> </tr> </table></body></html>";
        message.setContent(msg,"text/html");
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);


        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(
                    recipients));
        Transport.send(message);
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}