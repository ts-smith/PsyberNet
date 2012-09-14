package com.datapath.telepath;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datapath.telepath.ConversationFragment.UpdateSound;

/* Adapter for main conversation items.
 * Sets onClickListeners for each specific item type
 * Uses holder for efficiency
 */


class ConversationAdapter extends BaseAdapter {
	private static final int TYPE_PARENT = 0;
	private static final int TYPE_REPLY = 1;
	private static final int TYPE_LAUNCHREPLY = 2;

	final List<ConversationItem> conversationItems;

	private LayoutInflater mInflater;

	Context context;
	String regionName;

	ConversationAdapter(List<ConversationItem> conversationItems, Context context, LayoutInflater inflater, String regionName) {
		this.conversationItems = conversationItems;
		mInflater = inflater;
		this.context = context;
		this.regionName=regionName;
	}

	@Override
	public int getViewTypeCount() {
		return ViewHolderType.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		ConversationItem item = conversationItems.get(position);

		if (item.getTopic()!=null){
			return TYPE_PARENT;
		}
		else if (item.getPoster()!=null){
			return TYPE_REPLY;
		}
		else{
			return TYPE_LAUNCHREPLY;
		}
	}
	@Override
	public int getCount() {
		return conversationItems.size();
	}
	@Override
	public Object getItem(int position) {
		return conversationItems.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);

		if (convertView == null) {
			switch (type){
			case TYPE_PARENT:
				holder = new ParentViewHolder();
				convertView = mInflater.inflate(R.layout.conversation_parent_row, null);

				holder.idView = (TextView) convertView.findViewById(R.id.conversation_parent_idNum);
				holder.posterView = (TextView) convertView.findViewById(R.id.conversation_parent_poster);
				holder.contentView = (TextView) convertView.findViewById(R.id.conversation_parent_content);
				holder.dateView = (TextView) convertView.findViewById(R.id.conversation_parent_date);
				holder.topicView = (TextView) convertView.findViewById(R.id.conversation_parent_topic);

				convertView.setOnClickListener(new SendMessageClickListener(context));

				break;
			case TYPE_REPLY:
				holder = new ReplyViewHolder();
				convertView = mInflater.inflate(R.layout.conversation_reply_row, null);

				holder.idView = (TextView) convertView.findViewById(R.id.conversation_reply_idNum); 
				holder.posterView = (TextView) convertView.findViewById(R.id.conversation_reply_poster);
				holder.contentView = (TextView) convertView.findViewById(R.id.conversation_reply_content);
				holder.dateView = (TextView) convertView.findViewById(R.id.conversation_reply_date);
				
				convertView.setOnClickListener(new SendMessageClickListener(context));

				break;
			case TYPE_LAUNCHREPLY:
				holder = new LaunchReplyViewHolder();
				convertView = mInflater.inflate(R.layout.conversation_launch_reply_row, null);

				holder.idView = (TextView) convertView.findViewById(R.id.conversation_launch_reply_idNum);

				holder.launchReplybutton = (Button)convertView.findViewById(R.id.conversation_launch_reply_button);
				holder.launchReplybutton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View view) {

						RelativeLayout parent = (RelativeLayout) view.getParent();

						Intent intent = new Intent(context, ConversationAddReplyActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", context.MODE_WORLD_READABLE);
						String posterName = myPrefs.getString("userName", "Anon");
						intent.putExtra("poster", posterName);

						String idText = ((TextView)parent.findViewById(R.id.conversation_launch_reply_idNum)).getText().toString();
						intent.putExtra("idNum", idText);

						String queryableRegion = myPrefs.getString("currentRegion", "RegionInitializationError");
						intent.putExtra("region", queryableRegion);

						context.startActivity(intent);
					}

				});
				holder.lockButton = (Button)convertView.findViewById(R.id.lock_button);
				holder.lockButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View view) {

						RelativeLayout parent = (RelativeLayout)view.getParent();

						int idNum = Integer.parseInt(((TextView)parent.findViewById(R.id.conversation_launch_reply_idNum)).getText().toString());

						ConversationFilter.appliedRegion=regionName;
						ConversationFilter.idFilter = idNum;

						boolean lockSet = !ConversationFilter.lockSet;
						ConversationFilter.lockSet=lockSet;
						if (lockSet){
							//prevent complete depopulation
							ConversationFilter.topicFilter.clear();
						}

						ConversationFragment.sound = UpdateSound.LockSet;
						ConversationFragment.updateListView(true);
					}
				});
				break;
			}
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder)convertView.getTag();
		}

		ConversationItem dataPopulator = conversationItems.get(position);

		if(holder.getType()==ViewHolderType.PARENT){
			holder.idView.setText(dataPopulator.getIdNum());
			holder.posterView.setText(dataPopulator.getPoster());
			holder.topicView.setText(dataPopulator.getTopic());
			holder.contentView.setText(dataPopulator.getContent());
			holder.dateView.setText(dataPopulator.getDate());
		}
		else if (holder.getType()==ViewHolderType.REPLY){
			holder.idView.setText(dataPopulator.getIdNum());
			holder.posterView.setText(dataPopulator.getPoster());
			holder.contentView.setText(dataPopulator.getContent());
			holder.dateView.setText(dataPopulator.getDate());
		}else if (holder.getType()==ViewHolderType.LAUNCHREPLY){
			holder.idView.setText(dataPopulator.getIdNum());

			//may be horribly inefficient
			if (ConversationFilter.lockSet==true){
				holder.lockButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, 0, 0);
			}
			else{
				holder.lockButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_off_icon, 0, 0, 0);
			}
		}
		return convertView;
	}

	public static enum ViewHolderType{
		PARENT,
		REPLY,
		LAUNCHREPLY;
	}
	public static abstract class ViewHolder{

		TextView idView;
		TextView posterView;
		TextView dateView;
		TextView topicView;
		TextView contentView;
		Button launchReplybutton;
		Button lockButton;

		public abstract ViewHolderType getType();
	}
	public static class ParentViewHolder extends ViewHolder{
		public ViewHolderType type  = ViewHolderType.PARENT;
		@Override
		public ViewHolderType getType() {
			return type;
		}
	}
	public static class ReplyViewHolder extends ViewHolder{
		public ViewHolderType type  = ViewHolderType.REPLY;
		@Override
		public ViewHolderType getType() {
			return type;
		}
	}
	public static class LaunchReplyViewHolder extends ViewHolder{
		public ViewHolderType type  = ViewHolderType.LAUNCHREPLY;
		@Override
		public ViewHolderType getType() {
			return type;
		}
	}

	class SendMessageClickListener implements OnClickListener{

		Context context;

		private SendMessageClickListener(Context context){
			this.context = context;
		}

		@Override
		public void onClick(View personRow) {
			SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

			String sender = myPrefs.getString("userName", MainActivity.makeAnonTag());//<-shouldn't happen

			if (!sender.startsWith("Anon-")){



				//why did I have unique names for all of the row types again?
				boolean parentRow = false;

				TextView parent = (TextView) personRow.findViewById(R.id.conversation_parent_poster);
				if(parent!=null){
					parentRow = true;
				}

				String receiver;

				if (parentRow){
					receiver = parent.getText().toString();
				}
				else{
					receiver = ((TextView)personRow.findViewById(R.id.conversation_reply_poster)).getText().toString();
				}

				if (!receiver.startsWith("Anon-")){
					Intent startPM = new Intent(context, SendPMActivity.class);

					startPM.putExtra("sender", sender);
					startPM.putExtra("receiver", receiver);

					context.startActivity(startPM);
				}
			}
		}
	}
}