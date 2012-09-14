package com.datapath.telepath;

//standard object for handing data to conversationAdapter

public class ConversationItem {
	
	private final String idNum;
	
	private final String poster;
	private final String date;
	private final String topic;
	private final String content;
	
	public ConversationItem(String idNum, String poster, String date, String topic, String content){
		this.idNum = idNum;
		this.poster = poster;
		this.date = date;
		this.topic = topic;
		this.content = content;
	}
	
	public String getIdNum(){
		return idNum;
	}
	public String getPoster(){
		return poster;
	}
	public String getDate(){
		return date;
	}
	public String getTopic(){
		return topic;
	}
	public String getContent(){
		return content;
	}
}
