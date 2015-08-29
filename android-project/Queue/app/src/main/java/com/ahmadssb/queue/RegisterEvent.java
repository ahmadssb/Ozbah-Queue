package com.ahmadssb.queue;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterEvent extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pDialog;
    Button btnCreateEvent;
    ListView lvEventList;

    private Dialog dialog;


    ArrayList<HashMap<String, String>> alEventList, alNewEvent;
    JSONArray jsonEventsList = null;
    JSONArray jsonNewEvent = null;


    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_EVENTS = "events";
    public static final String TAG_EVENT_ID = "event_id";
    public static final String TAG_EVENT_NAME = "event_name";
    public static final String TAG_EVENT_PASSWORD = "event_password";

    public static final String URL_EVENTS_LIST = "http://192.168.1.27/GoogleDrive/ozbah/eventsList.php";
    public static final String URL_REGISTER_NEW_EVENT = "http://localhost/GoogleDrive/ozbah/registerEvent.php";

    private SharedPreferences sharedPref;


    private void updateCreateNewEventJSONData() {

        alNewEvent = new ArrayList<HashMap<String, String>>();


        JSONParser jParser = new JSONParser();

        JSONObject json = jParser.getJSONFromUrl(URL_REGISTER_NEW_EVENT);

        try {

            jsonNewEvent = json.getJSONArray(TAG_EVENTS);

            for (int i = 0; i < jsonNewEvent.length(); i++) {
                JSONObject c = jsonNewEvent.getJSONObject(i);

                //gets the content of each tag
                String event_id = c.getString(TAG_EVENT_ID);
                String evet_name = c.getString(TAG_EVENT_NAME);
                String event_password = c.getString(TAG_EVENT_PASSWORD);


                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(TAG_EVENT_ID, event_id);
                map.put(TAG_EVENT_NAME, evet_name);
                map.put(TAG_EVENT_PASSWORD, event_password);

                // adding HashList to ArrayList
                alNewEvent.add(map);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class CreateNewEvent extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterEvent.this);
            pDialog.setMessage(getString(R.string.creating_new_event));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            updateCreateNewEventJSONData();

            return null;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            pDialog.dismiss();

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        this.sharedPref = getSharedPreferences(getPackageName(), 0);
        btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new CreateNewEvent().execute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_event, menu);
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
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnCancel:
                dialog.dismiss();
                break;

            case R.id.btnCreateEvent:
                String sEventName = ((EditText) dialog.findViewById(R.id.etEventName)).getText().toString();
                String sEventPassword = ((EditText) dialog.findViewById(R.id.etEventPassword)).getText().toString();

                if ( sEventName.isEmpty() || sEventName == "") {
                    ((EditText) dialog.findViewById(R.id.etEventName)).setHint(R.string.empty_event_name);
                    ((EditText) dialog.findViewById(R.id.etEventName)).setHintTextColor(Color.RED);
                }else if(sEventPassword.isEmpty()|| sEventPassword == ""){
                    ((EditText) dialog.findViewById(R.id.etEventPassword)).setHint(R.string.type_password);
                    ((EditText) dialog.findViewById(R.id.etEventPassword)).setHintTextColor(Color.RED);
                }else{
                    new CreateNewEvent().execute();
                    dialog.dismiss();

                }

        }
    }
}
