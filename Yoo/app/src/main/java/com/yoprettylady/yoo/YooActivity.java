package com.yoprettylady.yoo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class YooActivity extends ActionBarActivity {

    OkHttpClient client = new OkHttpClient();
    final private String API_TOKEN = "key here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoo);
        ((Button)findViewById(R.id.yo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendYo("<username>");
                //getSubscriberCount();
                userExists("<username>");
            }

        });
    }


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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void sendYo(String username) {
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
                    Log.e("RESULT",response.body().string());
                }catch (Exception e){
                    Log.e("ERROR","Failed to Send Yo");
                    e.printStackTrace();
                }
            }
        });
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
                    Log.e("ERROR","Failed to Send Yo");
                    e.printStackTrace();
                }
            }
        }).start();
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
}
