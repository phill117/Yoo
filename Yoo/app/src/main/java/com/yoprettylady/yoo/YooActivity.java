package com.yoprettylady.yoo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class YooActivity extends ActionBarActivity {

    OkHttpClient client = new OkHttpClient();
    final private String API_TOKEN = "09f7f2f3-7c52-4a99-9010-d4326867f5d0";
    LayoutInflater inflater;
    Typeface montBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoo);
        montBold = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat-Bold.otf");
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Button yoButton = ((Button)findViewById(R.id.yo));
        yoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendYo("<username>");
                //getSubscriberCount();
                userExists("PHILL117");
            }
        });
        yoButton.setTypeface(montBold);
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
                        //if succes,
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
