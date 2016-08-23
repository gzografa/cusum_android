package com.example.giannis.cusum_android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;

public class Contact extends Activity {
    SharedPreferences sharedpreferences;
    String CONTACTPREFERENCES =  "contactPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        retrievePreferences();
    }


    public void clearContactForm(View view) {
        TextView sampleTextView;
        sampleTextView  = (TextView) findViewById(R.id.name );
        sampleTextView.setText("");
        sampleTextView  = (TextView) findViewById(R.id.mobile );
        sampleTextView.setText("");
        sampleTextView  = (TextView) findViewById(R.id.email );
        sampleTextView.setText("");
    }

    public void saveContactForm(View view) {

        TextView sampleTextView;
        String name, email, mobile;
        sampleTextView  = (TextView) findViewById(R.id.name );
        name = sampleTextView.getText().toString();
        sampleTextView  = (TextView) findViewById(R.id.mobile );
        mobile = sampleTextView.getText().toString();
        sampleTextView  = (TextView) findViewById(R.id.email );
        email = sampleTextView.getText().toString();

        String stringifiedContactJSON = getContactJSON(name, mobile, email);

        SharedPreferences sharedpreferences = getSharedPreferences(CONTACTPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Contact",stringifiedContactJSON);
        editor.commit();

    }

    public String getContactJSON(String name, String mobile, String email){
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("mobile", mobile);
            obj.put("email", email);

            Toast.makeText(this, "save pressed " +obj.toString(),
                    Toast.LENGTH_LONG).show();
            return  obj.toString();

        }catch(JSONException ex){
            return "{}";
        }
    }

    public void retrievePreferences(){
        SharedPreferences prefs = getSharedPreferences(CONTACTPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString("Contact", null);
        if(restoredText != null){
            setTextViews(restoredText);
        }
        Toast.makeText(this, "retrieved data " +restoredText,
                Toast.LENGTH_LONG).show();
    }

    public void setTextViews(String contact){
        try {
            TextView sampleTextView;
            JSONObject obj = new JSONObject(contact);
            String name = obj.getString("name");
            findSetTextView(R.id.name, name);
            String mobile = obj.getString("mobile");
            findSetTextView(R.id.mobile, mobile);
            String email = obj.getString("email");
            findSetTextView(R.id.email, email);
        }catch (JSONException ex){
            Log.i("tag ","error on parsing string");
        }
    }

    public void findSetTextView(int id, String value ){
        TextView sampleTextView;
        sampleTextView  = (TextView) findViewById(id);
        sampleTextView.setText(value);
    }


}
