package com.example.pracs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> subList;
    private HashMap<String, List<String>> pracItem;

    public CustomExpandableListViewAdapter(Context context, List<String> subList, HashMap<String, List<String>> pracItem) {
        this.context = context;
        this.subList = subList;
        this.pracItem = pracItem;
    }

    @Override
    public int getGroupCount() {
        return subList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return pracItem.get(subList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return subList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return pracItem.get(subList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String subTitle = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflate = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.sub_items, null);
        }
        TextView textView = convertView.findViewById(R.id.subTitle);
        textView.setText(subTitle);
        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String pracTitle = (String) getChild(groupPosition, childPosition);
        if(convertView == null) {
            LayoutInflater inflate = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.prac_item, null);
        }
        TextView textView = convertView.findViewById(R.id.pracTitle);
        textView.setText(pracTitle);

        convertView.setOnClickListener(v -> {
            moveToMain((String) getGroup(groupPosition), pracTitle);
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void moveToMain(String sub, String prac) {
        Intent MainIntent = new Intent(context, MainActivity.class);
        MainIntent.putExtra("subject", sub);
        MainIntent.putExtra("practical", prac);
        context.startActivity(MainIntent);
    }
}
