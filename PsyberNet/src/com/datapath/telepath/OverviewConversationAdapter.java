package com.datapath.telepath;

import com.datapath.telepath.R;
import com.datapath.telepath.R.id;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//supposed to take overview information and convert it to truncated for quick view
public class OverviewConversationAdapter extends ArrayAdapter<OverviewConversationItem>{

    Context context; 
    int rowTemplateId;    
    OverviewConversationItem[] items = null;
    
    //what is going on here
    //why is everything a bulletin adapter, where is the conversationAdapter?
    //am I really that stupid? or is it lazy?
    
    //previous comments left over, similar bug still somewhere present, mentioned in BulletinAdapter
    
    public OverviewConversationAdapter(Context context, int rowTemplateId, OverviewConversationItem[] items) {
        super(context, rowTemplateId, items);
        this.rowTemplateId = rowTemplateId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ConversationHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(rowTemplateId, parent, false);
            
            holder = new ConversationHolder();
            holder.overviewConversationPoster = (TextView)row.findViewById(R.id.overviewConversationPoster);
            holder.overviewConversationDate = (TextView)row.findViewById(R.id.overviewConversationDate);
            holder.overviewConversationTopic = (TextView)row.findViewById(R.id.overviewConversationTopic);
            holder.overviewConversationContent = (TextView)row.findViewById(R.id.overviewConversationContent);
            
            
            row.setTag(holder);
        }
        else
        {
            holder = (ConversationHolder)row.getTag();
        }
        
        OverviewConversationItem conversationItem = items[position];
        holder.overviewConversationDate.setText(conversationItem.time);
        holder.overviewConversationPoster.setText(conversationItem.parent);
        holder.overviewConversationTopic.setText(conversationItem.topic);
        holder.overviewConversationContent.setText(conversationItem.textPreview);
        
        return row;
    }
    
    static class ConversationHolder
    {
        TextView overviewConversationPoster;
        TextView overviewConversationDate;
        TextView overviewConversationTopic;
        TextView overviewConversationContent;
    }
}