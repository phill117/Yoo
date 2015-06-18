package com.yoprettylady.yoo;

import android.util.Log;

import com.samsung.android.sdk.accessory.SASocket;

/**
 * Created by Sean on 5/14/2015.
 */
public class YooProviderSocket extends SASocket {

    public final static String TAG = "SAPServiceProvider";

    public YooProviderSocket(){
        super(YooProviderSocket.class.getName());
    }

    @Override
    public void onError(int channelID, String errorString, int errorCode) {
        Log.e(TAG, "ERROR: " + errorString + " | " + errorCode);
    }

    @Override
    public void onReceive(int channelID, byte[] data) {

        //will be the username
        final String message = new String(data);
        Log.e("SAP MESSAGE",message);

        YooActivity.sendYo(message);

    }

    @Override
    protected void onServiceConnectionLost(int i) {
        SAPServiceProvider.closeConnection();
        //currentConnection = null;
    }

}
