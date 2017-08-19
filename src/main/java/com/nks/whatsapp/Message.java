package com.nks.whatsapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nks.whatsapp.event.MessageListener;

public abstract class Message implements Serializable,UniqueIdentity,EventEntity{

public static final int TEXT=0;	
public static final int VIDEO=1;
public static final int AUDIO=2;
public static final int PHOTO=3;


private static final long serialVersionUID = 5402700866873286839L;
private List<MessageStatus> statuses=new ArrayList<MessageStatus>();
private transient List<MessageListener> listeners=new ArrayList<MessageListener>();
private String senderId;
private String uniqueId;

protected Message(String uniqueId,String senderId)
{
	this.uniqueId=uniqueId;
	this.senderId=senderId;
}



public String getSenderId() {
	return senderId;
}



@Override
public String getUniqueId() {
	return uniqueId;
}



public List<MessageStatus> getStatuses()
{
	return statuses;
}

public void addMessageListener(MessageListener listener)
{
this.listeners.add(listener);	
}

public void removeListener(MessageListener listener)
{
this.listeners.remove(listener);	
}

public MessageStatus getLastStatus()
{
if(statuses.size()==0) return null;
return this.statuses.get(statuses.size()-1);	
}

public void addStatus(MessageStatus status)
{
	statuses.add(status);
	for(MessageListener listener:listeners)
		listener.messageStatusChanged(status);
}

@Override
public void removeListenerHirearchy() {
	for(int i=listeners.size();i==0;i--)
		listeners.remove(i);
}



public abstract int getType();

}
