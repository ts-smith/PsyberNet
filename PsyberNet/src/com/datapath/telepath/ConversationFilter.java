package com.datapath.telepath;

import java.util.HashSet;
import java.util.Set;

/*
 * Static object for handling locks and filters.
 */

public class ConversationFilter {

	//this probably doesn't need to be a set, since redundancies are handled in the settings activity
	static Set<String> topicFilter = new HashSet<String>();
	static String appliedRegion;
	static int idFilter;
	static boolean lockSet = false;

	//temporary minimal functionality
	public static boolean acceptsTopic(String topic){
		if (topicFilter.size()==0 || valuePassed(topic)){
			return true;
		}
		else{
			return false;
		}
	}
	//check if topic is in topicFilter, returns true if so
	static boolean valuePassed(String topic){
		
		for (String acceptedTopic: topicFilter){
			if(topic.equals(acceptedTopic)){
				return true;
			}
		}
		return false;
	}
	static LockStatus getLockStatus(int idNum){
		if (lockSet == false){
			return LockStatus.NOT_SET;
		}else if(idNum == idFilter){
			return LockStatus.LOCK_ACCEPTED;
		}else{
			return LockStatus.LOCK_BLOCKED;
		}
	}
	public enum LockStatus {
		NOT_SET,
		LOCK_ACCEPTED,
		LOCK_BLOCKED
	}
}


