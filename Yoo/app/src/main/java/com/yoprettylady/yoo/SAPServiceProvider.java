package com.yoprettylady.yoo;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

/**
 * Created by Sean on 3/8/2015.
 */
public class SAPServiceProvider extends SAAgent{

    public final static String TAG = "SAPServiceProvider";
    public final static int INCOMING_CHANNEL_ID = 123;
    public final static int OUTGOING_CHANNEL_ID = 234;
    private final IBinder mIBinder = new LocalBinder();
    static YooProviderSocket currentConnection = null;

    public SAPServiceProvider(){
        super(TAG,YooProviderSocket.class);
    }

    //callback for SAAgent.findPeerAgents()
    @Override
    protected void onFindPeerAgentResponse(SAPeerAgent peerAgent, int result) {
        if(result == PEER_AGENT_FOUND) {
            if(matchesMe(peerAgent)){}//TODO fix this when you know what it reads
            requestServiceConnection(peerAgent);
        } else if(result == FINDPEER_DEVICE_NOT_CONNECTED){
            Log.i(TAG, "Peer Agents are not found, no accessory device connected.");
        } else if(result == FINDPEER_SERVICE_NOT_FOUND ) {
            Log.i(TAG, "No matching service on connected accessory.");
        }
    }

    private boolean matchesMe(SAPeerAgent peerAgent){
        if(peerAgent.getAppName().equals("Yoo")) Log.e(TAG,"the peeragent's id: " +peerAgent.getPeerId());
        return false;
    }

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket thisConnection, int result) {
        if(result == CONNECTION_SUCCESS){
            if(thisConnection != null){
                currentConnection = (YooProviderSocket) thisConnection;

                Toast.makeText(getBaseContext(),"CONNECTION ESTABLIHED", Toast.LENGTH_LONG).show();
            }else Log.e(TAG, "SASocket object is null");
        }
        else if(result == CONNECTION_ALREADY_EXIST) Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
        else Log.e(TAG, "onSeviceConnectionResponse result error = " + result);

    }

    @Override
    protected void onPeerAgentUpdated(SAPeerAgent peerAgent, int result) {
        if(result == PEER_AGENT_AVAILABLE) {
            requestServiceConnection(peerAgent);
        } else if (result == PEER_AGENT_UNAVAILABLE) {
            Log.i(TAG,"Peer Agent no longer available:" + peerAgent.getAppName());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    public void findPeers() {
        findPeerAgents();
    }

    //initialize the accessory framework (with SA.initialize())
    @Override
    public void onCreate() {
        super.onCreate();

        SA mAccessory = new SA();
        try{
            mAccessory.initialize(this);
            Log.d("SAP Provider", "oncreate try block");
        } catch (SsdkUnsupportedException e){
            Log.d("SAP PROVIDER", "on create try block error unsuported sdk");
        } catch (Exception e1){
            Log.e(TAG, "Cannot initialize Accessory package");
            e1.printStackTrace();
            stopSelf();
        }
    }

    //callback for handling connection requests (always accept)
    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        Log.e("     onServiceConnectionRequested    ", "accepting service...");
        acceptServiceConnectionRequest(peerAgent);
    }

    public static void closeConnection(){
        if(currentConnection != null){
            currentConnection.close();
            currentConnection = null;
        }
    }

    public class LocalBinder extends Binder {
        public SAPServiceProvider getService(){
            return SAPServiceProvider.this;
        }
    }

}
