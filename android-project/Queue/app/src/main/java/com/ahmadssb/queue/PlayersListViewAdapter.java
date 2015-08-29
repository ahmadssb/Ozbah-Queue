package com.ahmadssb.queue;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class PlayersListViewAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater layoutInflater;
    private ArrayList<String> arrayList;


    public PlayersListViewAdapter(Activity activity, ArrayList<String> arrayList){
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.single_text_list_layout, null);


        TextView player1 = (TextView) convertView.findViewById(R.id.tvPlayer1);
        TextView player2 = (TextView) convertView.findViewById(R.id.tvPlayer2);


        player1.setText(arrayList.get(position));

        player2.setText(arrayList.get(position+1));

        return convertView;
    }
}
