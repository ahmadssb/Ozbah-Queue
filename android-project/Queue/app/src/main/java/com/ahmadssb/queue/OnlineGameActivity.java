package com.ahmadssb.queue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OnlineGameActivity extends AppCompatActivity implements View.OnClickListener, NfcAdapter.CreateNdefMessageCallback {
    private ListView lvCurrent, lvWaiting;
    private GridView gvCurrent, gvWaiting;
    private Button btnAdd, btnRemove, btnLoad, btnRemoveUsers;
    private TextView tvFirstGame, tvP1P3vsP2P4 , tvCurrent;
    private CheckBox cbSpecialCase;
    static final boolean isChecked = false;
    ArrayList<String> currentList, waitingList;
    private MyAdapterCurrent adapterCurrent;
    private MyAdapterWaiting adapterWaiting;

    private boolean isFirstGame = false;
    private static int countGames = 0;
    private boolean isGameSaved = false;
    private final static  int TWO_PLAYERS = 2;
    private final static  int FOUR_PLAYERS = 4;
    private final static int DEFAULT = -1;
    private static int PLAYER_SET = DEFAULT;

    private final static  int ONLINE_STORAGE = 1;
    private final static  int OFFLINE_STORAGE = 0;
    private static int STORAGE = OFFLINE_STORAGE;


    InterstitialAd mInterstitialAd;
    private int[] mAdGameCount = {3,7,12,20};
    private static boolean isAdDisplay = false;

    Tracker mTracker;
    NfcAdapter mNfcAdapter;



    private Dialog dialog;
    private ProgressDialog pDialog;


    ArrayList<HashMap<String, String>> alUsersList;
    JSONArray jsonActiveUsersList = null;
    JSONArray jsonWaitingUsersList = null;
    JSONArray jsonNewEvent = null;
    String sEventName , sEventPassword;


    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_ARRAY_ACTIVE_USERS = "active_users";
    public static final String TAG_ARRAY_WAITING_USERS = "waiting_users";
    public static final String TAG_EVENT_ID = "user_event_id";
    public static final String TAG_USER_NAME = "user_name";
    public static final String TAG_CURRENT = "current";
    public static final String TAG_WAITING = "waiting";
    public static final String TAG_TIMESTAMP = "timestamp";

//    public static final String URL_USERS_LIST = "http://YOUR-WEBSITE-LINK/ozbah/usersList.php";
//    public static final String URL_REGISTER_USERSS = "http://YOUR-WEBSITE-LINK/ozbah/registerUsers.php";
//    public static final String URL_REMOVE_EVENT_USERSS = "http://YOUR-WEBSITE-LINK/ozbah/deleteEventUsers.php";

    public static final String URL_USERS_LIST = "http://192.168.1.111/GoogleDrive/Code-Projects/Web/_AHMADSSB/ozbah/php-web-services/usersList.php";
    public static final String URL_REGISTER_USERSS = "http://192.168.1.111/GoogleDrive/Code-Projects/Web/_AHMADSSB/ozbah/php-web-services/registerUsers.php";
    public static final String URL_REMOVE_EVENT_USERSS = "http://192.168.1.111/GoogleDrive/Code-Projects/Web/_AHMADSSB/ozbah/php-web-services/deleteEventUsers.php";

    private SharedPreferences sharedPref;


    public  void setGame4Players(final Context mContext, final ArrayList<String> _currentList, final ArrayList<String> _waitingList, int countGames){
        this.PLAYER_SET = FOUR_PLAYERS;
        final int[] iCountGames = {countGames};
        final TinyDB tinydb = new TinyDB(mContext);
        tinydb.putInt("PLAYER_SET", FOUR_PLAYERS);
        tinydb.putInt("count_games", iCountGames[0]);
        this.countGames = tinydb.getInt("count_games", countGames);


        if (!(_waitingList.size() < 4) && _currentList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.alertTitleFirstGame);
            builder.setMessage(R.string.msgFirstGame);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //tvFirstGame.setVisibility(View.VISIBLE);
                    tvP1P3vsP2P4.setVisibility(View.VISIBLE);
                    iCountGames[0]++;
                    tinydb.putInt("count_games", iCountGames[0]);
                    _currentList.add(_waitingList.get(0));
                    _currentList.add(_waitingList.get(1));
                    _currentList.add(_waitingList.get(2));
                    _currentList.add(_waitingList.get(3));
                    adapterCurrent.notifyDataSetChanged();
                    _waitingList.remove(0);
                    _waitingList.remove(0);
                    _waitingList.remove(0);
                    _waitingList.remove(0);
                    adapterWaiting.notifyDataSetChanged();
                    isFirstGame = true;
                    //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                    saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);

                }
            });
            builder.create().show();

        } else if (_currentList.size() == 4) {
            if (isFirstGame) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.alertTitleFirstGame);
                builder.setMessage(R.string.msgFirstGame);
                builder.setPositiveButton(R.string.p1p3, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        _waitingList.add(_currentList.get(0));
                        _waitingList.add(_currentList.get(2));
                        _currentList.add(_waitingList.get(0));
                        _currentList.add(_waitingList.get(1));
                        _waitingList.remove(0);
                        _waitingList.remove(0);
                        _currentList.remove(0);
                        _currentList.remove(1);

                        adapterCurrent.notifyDataSetChanged();
                        adapterWaiting.notifyDataSetChanged();
                        isFirstGame = false;
                        tvP1P3vsP2P4.setVisibility(View.GONE);

                        //tvFirstGame.setVisibility(View.GONE);
                        iCountGames[0]++;
                        tinydb.putInt("count_games", iCountGames[0]);
                        //tvFirstGame.setText("" + iCountGames[0]);
                        //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                        saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);

                    }
                });
                builder.setNegativeButton(R.string.p2p4, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                        _waitingList.add(currentList.get(1));
                        _waitingList.add(currentList.get(3));
                        _currentList.add(_waitingList.get(0));
                        _currentList.add(_waitingList.get(1));
                        _waitingList.remove(0);
                        _waitingList.remove(0);
                        _currentList.remove(1);
                        _currentList.remove(2);

                        adapterCurrent.notifyDataSetChanged();
                        adapterWaiting.notifyDataSetChanged();

                        isFirstGame = false;
                        tvP1P3vsP2P4.setVisibility(View.GONE);

                        //tvFirstGame.setVisibility(View.GONE);
                        iCountGames[0]++;
                        tinydb.putInt("count_games", iCountGames[0]);
                        //tvFirstGame.setText("" + iCountGames[0]);
                        //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                        saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);


                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.alertTitle);
                builder.setPositiveButton(R.string.p3p4, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        waitingList.add(_currentList.get(2));
                        waitingList.add(_currentList.get(3));
                        _currentList.add(_waitingList.get(0));
                        _currentList.add(_waitingList.get(1));
                        _waitingList.remove(0);
                        _waitingList.remove(0);
                        _currentList.remove(2);
                        _currentList.remove(2);

                        adapterCurrent.notifyDataSetChanged();
                        adapterWaiting.notifyDataSetChanged();

                        iCountGames[0]++;
                        tinydb.putInt("count_games", iCountGames[0]);
                        //tvFirstGame.setText("" + iCountGames[0]);
                        //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                        saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);

                    }
                });
                builder.setNegativeButton(R.string.p1p2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                        _waitingList.add(_currentList.get(0));
                        _waitingList.add(_currentList.get(1));
                        _currentList.add(_waitingList.get(0));
                        _currentList.add(_waitingList.get(1));
                        _waitingList.remove(0);
                        _waitingList.remove(0);
                        _currentList.remove(0);
                        _currentList.remove(0);

                        adapterCurrent.notifyDataSetChanged();
                        adapterWaiting.notifyDataSetChanged();

                        iCountGames[0]++;
                        tinydb.putInt("count_games", iCountGames[0]);
                       // tvFirstGame.setText("" + iCountGames[0]);
                        //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                        saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.alertAddPeople);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }

    public void setGame2Players(final Context mContext, final ArrayList<String> _currentList, final ArrayList<String> _waitingList, int countGames){
        this.PLAYER_SET = TWO_PLAYERS;
        final TinyDB tinydb = new TinyDB(mContext);
        tinydb.putInt("PLAYER_SET", TWO_PLAYERS);
        tinydb.putInt("count_games", countGames);
        this.countGames = tinydb.getInt("count_games", countGames);
        final int[] iCountGames = {tinydb.getInt("count_games", countGames)};

        if (!(_waitingList.size() < 2) && _currentList.size() == 0) {

            iCountGames[0]++;
            _currentList.add(_waitingList.get(0));
            _currentList.add(_waitingList.get(1));

            adapterCurrent.notifyDataSetChanged();

            _waitingList.remove(0);
            _waitingList.remove(0);
            adapterWaiting.notifyDataSetChanged();
            isFirstGame = true;
            tinydb.putInt("count_games", iCountGames[0]);
            this.countGames = tinydb.getInt("count_games", countGames);

            saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);


        } else if (_currentList.size() == 2) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.alertTitle);
                builder.setPositiveButton(R.string.p1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        waitingList.add(_currentList.get(0));
                        _currentList.add(_waitingList.get(0));
                        _waitingList.remove(0);
                        _currentList.remove(0);

                        adapterCurrent.notifyDataSetChanged();
                        adapterWaiting.notifyDataSetChanged();

                        iCountGames[0]++;
                        tinydb.putInt("count_games", iCountGames[0]);
                        saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);

                    }
                });
                builder.setNegativeButton(R.string.p2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Next Game Button

                        _waitingList.add(_currentList.get(1));
                        _currentList.add(_waitingList.get(0));
                        _waitingList.remove(0);
                        _currentList.remove(1);

                        adapterCurrent.notifyDataSetChanged();
                        adapterWaiting.notifyDataSetChanged();

                        iCountGames[0]++;
                        tinydb.putInt("count_games", iCountGames[0]);
                        saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.alertAdd2People);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }



    }

    public  void selectGameSet(final Context mContext,  final ArrayList<String> _currentList, final ArrayList<String> _waitingList, final int countGames){
        final TinyDB tinydb = new TinyDB(mContext);
        PLAYER_SET = tinydb.getInt("PLAYER_SET", DEFAULT);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.select_player_set);
            builder.setPositiveButton(R.string.p2set, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK Button
                    if (!_currentList.isEmpty()){
                        ChangeGameSetPlayers(mContext,FOUR_PLAYERS, _currentList, _waitingList, countGames);

                    }else{

                        PLAYER_SET = TWO_PLAYERS;
                        tinydb.putInt("PLAYER_SET", TWO_PLAYERS);
                        PLAYER_SET = tinydb.getInt("PLAYER_SET", TWO_PLAYERS);
                        Toast.makeText(mContext,getString(R.string.current_player_message)+ PLAYER_SET, Toast.LENGTH_LONG ).show();
                        Log.d("PLAYER_SET_2", getString(R.string.current_player_message) + PLAYER_SET);
                    }

                }
            });
            builder.setNegativeButton(R.string.p4set, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked Next Game Button
                    if (!_currentList.isEmpty()) {
                        ChangeGameSetPlayers(mContext, TWO_PLAYERS, _currentList, _waitingList, countGames);

                    } else {

                        PLAYER_SET = FOUR_PLAYERS;
                        tinydb.putInt("PLAYER_SET", FOUR_PLAYERS);
                        PLAYER_SET = tinydb.getInt("PLAYER_SET", FOUR_PLAYERS);
                        Toast.makeText(mContext, getString(R.string.current_player_message) + PLAYER_SET, Toast.LENGTH_LONG).show();
                        Log.d("PLAYER_SET_4", getString(R.string.current_player_message) + PLAYER_SET);

                    }

                }
            });
            AlertDialog numberOfPlayersDialog = builder.create();
            numberOfPlayersDialog.show();

    }


    public void ChangeGameSetPlayers(final Context mContext,int player_set, final ArrayList<String> _currentList, final ArrayList<String> _waitingList, int countGames){
        TinyDB tinydb = new TinyDB(mContext);


        this.PLAYER_SET = tinydb.getInt("PLAYER_SET", DEFAULT);
        final int[] iCountGames = {countGames};
        tinydb.putBoolean("isFirstGame",false);
        isFirstGame = tinydb.getBoolean("isFirstGame",false);
        if(player_set == TWO_PLAYERS){
            tinydb.putInt("PLAYER_SET", FOUR_PLAYERS);
            this.PLAYER_SET = tinydb.getInt("PLAYER_SET", FOUR_PLAYERS);
            if (_currentList.size() == 2 && _waitingList.size() >= 2 ){

                tinydb.putBoolean("isFirstGame",false);
                this.isFirstGame = tinydb.getBoolean("isFirstGame",false);
                //_waitingList.add(_currentList.get(0));
                //_waitingList.add(_currentList.get(1));
                _currentList.add(_waitingList.get(0));
                _currentList.add(_waitingList.get(1));
                _waitingList.remove(0);
                _waitingList.remove(0);
                // _currentList.remove(0);
                // _currentList.remove(0);

                adapterCurrent.notifyDataSetChanged();
                adapterWaiting.notifyDataSetChanged();

                iCountGames[0]++;
                // tvFirstGame.setText("" + iCountGames[0]);
                // saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);

                Toast.makeText(mContext,getString(R.string.current_player_message)+ PLAYER_SET, Toast.LENGTH_LONG ).show();
                Log.d("Change_PLAYER_SET_4", getString(R.string.current_player_message) + PLAYER_SET);

            }else if (_currentList.size() == 2 && _waitingList.size() < 2 ){

                tinydb.putBoolean("isFirstGame",false);
                this.isFirstGame = tinydb.getBoolean("isFirstGame",false);
                //_waitingList.add(_currentList.get(0));
                //_waitingList.add(_currentList.get(1));
                //_currentList.add(_waitingList.get(0));
                //_currentList.add(_waitingList.get(1));

                _waitingList.add(0,_currentList.get(1));
                _waitingList.add(0,_currentList.get(0));
                //_waitingList.remove(0);
                //_waitingList.remove(0);
                _currentList.remove(0);
                _currentList.remove(0);

                adapterCurrent.notifyDataSetChanged();
                adapterWaiting.notifyDataSetChanged();

                iCountGames[0]++;
                // tvFirstGame.setText("" + iCountGames[0]);
                // saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);
                // Toast.makeText(mContext,R.string.current_player_message + String.valueOf(PLAYER_SET), Toast.LENGTH_LONG ).show();
                Toast.makeText(mContext,getString(R.string.current_player_message)+ PLAYER_SET, Toast.LENGTH_LONG ).show();
                Log.d("Change_PLAYER_SET_4", getString(R.string.current_player_message) + PLAYER_SET);

            }

        }else if(player_set == FOUR_PLAYERS){
            //setGame4Players(MainActivity.this, currentList, waitingList,  countGames);
            tinydb.putInt("PLAYER_SET", TWO_PLAYERS);
            this.PLAYER_SET = tinydb.getInt("PLAYER_SET", TWO_PLAYERS);

            tinydb.putBoolean("isFirstGame",false);
            this.isFirstGame = tinydb.getBoolean("isFirstGame",false);
            if (_currentList.size() == 4){
                _waitingList.add(0,_currentList.get(3));
                _waitingList.add(0,_currentList.get(2));
                // _currentList.add(_waitingList.get(0));
                // _currentList.add(_waitingList.get(1));
                // _waitingList.remove(0);
                // _waitingList.remove(0);
                _currentList.remove(2);
                _currentList.remove(2);

                adapterCurrent.notifyDataSetChanged();
                adapterWaiting.notifyDataSetChanged();

                iCountGames[0]++;
                // tvFirstGame.setText("" + iCountGames[0]);
                //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                saveArrayListTinyDB(mContext, _currentList, _waitingList, iCountGames[0]);
                // Toast.makeText(mContext, R.string.current_player_message + String.valueOf(PLAYER_SET), Toast.LENGTH_LONG).show();
                Toast.makeText(mContext,getString(R.string.current_player_message)+ PLAYER_SET, Toast.LENGTH_LONG ).show();
                Log.d("Change_PLAYER_SET_4", getString(R.string.current_player_message) + PLAYER_SET);

            }


        }else {
            Toast.makeText(getApplicationContext(),R.string.someting_went_wrong,Toast.LENGTH_LONG).show();
        }


    }

    public void saveArrayListTinyDB (Context mContext, ArrayList<String> currentList, ArrayList<String> waitingList, int count) {
        TinyDB tinydb = new TinyDB(mContext);

        tinydb.putListString("waitingListtinydb", waitingList);
        tinydb.putListString("currentListtinydb", currentList);
        tinydb.putInt("PLAYER_SET", this.PLAYER_SET);



        if(waitingList.size()<4 || currentList.size()<4){count =0;}

        tinydb.putInt("count_games", count);
        tinydb.putBoolean("isGameSaved",true);
        tinydb.putBoolean("isFirstGame",isFirstGame);

        SharedPreferences sp = getSharedPreferences(getPackageName(), 0);
        String current = tinydb.getString("currentListtinydb");
       // Toast.makeText(OnlineGameActivity.this,"current: "+current, Toast.LENGTH_LONG).show();
        if ( null != current && 0 != current.compareTo("")){
            //current = " ";
        }
        String waiting = tinydb.getString("waitingListtinydb");
        //Toast.makeText(OnlineGameActivity.this,"waiting"+waiting, Toast.LENGTH_LONG).show();

        if ( null != waiting && 0 != waiting.compareTo("")){
            //waiting = " ";
        }
        String eventid = sp.getString("eventid",null);
        //Toast.makeText(OnlineGameActivity.this,"eventid"+eventid, Toast.LENGTH_LONG).show();


        Log.d("B_eventid!", eventid);
        Log.d("B_current!", current);
        Log.d("B_waiting!", waiting);

        new SaveOnline(eventid,current,waiting).execute();
    }

    public void loadArrayListTinyDB (Context mContext, ArrayList<String> currentList, ArrayList<String> waitingList) {
        TinyDB tinydb = new TinyDB(mContext);

        this.waitingList = tinydb.getListString("waitingListtinydb");
        this.currentList = tinydb.getListString("currentListtinydb");

        if(this.currentList.size() > 2){
            tinydb.putInt("PLAYER_SET", FOUR_PLAYERS);
        }else if(this.currentList.size() == 2){
            tinydb.putInt("PLAYER_SET", TWO_PLAYERS);
        }else{
            tinydb.putInt("PLAYER_SET", DEFAULT);
        }
        this.PLAYER_SET = tinydb.getInt("PLAYER_SET", DEFAULT);
        Log.d("PLAYER_SET",""+PLAYER_SET );
        Log.d("currentList.size()",""+this.currentList.size() );
        countGames = tinydb.getInt("count_games", countGames);
        this.isFirstGame = tinydb.getBoolean("isFirstGame",false);

    }

    public void initAdMob(Context mContext){
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                //beginPlayingGame();
            }
        });

        requestNewInterstitial();

    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .addTestDevice(getString(R.string.device_id))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void shareData(ArrayList<String> _current,ArrayList<String> _waiting){

        String sCurrent = getString(R.string.current)+":\n";
        String sWaiting = getString(R.string.waiting)+":\n";
        String sTotal = "";

        for (int c =0 ; c< _current.size(); c++){
            sCurrent += _current.get(c)+"\n";
        }


        for (int w =0 ; w< _waiting.size(); w++){
            sWaiting += _waiting.get(w)+"\n";
        }

        sTotal = sCurrent + sWaiting + "\n";
        sTotal += getString(R.string.downloadapp) ;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sTotal);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        SharedPreferences sp = getSharedPreferences("com.ahmadssb.queue", 0);
        String current = sp.getString("currentListtinydb",null);
        String waiting = sp.getString("waitingListtinydb",null);

        String mFullData = (current + "<$!$>" + waiting);
        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{NdefRecord.createMime(
                        "application/com.ahmadssb.queue", mFullData.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                         */
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }


    public class SaveOnline extends AsyncTask<String, String, String> {

        String eventid, current,waiting;
        int status;
        public SaveOnline(String _evebtid, String _current, String _waiting){
            this.eventid = _evebtid;
            this.current = _current;
            this.waiting = _waiting;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OnlineGameActivity.this);
            pDialog.setMessage(getString(R.string.saving_data_online));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                Calendar calendar = Calendar.getInstance();
                java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());
                String timestamp = ourJavaTimestampObject.toString();
                Log.d("timestamp" ,timestamp);

                JSONParser jParser = new JSONParser();
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_EVENT_ID, this.eventid));
                params.add(new BasicNameValuePair(TAG_CURRENT, this.current));
                params.add(new BasicNameValuePair(TAG_WAITING, this.waiting));
                params.add(new BasicNameValuePair(TAG_TIMESTAMP, timestamp));

                Log.d("request!", "starting New Event");
                Log.d(TAG_EVENT_ID, eventid);
                Log.d(TAG_CURRENT, current);
                Log.d(TAG_WAITING, waiting);

                JSONObject json = jParser.makeHttpRequest(URL_REGISTER_USERSS, "POST", params);
                Log.d("Saving Data attempt", json.toString());

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
                Toast.makeText(OnlineGameActivity.this,file_url, Toast.LENGTH_LONG).show();
               // Toast.makeText(OnlineGameActivity.this,"current"+this.current, Toast.LENGTH_LONG).show();
               // Toast.makeText(OnlineGameActivity.this,"evebtid"+this.eventid, Toast.LENGTH_LONG).show();
                //Toast.makeText(OnlineGameActivity.this,"waiting"+this.waiting, Toast.LENGTH_LONG).show();

                if(this.status == 1) {
                    //startActivity(new Intent(OnlineGameActivity.this, OnlineGameActivity.class));
                    //finish();
                }


            }
        }

    }

    class LoadGame extends AsyncTask<String, String, String> {
        TinyDB tinydb = new TinyDB(OnlineGameActivity.this);
        int status;

        SharedPreferences sp = getSharedPreferences(getPackageName(), 0);
        String eventid = sp.getString("eventid",null);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OnlineGameActivity.this);
            pDialog.setMessage(getString(R.string.loading_data_online));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... args) {

            alUsersList = new ArrayList<HashMap<String, String>>();

            try {

                JSONParser jParser = new JSONParser();
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_EVENT_ID, eventid));

                Log.d("request!", "starting New Event");
                Log.d(TAG_EVENT_ID, eventid);

                JSONObject json = jParser.makeHttpRequest(URL_USERS_LIST, "POST", params);
                Log.d("Loading Data attempt", json.toString());

                this.status = json.getInt(TAG_SUCCESS);

                if(this.status == 1){
                    Log.d("status = 1: ", json.toString());

                    try {

                        jsonActiveUsersList = json.getJSONArray(TAG_ARRAY_ACTIVE_USERS);

                        // looping through all active_users  according to the json object returned
                        for (int i = 0; i < jsonActiveUsersList.length(); i++) {
                            JSONObject c = jsonActiveUsersList.getJSONObject(i);

                            //gets the content of each tag
                            String current = c.getString(TAG_USER_NAME);
                            Log.d("Curent Name:",current);
                            tinydb.putString("currentListtinydb", current);
                        }

                        jsonWaitingUsersList = json.getJSONArray(TAG_ARRAY_WAITING_USERS);

                        // looping through all active_users  according to the json object returned
                        for (int i = 0; i < jsonWaitingUsersList.length(); i++) {
                            JSONObject c = jsonWaitingUsersList.getJSONObject(i);

                            //gets the content of each tag
                            String waiting = c.getString(TAG_USER_NAME);
                            Log.d("Waiting Name:",waiting);
                            tinydb.putString("waitingListtinydb", waiting);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    loadArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList);

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
            //Toast.makeText(OnlineGameActivity.this,file_url, Toast.LENGTH_LONG).show();

            if(PLAYER_SET == TWO_PLAYERS && currentList.size() == 4){
                waitingList.add(0,currentList.get(3));
                waitingList.add(0,currentList.get(2));

                currentList.remove(2);
                currentList.remove(2);
                tinydb.putInt("PLAYER_SET", TWO_PLAYERS);

            } else if(PLAYER_SET == FOUR_PLAYERS && currentList.size() == 2 && waitingList.size() >= 2){
                currentList.add(waitingList.get(0));
                currentList.add(waitingList.get(1));

                waitingList.remove(0);
                waitingList.remove(0);

                tinydb.putInt("PLAYER_SET", FOUR_PLAYERS);

            }else if(PLAYER_SET == FOUR_PLAYERS && currentList.size() == 2 && waitingList.size() < 2){
                waitingList.add(0,currentList.get(1));
                waitingList.add(0,currentList.get(0));
                currentList.remove(0);
                currentList.remove(0);
                tinydb.putInt("PLAYER_SET", DEFAULT);
            }
            PLAYER_SET = tinydb.getInt("PLAYER_SET", DEFAULT);
            adapterCurrent.notifyDataSetChanged();
            adapterWaiting.notifyDataSetChanged();
        }
    }

    class RemoveEventUsers extends AsyncTask<String, String, String> {
        TinyDB tinydb = new TinyDB(OnlineGameActivity.this);
        int status;

        SharedPreferences sp = getSharedPreferences(getPackageName(), 0);
        String eventid = sp.getString("eventid",null);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OnlineGameActivity.this);
            pDialog.setMessage(getString(R.string.removing_event_users));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... args) {

            alUsersList = new ArrayList<HashMap<String, String>>();

            try {

                JSONParser jParser = new JSONParser();
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_EVENT_ID, eventid));

                Log.d("request!", "starting New Event");
                Log.d(TAG_EVENT_ID, eventid);

                JSONObject json = jParser.makeHttpRequest(URL_REMOVE_EVENT_USERSS, "POST", params);
                Log.d("Loading Data attempt", json.toString());

                this.status = json.getInt(TAG_SUCCESS);

                if(this.status == 1){
                    Log.d("status = 1: ", json.toString());
                    currentList.clear();
                    waitingList.clear();
                    loadArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList);

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
            //Toast.makeText(OnlineGameActivity.this,file_url, Toast.LENGTH_LONG).show();
            new LoadGame().execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_online_game, menu);
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

            final AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGameActivity.this);
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
        }else if(id == R.id.action_load){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            isGameSaved = pref.getBoolean("isGameSaved", false);
            Log.d("isGameSaved", "" + isGameSaved);
            new LoadGame().execute();
        }else if(id == R.id.action_switch_game){
            selectGameSet(OnlineGameActivity.this, currentList, waitingList, countGames);
        }else if(id == R.id.action_insert){
            dialog = new Dialog(OnlineGameActivity.this);
            dialog.setContentView(R.layout.layout);
            dialog.findViewById(R.id.button_cancel).setOnClickListener(
                    OnlineGameActivity.this);
            dialog.findViewById(R.id.cbSpecialCase).setOnClickListener(OnlineGameActivity.this);
            dialog.findViewById(R.id.button_ok).setOnClickListener(
                    OnlineGameActivity.this);
            dialog.show();
        }else if(id == R.id.action_next_game){
            TinyDB tinydb = new TinyDB(OnlineGameActivity.this);
            countGames = tinydb.getInt("count_games", countGames);
            if(countGames == mAdGameCount[0] || countGames == mAdGameCount[1]
                    || countGames == mAdGameCount[2] || countGames == mAdGameCount[3] ){


            }else{

            }
            PLAYER_SET = tinydb.getInt("PLAYER_SET", DEFAULT);
            if(PLAYER_SET == TWO_PLAYERS){
                setGame2Players(OnlineGameActivity.this, currentList, waitingList, countGames);

            }else if(PLAYER_SET == FOUR_PLAYERS){
                setGame4Players(OnlineGameActivity.this, currentList, waitingList,  countGames);
            }else {
                Toast.makeText(getApplicationContext(),R.string.someting_went_wrong,Toast.LENGTH_LONG).show();
            }

        }else if(id == R.id.action_share){
            shareData(currentList,waitingList);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
/*
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
*/
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isGameSaved = pref.getBoolean("isGameSaved", false);
        Log.d("isGameSaved", ""+isGameSaved);

        //Begining of AdMob Configiration

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                //beginPlayingGame();
            }
        });

        requestNewInterstitial();


        //End of AdMob Configiration

//        lvCurrent = (ListView) findViewById(R.id.lvCurrent);
//        lvWaiting = (ListView) findViewById(R.id.lvWaiting);


        gvCurrent = (GridView) findViewById(R.id.gvCurrent);
        gvWaiting = (GridView) findViewById(R.id.gvWaiting);


        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnRemove = (Button) findViewById(R.id.btnGame);
        btnRemoveUsers =  (Button) findViewById(R.id.btnRemoveUsers);
        btnLoad = (Button) findViewById(R.id.btnLoad);

        findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareData(currentList,waitingList);
            }
        });

        tvFirstGame = (TextView) findViewById(R.id.tvP1P3vsP2P4);
        tvFirstGame.setVisibility(View.GONE);

        tvP1P3vsP2P4 = (TextView) findViewById(R.id.tvP1P3vsP2P4);
        tvP1P3vsP2P4.setVisibility(View.GONE);

        currentList = new ArrayList<String>();
        waitingList = new ArrayList<String>();

        adapterCurrent = new MyAdapterCurrent();
        adapterWaiting = new MyAdapterWaiting();

        final TinyDB tinydb = new TinyDB(OnlineGameActivity.this);

        //selectGameSet(OnlineGameActivity.this,currentList,waitingList,countGames);



        tvCurrent = (TextView) findViewById(R.id.textView);
        tvCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareData(currentList, waitingList);
            }
        });


        findViewById(R.id.btnRemoveUsers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemoveEventUsers().execute();
            }
        });

        findViewById(R.id.btnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                isGameSaved = pref.getBoolean("isGameSaved", false);
                Log.d("isGameSaved", "" + isGameSaved);
                new LoadGame().execute();



            }
        });


        gvWaiting.setAdapter(adapterWaiting);
        gvWaiting.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGameActivity.this);
                builder.setTitle(R.string.alertTitleEditName);
                //builder.setMessage(R.string.msgEditName);
                final EditText etName = new EditText(OnlineGameActivity.this);
                etName.setText("");
                etName.setHint(getString(R.string.msgEditName));
                builder.setView(etName);
                Log.d("etName", etName.getText().toString());
                builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String text = etName.getText().toString();

                        if (null != text && 0 != text.compareTo("")) {
                            Log.d("etName", text);
                            waitingList.set(position, text);
                            dialog.dismiss();
                            adapterWaiting.notifyDataSetChanged();
                        }
                        //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                        saveArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList, countGames);
                    }
                });
                builder.setNeutralButton(R.string.position, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGameActivity.this);
                        builder.setTitle(getString(R.string.change_position));
                        //builder.setMessage(getString(R.string.change_position_message));
                        builder.setItems(waitingList.toArray(new String[waitingList.size()]), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                                //Toast.makeText(MainActivity.this, waitingList.get(item) + "-"+ item ,Toast.LENGTH_LONG).show();
                                Log.d("waitingList-item", waitingList.get(item) + "-" + item);
                                if (item > position) {

                                    waitingList.add(item, waitingList.get(position));
                                    //waitingList.add(position, waitingList.get(item));
                                    Log.d("waitingList-items", waitingList.toString());
                                    waitingList.remove(position);
                                } else if (item < position) {
                                    waitingList.add(item, waitingList.get(position));
                                    waitingList.remove(position + 1);
                                }

                                adapterWaiting.notifyDataSetChanged();
                                //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                                saveArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList, countGames);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                });

                builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        waitingList.remove(position);
                        adapterWaiting.notifyDataSetChanged();
                        //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                        saveArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList, countGames);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        gvCurrent.setAdapter(adapterCurrent);
        gvCurrent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGameActivity.this);
                builder.setTitle(R.string.alertTitleEditName);
                //builder.setMessage(R.string.msgEditName);
                final EditText etName = new EditText(OnlineGameActivity.this);
                etName.setText("");
                etName.setHint(getString(R.string.msgEditName));
                builder.setView(etName);
                Log.d("etName", etName.getText().toString());
                builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String text = etName.getText().toString();

                        if (null != text && 0 != text.compareTo("")) {
                            Log.d("etName", text);
                            currentList.set(position, text);
                            dialog.dismiss();
                            adapterCurrent.notifyDataSetChanged();
                        }


                        //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                        saveArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList, countGames);
                    }
                });


                builder.setNeutralButton(R.string.position, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGameActivity.this);
                        builder.setTitle(getString(R.string.change_position));
                        //builder.setMessage(getString(R.string.change_position_message));
                        builder.setItems(currentList.toArray(new String[currentList.size()]), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                                //Toast.makeText(MainActivity.this, waitingList.get(item) + "-"+ item ,Toast.LENGTH_LONG).show();
                                Log.d("waitingList-item", currentList.get(item) + "-" + item);
                                if (item > position) {

                                    currentList.add(item, currentList.get(position));
                                    //waitingList.add(position, waitingList.get(item));
                                    Log.d("currentList-items", currentList.toString());
                                    currentList.remove(position);
                                } else if (item < position) {
                                    currentList.add(item, currentList.get(position));
                                    currentList.remove(position + 1);
                                }

                                adapterCurrent.notifyDataSetChanged();
                                //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                                saveArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList, countGames);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                });


                builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        waitingList.add(currentList.get(position));
                        currentList.remove(position);

                        if (position == 0) {
                            currentList.add(1, waitingList.get(0));
                        } else if (position == 2) {
                            currentList.add(3, waitingList.get(0));
                        } else {
                            currentList.add(position, waitingList.get(0));
                        }

                        waitingList.remove(0);

                        adapterCurrent.notifyDataSetChanged();
                        adapterWaiting.notifyDataSetChanged();
                        //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                        saveArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList, countGames);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


                return true;
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog = new Dialog(OnlineGameActivity.this);
                dialog.setContentView(R.layout.layout);
                dialog.findViewById(R.id.button_cancel).setOnClickListener(
                        OnlineGameActivity.this);
                dialog.findViewById(R.id.cbSpecialCase).setOnClickListener(OnlineGameActivity.this);
                dialog.findViewById(R.id.button_ok).setOnClickListener(
                        OnlineGameActivity.this);
                dialog.show();

            }
        });

        findViewById(R.id.btnGame).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                countGames = tinydb.getInt("count_games", countGames);
                if (countGames == mAdGameCount[0] || countGames == mAdGameCount[1]
                        || countGames == mAdGameCount[2] || countGames == mAdGameCount[3]) {


                } else {

                }
                TinyDB tinydb = new TinyDB(OnlineGameActivity.this);
                PLAYER_SET = tinydb.getInt("PLAYER_SET", DEFAULT);
                if (PLAYER_SET == TWO_PLAYERS) {
                    setGame2Players(OnlineGameActivity.this, currentList, waitingList, countGames);

                } else if (PLAYER_SET == FOUR_PLAYERS) {
                    setGame4Players(OnlineGameActivity.this, currentList, waitingList, countGames);
                } else {
                    selectGameSet(OnlineGameActivity.this, currentList, waitingList, countGames);
                }

            }
        });
        isGameSaved = pref.getBoolean("isGameSaved", false);
        Log.d("isGameSaved", "" + isGameSaved);
        new LoadGame().execute();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        final TinyDB tinydb = new TinyDB(OnlineGameActivity.this);
        tinydb.putInt("PLAYER_SET", DEFAULT);
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("AnalyticsApplication", "Setting screen name: " + getString(R.string.app_name));
        mTracker.setScreenName("Image~" + getString(R.string.app_name));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];


        SharedPreferences sp = getSharedPreferences("com.ahmadssb.queue", 0);
        SharedPreferences.Editor editor = sp.edit();


        String mFullData =  new String(msg.getRecords()[0].getPayload());

        String[] mData = mFullData.split("<$!$>");
        String current = mData[0];
        String waiting = mData[1];

        editor.putString("currentListtinydb", current);
        editor.putString("waitingListtinydb", waiting);


        new LoadGame().execute();
        if(PLAYER_SET == TWO_PLAYERS && currentList.size() == 4){
            waitingList.add(0,currentList.get(3));
            waitingList.add(0,currentList.get(2));

            currentList.remove(2);
            currentList.remove(2);
        } else if(PLAYER_SET == FOUR_PLAYERS && currentList.size() == 2 && waitingList.size() >= 2){
            currentList.add(waitingList.get(0));
            currentList.add(waitingList.get(1));

            waitingList.remove(0);
            waitingList.remove(0);
        }else if(PLAYER_SET == FOUR_PLAYERS && currentList.size() == 2 && waitingList.size() < 2){

            waitingList.add(0,currentList.get(1));
            waitingList.add(0,currentList.get(0));
            currentList.remove(0);
            currentList.remove(0);

        }

        adapterCurrent.notifyDataSetChanged();
        adapterWaiting.notifyDataSetChanged();

    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.button_cancel:
                dialog.dismiss();
                break;

            case R.id.button_ok:
                String text = ((EditText) dialog.findViewById(R.id.edit_box))
                        .getText().toString();
                cbSpecialCase = (CheckBox) dialog.findViewById(R.id.cbSpecialCase);
                boolean isRegistered = false;
                if (null != text && 0 != text.compareTo("")) {
                    for (int i = 1; i < waitingList.size(); i++) {
                        if (text.equals(waitingList.get(i))) {
                            isRegistered = true;
                            ((EditText) dialog.findViewById(R.id.edit_box)).setText(R.string.already_registered);
                            break;
                        }
                        for (int j = 0; j < currentList.size(); j++) {
                            if (text.equals(currentList.get(j))) {
                                ((EditText) dialog.findViewById(R.id.edit_box)).setText(R.string.already_registered);
                                isRegistered = true;
                                break;
                            }
                        }
                    }

                    if (!isRegistered) {
                        if (cbSpecialCase.isChecked()) {
                            waitingList.add(1, text);
                            dialog.dismiss();
                            adapterWaiting.notifyDataSetChanged();
                        } else {
                            waitingList.add(text);
                            dialog.dismiss();
                            adapterWaiting.notifyDataSetChanged();
                        }
                    }

                    //saveArrayList(MainActivity.this, currentList, waitingList, countGames);
                    saveArrayListTinyDB(OnlineGameActivity.this, currentList, waitingList, countGames);

                }
                break;
        }
    }

    private class MyAdapterCurrent extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return currentList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return currentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (null == view) {
                view = new TextView(OnlineGameActivity.this);
                view.setPadding(10, 10, 10, 10);
                view.setTextSize(24);
            }
            view.setText(position + 1 + ". " + currentList.get(position));
            return view;
        }
    }

    private class MyAdapterWaiting extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return waitingList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return waitingList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            TextView view = (TextView) convertView;
            if (null == view) {
                view = new TextView(OnlineGameActivity.this);
                view.setPadding(10, 10, 10, 10);
                view.setTextSize(24);
            }
            view.setText(position + 1 + ". " + waitingList.get(position));
            return view;
        }
    }



}
