package com.example.admin.webrtccall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.admin.webrtccall.MainActivity;
import com.example.admin.webrtccall.R;
import com.example.admin.webrtccall.model.HistoryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 7/25/2016.
 */
public class HistoryAdapter extends ArrayAdapter<HistoryItem> {
    private final Context context;
    private LayoutInflater inflater;
    private List<HistoryItem> values = new ArrayList<>();
    private ArrayList<HistoryItem> items;
    public static String i;

    public HistoryAdapter(Context context, ArrayList<HistoryItem> values) {
        super(context, R.layout.item_history, android.R.id.text1, values);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.values = values;
        this.items = values;
    }

    class ViewHolder {
        TextView user;
        TextView id;
        HistoryItem histItem;
        CheckBox checkBox;
    }

    public List<HistoryItem> getSelected(){
        return values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HistoryItem hItem = this.values.get(position);
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_history, parent, false);
            holder.user = (TextView) convertView.findViewById(R.id.opponentsName);
            holder.id = (TextView) convertView.findViewById(R.id.history_time);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.opponentsCheckBox);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.user.setText(hItem.getUserName());
        holder.id.setText(hItem.getUserId());

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(values.contains(hItem));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    i = hItem.getUserId();
                    values.add(hItem);
                    ((MainActivity) getContext()).makeCall(holder.id.getText().toString());
                } else {
                    if (i == hItem.getUserId()) {
                        i = String.valueOf(0);
                    }
                    values.remove(hItem);
                }
            }
        });

        holder.histItem = hItem;
        return convertView;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

}
