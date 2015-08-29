package com.ahmadssb.queue;

/**
 * Created by Ahmed on 8/2/2015.
 */
        import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
    private Context context;



    public ConnectionDetector(Context _context){
        this.context = _context;

    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null){
            NetworkInfo[] networkInfo = connectivity.getAllNetworkInfo();
            if(networkInfo != null){
                for(int i = 0; i<networkInfo.length; i++){
                    if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED ){
                        return true;
                    }

                }
            }
        }

        return false;
    }
}
