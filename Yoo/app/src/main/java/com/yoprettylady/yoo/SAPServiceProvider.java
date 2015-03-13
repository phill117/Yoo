package com.yoprettylady.yoo;

import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Sean on 3/8/2015.
 */
public class SAPServiceProvider extends SAAgent{

    public final static String TAG = "SAPServiceProvider";
    public final static int SAP_SERVICE_CHANNEL_ID = 123;
    private final IBinder mIBinder = new LocalBinder();
    static HashMap<Integer, SAPServiceProviderConnection> connectionMap = null;

    public SAPServiceProvider(){
        super(TAG,SAPServiceProviderConnection.class);
    }

    @Override
    protected void onFindPeerAgentResponse(SAPeerAgent saPeerAgent, int i) {

    }

    @Override
    protected void onServiceConnectionResponse(SASocket thisConnection, int result) {
        if(result == CONNECTION_SUCCESS){
            if(thisConnection != null){
                SAPServiceProviderConnection myConnection = (SAPServiceProviderConnection) thisConnection;

                if(connectionMap == null){
                    connectionMap  = new HashMap<Integer, SAPServiceProviderConnection>();
                }

                myConnection.connectionID = (int) (System.currentTimeMillis() & 255);

                Log.d(TAG, "onServiceConnection connectionID = " + myConnection.connectionID);
                Toast.makeText(getBaseContext(),"CONNECTION ESTABLIHED", Toast.LENGTH_LONG).show();
            }else{
                Log.e(TAG, "SASocket object is null");
            }
        }else if(result == CONNECTION_ALREADY_EXIST){
            Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
        }else{
            Log.e(TAG, "onSeviceConnectionResponse result error = " + result);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SA mAcessory = new SA();
        try{
            mAcessory.initialize(this);
            Log.d("SAP Provider", "oncreate try block");
        } catch (SsdkUnsupportedException e){
            Log.d("SAP PROVIDER", "on create try block error unsuported sdk");
        } catch (Exception e1){
            Log.e(TAG, "Cannot initialize Accessory package");
            e1.printStackTrace();
            stopSelf();
        }
    }

    static public String getDeviceInfo(){
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return manufacturer + " " + model;
    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        acceptServiceConnectionRequest(peerAgent);
    }

    public class LocalBinder extends Binder {
        public SAPServiceProvider getService(){
            return SAPServiceProvider.this;
        }
    }

    public static class SAPServiceProviderConnection extends SASocket{

        private int connectionID;

        SAPServiceProviderConnection(){
            super(SAPServiceProviderConnection.class.getName());
        }

        @Override
        public void onError(int channelID, String errorString, int errorCode) {
            Log.e(TAG,"ERROR: "+errorString+ " | " + errorCode);
        }

        @Override
        public void onReceive(int channelID, byte[] data) {
            final String message;
            Time time = new Time();
            time.set(System.currentTimeMillis());
            String timeStr = " " + String.valueOf(time.minute) + ":" + String.valueOf(time.second);
            String strToUpdateUI = new String(data);
            message = getDeviceInfo() + strToUpdateUI.concat(timeStr);

            Log.d("SAP MESSAGE",message);

            final SAPServiceProviderConnection uHandler = connectionMap.get(Integer.parseInt(String.valueOf(connectionID)));

            if(uHandler == null){
                Log.e(TAG,"Error, can not get SAPServiceProviderConnection handler");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        uHandler.send(SAP_SERVICE_CHANNEL_ID, message.getBytes());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        protected void onServiceConnectionLost(int i) {
            if(connectionMap != null){
                connectionMap.remove(connectionID);
            }
        }
    }
}
