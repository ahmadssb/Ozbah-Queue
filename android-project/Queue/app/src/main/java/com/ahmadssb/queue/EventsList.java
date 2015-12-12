package com.ahmadssb.queue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventsList extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pDialog;
    Button btnCreateEvent;
    ListView lvEventList;

    private Dialog dialog;


    ArrayList<HashMap<String, String>> alEventList, alNewEvent;
    JSONArray jsonEventsList = null;
    JSONArray jsonNewEvent = null;
    String sEventName , sEventPassword;

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_EVENTS = "events";
    public static final String TAG_EVENT_ID = "event_id";
    public static final String TAG_EVENT_NAME = "event_name";
    public static final String TAG_EVENT_PASSWORD = "event_password";

//    public static final String URL_EVENTS_LIST = "http://YOUR-WEBSITE-LINK/ozbah/eventsList.php";
//    public static final String URL_REGISTER_NEW_EVENT = "http://YOUR-WEBSITE-LINK/ozbah/registerEvent.php";
//    public static final String URL_SELECT_EVENT = "http://YOUR-WEBSITE-LINK/ozbah/selectEvent.php";

    public static final String URL_EVENTS_LIST = "http://192.168.1.111/GoogleDrive/Code-Projects/Web/_AHMADSSB/ozbah/php-web-services/eventsList.php";
    public static final String URL_REGISTER_NEW_EVENT = "http://192.168.1.111/GoogleDrive/Code-Projects/Web/_AHMADSSB/ozbah/php-web-services/registerEvent.php";
    public static final String URL_SELECT_EVENT = "http://192.168.1.111/ozbah/GoogleDrive/Code-Projects/Web/_AHMADSSB/php-web-services/selectEvent.php";

    private SharedPreferences sharedPref;

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.btnCancel:
                dialog.dismiss();
                break;

            case R.id.btnCreateEvent:
                 this.sEventName = ((EditText) dialog.findViewById(R.id.etEventName)).getText().toString();
                 this.sEventPassword = ((EditText) dialog.findViewById(R.id.etEventPassword)).getText().toString();

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
                break;
            case R.id.button_cancel:
                dialog.dismiss();
                break;
            case R.id.button_ok:
                this.sEventPassword = ((EditText) dialog.findViewById(R.id.etEventPassword)).getText().toString();
                if(sEventPassword.isEmpty()|| sEventPassword == ""){
                    ((EditText) dialog.findViewById(R.id.etEventPassword)).setHint(R.string.type_password);
                    ((EditText) dialog.findViewById(R.id.etEventPassword)).setHintTextColor(Color.RED);
                }else{
                    new SelectEvent(sharedPref.getString("eventid",null),sEventPassword).execute();
                    dialog.dismiss();
                }
                break;
        }
    }

    private void updateEventsList() {

        ListAdapter adapter = new SimpleAdapter(EventsList.this, alEventList,
                R.layout.activity_analytics_application,
                new String[]{TAG_EVENT_ID, TAG_EVENT_NAME},
                new int[]{R.id.tvEventID, R.id.tvEventName});

        lvEventList = (ListView) findViewById(R.id.lvEventsList);
        lvEventList.setAdapter(adapter);

        lvEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // do something


                TextView tvEventId = (TextView) (view.findViewById(R.id.tvEventID));
                String sEventID = tvEventId.getText().toString();

                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putString("eventid_"+sEventID, sEventID);
                edit.putString("eventid", sEventID);
                edit.commit();

                //Toast.makeText(EventsList.this, "id: " + sharedPref.getString("eventid", null), Toast.LENGTH_LONG).show();

                dialog = new Dialog(EventsList.this);
                dialog.setContentView(R.layout.select_event_check_password_layout);
                dialog.findViewById(R.id.button_cancel).setOnClickListener(
                        EventsList.this);
                dialog.findViewById(R.id.button_ok).setOnClickListener(
                        EventsList.this);
                dialog.show();


            }
        });


    }

    private void updateJSONData() {

        alEventList = new ArrayList<HashMap<String, String>>();

        JSONParser jParser = new JSONParser();

        JSONObject json = jParser.getJSONFromUrl(URL_EVENTS_LIST);
        Log.d("Loding Events Attemp", json.toString());

        try {

            jsonEventsList = json.getJSONArray(TAG_EVENTS);

            // looping through all posts according to the json object returned
            for (int i = 0; i < jsonEventsList.length(); i++) {
                JSONObject c = jsonEventsList.getJSONObject(i);

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
                alEventList.add(map);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        this.sharedPref = getSharedPreferences(getPackageName(), 0);


        findViewById(R.id.btnCreateEvent).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog = new Dialog(EventsList.this);
                dialog.setContentView(R.layout.activity_register_event);
                dialog.findViewById(R.id.btnCancel).setOnClickListener(
                        EventsList.this);
                dialog.findViewById(R.id.btnCreateEvent).setOnClickListener(
                        EventsList.this);
                dialog.show();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadEvents().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_offline) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(EventsList.this);
            builder.setTitle(R.string.action_offline_description);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class LoadEvents extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventsList.this);
            pDialog.setMessage(getString(R.string.loading_events));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            updateJSONData();

            return null;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            pDialog.dismiss();
            updateEventsList();
        }

    }

    public class CreateNewEvent extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventsList.this);
            pDialog.setMessage(getString(R.string.creating_new_event));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            //updateCreateNewEventJSONData();

            alNewEvent = new ArrayList<HashMap<String, String>>();
            int status;

            try {

                JSONParser jParser = new JSONParser();
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_EVENT_NAME, sEventName));
                params.add(new BasicNameValuePair(TAG_EVENT_PASSWORD, sEventPassword));

                Log.d("request!", "starting New Event");

                JSONObject json = jParser.makeHttpRequest(URL_REGISTER_NEW_EVENT, "POST", params);
                status = json.getInt(TAG_SUCCESS);

                if(status == 1){

                    jsonNewEvent = json.getJSONArray(TAG_EVENTS);
                    // full json response
                    Log.d("creating new event", json.toString());

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

                    Log.d("event added", json.toString());
                    finish();
                    startActivity(new Intent(EventsList.this, EventsList.class));
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("event status != 1", json.toString());
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            pDialog.dismiss();
            if(file_url != null){
                Toast.makeText(EventsList.this,file_url, Toast.LENGTH_LONG).show();
            }
        }

    }

    public class SelectEvent extends AsyncTask<String, String, String> {

        String id, password;
        int status;
        public SelectEvent(String _id, String _password){
            this.id = _id;
            this.password = _password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventsList.this);
            pDialog.setMessage(getString(R.string.checking_password));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {



            try {

                JSONParser jParser = new JSONParser();
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_EVENT_ID, this.id));
                params.add(new BasicNameValuePair(TAG_EVENT_PASSWORD, this.password));

                Log.d("request!", "starting New Event");

                JSONObject json = jParser.makeHttpRequest(URL_SELECT_EVENT, "POST", params);
                Log.d("Login attempt", json.toString());

                this.status = json.getInt(TAG_SUCCESS);

                if(this.status == 1){


                    Log.d("status = 1: ", json.toString());

                    //finish();
                    //startActivity(new Intent(EventsList.this, EventsList.class));
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("event status != 1", json.toString());
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
            pDialog.dismiss();
            if(file_url != null){
                Toast.makeText(EventsList.this,file_url, Toast.LENGTH_LONG).show();

                if(this.status == 1) {
                    startActivity(new Intent(EventsList.this, OnlineGameActivity.class));
                    finish();
                }


            }
        }

    }

}
