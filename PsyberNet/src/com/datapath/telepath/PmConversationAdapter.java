package com.datapath.telepath;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

//converts items to appropriate layout for self or other type in pmConversation
public class PmConversationAdapter extends BaseAdapter{
	private static final int TYPE_SELF = 0;
	private static final int TYPE_OTHER = 1;

	final List<ArrayList<String>> pmItems;

	private LayoutInflater mInflater;

	String self;

	PmConversationAdapter(List<ArrayList<String>> pmItems, LayoutInflater inflater, String self) {
		this.pmItems = pmItems;
		mInflater = inflater;
		this.self = self;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		ArrayList<String> item = pmItems.get(position);

		if (item.get(0).equals(self)){
			return TYPE_SELF;
		}
		else{
			return TYPE_OTHER;
		}
	}
	@Override
	public int getCount() {
		return pmItems.size();
	}
	@Override
	public Object getItem(int position) {
		return pmItems.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		PmViewHolder holder = null;
		int type = getItemViewType(position);

		if (convertView == null) {

			holder = new PmViewHolder();

			if (type==TYPE_SELF){
				convertView = mInflater.inflate(R.layout.pm_self_row, null);
			}
			else{
				convertView = mInflater.inflate(R.layout.pm_other_row, null);
			}

			holder.nameView = (TextView) convertView.findViewById(R.id.pm_name);
			holder.contentView = (TextView) convertView.findViewById(R.id.pm_content);
			holder.dateView = (TextView) convertView.findViewById(R.id.pm_date);

			convertView.setTag(holder);
		}
		else{
			holder = (PmViewHolder)convertView.getTag();
		}

		ArrayList<String> dataPopulator = pmItems.get(position);

		holder.nameView.setText(dataPopulator.get(0));
		holder.contentView.setText(dataPopulator.get(1));
		holder.dateView.setText(dataPopulator.get(2));

		return convertView;
	}

	public static class PmViewHolder{

		TextView nameView;
		TextView contentView;
		TextView dateView;
	}
}
