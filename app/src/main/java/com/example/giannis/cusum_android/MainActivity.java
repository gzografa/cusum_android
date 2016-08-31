package com.example.giannis.cusum_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;


public class MainActivity extends Activity {

//    private final static UUID PEBBLE_APP_UUID = UUID.fromString("1C50DB4A-8F61-48C2-8D1F-A03AAA134F96");
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("592BBBDE-ED83-4D0F-9FB8-9E1352A8B67D");
    private final static int FALL = 0;
    GPSTracker gps;
    GMailSender sender;
    String email = "I fell! Please help!";
//    String email = "&lt;!DOCTYPE html PUBLIC \\\"-//W3C//DTD XHTML 1.0 Transitional//EN\\\" \\\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\\\"&gt;&lt;html xmlns=\\\"http://www.w3.org/1999/xhtml\\\"&gt; &lt;head&gt; &lt;meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\" /&gt; &lt;title&gt;A Simple Responsive HTML Email&lt;/title&gt; &lt;style type=\\\"text/css\\\"&gt; body {margin: 0; padding: 0; min-width: 100%!important;} .content {width: 100%; max-width: 600px;} .content {padding: 40px 30px 20px 30px;} @media only screen and (min-device-width: 601px) { .content {width: 600px !important;} } .header {padding: 40px 30px 20px 30px;} &lt;/style&gt; &lt;/head&gt; &lt;body yahoo bgcolor=\\\"#f6f8f1\\\"&gt; &lt;table width=\\\"100%\\\" bgcolor=\\\"#f6f8f1\\\" border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\"&gt; &lt;tr&gt; &lt;td&gt; &lt;table class=\\\"content\\\" align=\\\"center\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\"&gt; &lt;tr&gt; &lt;td class=\\\"header\\\" bgcolor=\\\"#c7d8a7\\\"&gt; &lt;table width=\\\"70\\\" align=\\\"left\\\" border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\"&gt; &lt;tr&gt; &lt;td height=\\\"70\\\" style=\\\"padding: 0 20px 20px 0;\\\"&gt; &lt;img src=\\\"p11.png\\\" width=\\\"70\\\" height=\\\"70\\\" border=\\\"0\\\" alt=\\\"\\\" / &gt; &lt;/td&gt; &lt;/tr&gt; &lt;/table&gt; &lt;/td&gt; &lt;/tr&gt; &lt;/table&gt; &lt;table class=\\\"content\\\" align=\\\"center\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\"&gt; &lt;tr&gt; &lt;td class=\\\"header\\\" bgcolor=\\\"#5499C7\\\"&gt; &lt;table width=\\\"70\\\" align=\\\"left\\\" border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\"&gt; &lt;tr&gt; &lt;td height=\\\"70\\\" style=\\\"padding: 0 20px 20px 0;\\\"&gt; BASIC CONTENT &lt;/td&gt; &lt;/tr&gt; &lt;/table&gt; &lt;/td&gt; &lt;/tr&gt; &lt;/table&gt; &lt;/td&gt; &lt;/tr&gt; &lt;/table&gt; &lt;/body&gt;&lt;/html&gt;";
    String username = "gianniseapdiplwmatiki@gmail.com";
    String password = "giannisEAP";
    String phoneNo = "6976652492";
    String emailReceiver = "gzografa@gmail.com";

    SharedPreferences sharedpreferences;
    String CONTACTPREFERENCES =  "contactPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(MainActivity.this,
                    Contact.class);
            startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Construct output String
        StringBuilder builder = new StringBuilder();

        // Is the watch connected?
        boolean isConnected = PebbleKit.isWatchConnected(this);
        if(isConnected){
            PebbleKit.startAppOnPebble(getApplicationContext(),PEBBLE_APP_UUID);
            builder.append("Your Pebble watch is connected").append("\n");
            SharedPreferences prefs = getSharedPreferences(CONTACTPREFERENCES, MODE_PRIVATE);
            String restoredText = prefs.getString("Contact", null);
            if(restoredText != null){
                try {
                    TextView sampleTextView;
                    JSONObject obj = new JSONObject(restoredText);
                    String name = obj.getString("name");
                    String mobile = obj.getString("mobile");
                    String email = obj.getString("email");
                    builder.append("Emergency contact name :"+name).append("\n");
                    builder.append("Emergency phone        :"+mobile).append("\n");
                    builder.append("Emergency email        :"+email).append("\n");
                    changeEmergencyDetails(mobile,email);
                }catch (JSONException ex){
                    Log.i("tag ","error on parsing string");
                }
            }
        }else{
            builder.append("Pebble watch not connected").append("\n");
            builder.append("Please connect it and restart the app").append("\n");
            View myView = findViewById(R.id.mainView);
            myView.setBackgroundColor(Color.parseColor("#f0f0a6"));
        }

        TextView textView = (TextView)findViewById(R.id.firstString);
        textView.setText(builder.toString());


        //registers the data receiver
        PebbleKit.registerReceivedDataHandler(getApplicationContext(), dataReceiver);
    }


    // Create a new receiver to get AppMessages from the C app
    PebbleKit.PebbleDataReceiver dataReceiver = new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {

        @Override
        public void receiveData(Context context, int transaction_id,
                                PebbleDictionary dict) {
            startSound();
            sendSMS();
            sendEmail();
            changeLayoutUI();
            // A new AppMessage was received, tell Pebble. if an ACK is not send timeout may occur.
            PebbleKit.sendAckToPebble(context, transaction_id);
        }
    };

    protected void sendSMS() {
        String sms = "I fell! Please help!";

        sms = sms + getLocation();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    protected void sendEmail(){

        sender = new GMailSender(username, password);
        try {
            new MyAsyncClass().execute();

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
    }

    protected void changeLayoutUI(){
        TextView textView = (TextView)findViewById(R.id.firstString);
        textView.setText("Fall Detected");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(30);


        ImageView image = (ImageView) findViewById(R.id.imageView);
        image.setImageResource(R.drawable.wet_floor);
        View myView = findViewById(R.id.mainView);
        myView.setBackgroundColor(Color.parseColor("#ee4c50"));
    }

    protected void startSound(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences(CONTACTPREFERENCES, MODE_PRIVATE);
            String restoredText = prefs.getString("Contact", null);

            email = addCoordinatesToJSOn(restoredText);

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                Log.i("******* email",emailReceiver);
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("Need help", email, "gianniseapdiplwmatiki@gmail.com", emailReceiver);
            }
            catch (Exception ex) {
                Log.i("exception caught", ex.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            Toast.makeText(getApplicationContext(), "Email send", 100).show();
        }
    }


    protected String getLocation(){
        gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        StringBuilder location = new StringBuilder();
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();


            location.append("My coordinates are \n Lat: ");
            location.append(latitude);
            location.append(" ,long:  ");
            location.append(longitude);
            location.append("\n google maps url : http://maps.google.com/?q=");
            location.append(latitude);
            location.append(",");
            location.append(longitude);

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
//            gps.showSettingsAlert();
            location.append(" No GPS data");
        }
        return location.toString();
    }

    public String addCoordinatesToJSOn(String strgifiedJSON){
        gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        StringBuilder location = new StringBuilder();
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            try {
                JSONObject obj = new JSONObject(strgifiedJSON);
                obj.put("latitude", latitude);
                obj.put("longitude", longitude);
                return  obj.toString();
            }catch(JSONException ex){
                return strgifiedJSON;
            }
        }
        return strgifiedJSON;
    }

    public void changeEmergencyDetails(String mobile, String email){
        phoneNo = mobile;
        emailReceiver = email;
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        PebbleKit.closeAppOnPebble(getApplicationContext(),PEBBLE_APP_UUID);
        Log.i("destroy PEBBLE app",PEBBLE_APP_UUID.toString());
    }
}
