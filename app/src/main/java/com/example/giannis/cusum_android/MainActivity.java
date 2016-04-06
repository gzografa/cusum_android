package com.example.giannis.cusum_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.*;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;


public class MainActivity extends Activity {

    private final static UUID PEBBLE_APP_UUID = UUID.fromString("1C50DB4A-8F61-48C2-8D1F-A03AAA134F96");
    private final static int FALL = 0;
    GPSTracker gps;


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PebbleKit.startAppOnPebble(getApplicationContext(),PEBBLE_APP_UUID);

        // Construct output String
        StringBuilder builder = new StringBuilder();
        builder.append("Pebble Info\n\n");

        // Is the watch connected?
        boolean isConnected = PebbleKit.isWatchConnected(this);
        builder.append("Watch connected: " + (isConnected ? "true" : "false")).append("\n");

        // What is the firmware version?
        PebbleKit.FirmwareVersionInfo info = PebbleKit.getWatchFWVersion(this);
        builder.append("Firmware version: ");
        builder.append(info.getMajor()).append(".");
        builder.append(info.getMinor()).append("\n");

        // Is AppMesage supported?
        boolean appMessageSupported = PebbleKit.areAppMessagesSupported(this);
        builder.append("AppMessage supported: " + (appMessageSupported ? "true" : "false"));

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
            Log.i("DATA RECEIVED", dict.getString(0));
            TextView textView = (TextView)findViewById(R.id.firstString);
            textView.setText(dict.getString(0));
            sendSMS();
            // A new AppMessage was received, tell Pebble. if an ACK is not send timeout may occur.
            PebbleKit.sendAckToPebble(context, transaction_id);
        }

    };
    protected void sendSMS() {
        String phoneNo = "6976652492";
        String sms = "I fell! Please help!";

        sms = sms + getLocation().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
            getLocation();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
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

            // \n is for new line
            Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
//            gps.showSettingsAlert();
            location.append(" No GPS data");
        }



        return location.toString();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        PebbleKit.closeAppOnPebble(getApplicationContext(),PEBBLE_APP_UUID);
        Log.i("destroy PEBBLE app",PEBBLE_APP_UUID.toString());
    }
}
