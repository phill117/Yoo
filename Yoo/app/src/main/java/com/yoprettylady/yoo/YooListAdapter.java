package com.yoprettylady.yoo;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sean on 3/25/2015.
 */
public class YooListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<String> usernames;
    Typeface montBold;

    public YooListAdapter(ArrayList<String> usernames, Context context){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.usernames = usernames;
        montBold = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat-Bold.otf");
    }

    @Override
    public int getCount() {
        return usernames.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = inflater.inflate(R.layout.listitems_layout, parent, false);
        final TextView name = (TextView)convertView;//.findViewById(R.id.username);
        name.setText((String)getItem(position));
        name.setTypeface(montBold);
        //name.setClickable(true);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return usernames.get(position);
    }

}
