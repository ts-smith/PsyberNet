package com.datapath.telepath;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.datapath.telepath.R;
import com.datapath.telepath.R.id;

public class OverviewBulletinAdapter extends ArrayAdapter<OverviewBulletinItem>{

	//BUG
	//I am guessing everything is being routed through the bulletin adapter for some reason
	
    Context context; 
    int rowTemplateId;    
    OverviewBulletinItem[] items = null;
    
    public OverviewBulletinAdapter(Context context, int rowTemplateId, OverviewBulletinItem[] items) {
        super(context, rowTemplateId, items);
        this.rowTemplateId = rowTemplateId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BulletinHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(rowTemplateId, parent, false);
            
            holder = new BulletinHolder();
            holder.overviewBulletinPoster = (TextView)row.findViewById(R.id.overviewBulletinPoster);
            holder.overviewBulletinDate = (TextView)row.findViewById(R.id.overviewBulletinDate);
            holder.overviewBulletinTopic = (TextView)row.findViewById(R.id.overviewBulletinTopic);
            holder.overviewBulletinContent = (TextView)row.findViewById(R.id.overviewBulletinContent);
            
            
            row.setTag(holder);
        }
        else
        {
            holder = (BulletinHolder)row.getTag();
        }
        
        OverviewBulletinItem bulletinItem = items[position];
        holder.overviewBulletinDate.setText(bulletinItem.time);
        holder.overviewBulletinPoster.setText(bulletinItem.parent);
        holder.overviewBulletinTopic.setText(bulletinItem.topic);
        holder.overviewBulletinContent.setText(bulletinItem.textPreview);
        
        return row;
    }
    
    static class BulletinHolder
    {
        TextView overviewBulletinPoster;
        TextView overviewBulletinDate;
        TextView overviewBulletinTopic;
        TextView overviewBulletinContent;
    }
}