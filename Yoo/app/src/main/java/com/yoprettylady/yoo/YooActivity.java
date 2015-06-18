package com.yoprettylady.yoo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class YooActivity extends ActionBarActivity {

    public static OkHttpClient client = new OkHttpClient();
    final static private String API_TOKEN = "09f7f2f3-7c52-4a99-9010-d4326867f5d0";
    final static private String MY_USERNAME = "PHILL117";
    static final String ACTION_ATTACHED = "android.accessory.device.action.ATTACHED";
    static final String ACTION_DETACHED = "android.accessory.device.action.DETACHED";
    private SAPServiceProvider yooProvider = null;
    private boolean mIsBound = false;
    ListView listView;
    LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoo);

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ArrayList<String> usernames = new ArrayList<>();
        new FriendAsyncTask().execute();
        //for(String s : usernames)Log.e("FWENDList",s);

        listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(new YooListAdapter(usernames, this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendYo(((TextView) view).getText().toString());
            }
        });

        doBindService();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ATTACHED);
        filter.addAction(ACTION_DETACHED);
        registerReceiver(mBroadcastReceiver, filter);

        //startConnection();

//        Button yoButton = ((Button)findViewById(R.id.yo));
//        yoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //sendYo("<username>");
//                //getSubscriberCount();
//                userExists("PHILL117");
//            }
//        });
//        yoButton.setTypeface(montBold);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        closeConnection();
        doUnbindService();
        super.onDestroy();
    }

    void doBindService() {
        mIsBound = bindService(new Intent(YooActivity.this,
                SAPServiceProvider.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    private void startConnection() {
        if (mIsBound  && yooProvider != null) {
            yooProvider.findPeers();
        }
    }
    private void closeConnection() {
        if (mIsBound && yooProvider != null) {
            yooProvider.closeConnection();
        }
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_ATTACHED.equals(action)) {
                doBindService();
            } else if (ACTION_DETACHED.equals(action)) {
                closeConnection();
                doUnbindService();
            }
        }
    };

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            yooProvider = ((SAPServiceProvider.LocalBinder) service).getService();
            //yooProvider.findPeers();
        }
        @Override
        public void onServiceDisconnected(ComponentName className) {
            yooProvider = null;
            mIsBound = false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yoo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_friend) {

            final Dialog dialog = new Dialog(this);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.add_friend_layout,null);
            view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Contact
                    //check for success
                        //if success,
                            //show toast or something
                            //dialog.dismiss();
                        //else try again
                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setContentView(view);
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * YO METHODS
     */

    public static void sendYo(String username) {
        final String user = username;
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    FormEncodingBuilder builder = new FormEncodingBuilder();
                    builder.add("api_token",API_TOKEN).add("username",user);
                    RequestBody body = builder.build();
                    Request request = new Request.Builder()
                            .url("http://api.justyo.co/yo/")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    Log.e("RESULT OF SEND YO!",response.body().string());
                }catch (Exception e){
                    Log.e("ERROR","Failed to Send Yo");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void getSubscriberCount(){
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://api.justyo.co/subscribers_count?api_token="+API_TOKEN)
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    Log.e("RESULT",response.body().string());
                }catch (Exception e){
                    Log.e("ERROR","Failed to get subscriber count");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    ArrayList<String> getFriendList(){

        ArrayList<String> friendlist = new ArrayList<>();
        Log.e("GTTING THE FIRENDS0","HIR");

            try {

                FormEncodingBuilder builder = new FormEncodingBuilder();
                builder.add("api_token",API_TOKEN).add("username",MY_USERNAME);
                RequestBody body = builder.build();

                Request request = new Request.Builder()
                        .url("https://api.justyo.co/rpc/get_followers")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();

                String responseStr = response.body().string();
                JSONObject jsonObject = new JSONObject(responseStr);
                JSONArray friends = jsonObject.getJSONArray("followers");

                Log.e("RESULT 4 firends",responseStr);
                for(int i = 0; i < friends.length(); i++) friendlist.add(friends.getString(i));

            }catch (Exception e){
                Log.e("ERROR","Failed to populate list");
                e.printStackTrace();
            }

        return friendlist;
    }

    void userExists(String username){
        final String user = username;
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://api.justyo.co/check_username?api_token="+API_TOKEN+"&username="+user)
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    Log.e("RESULT",response.body().string());
                }catch (Exception e){
                    Log.e("ERROR","Failed to Send Yo");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class FriendAsyncTask extends AsyncTask<Void,Void,ArrayList<String>>{
        @Override

        protected ArrayList<String> doInBackground(Void... params) {
           return getFriendList();
        }

        @Override
        protected void onPostExecute(ArrayList<String> friendList) {
            ((YooListAdapter)listView.getAdapter()).usernames = friendList;
            ((YooListAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

}
