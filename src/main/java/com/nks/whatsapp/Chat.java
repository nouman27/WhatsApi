package com.nks.whatsapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.nks.whatsapp.event.ChatListener;

public class Chat implements Serializable,UniqueIdentity,EventEntity{

private static final long serialVersionUID = -1618564900058335222L;

private Member member;	
private Contact myContact;
private List<Message> messages=new ArrayList<Message>();
private transient List<ChatListener> listeners=new ArrayList<ChatListener>();


public Chat(Contact myContact,Member member)
{
this.myContact=myContact;
this.member=member;	
} 

public Member getMember() {
	return member;
}

public List<Message> getMessages() {
	return messages;
}

private void addMessage(Message message)
{
messages.add(message);	
for(ChatListener listener:listeners)
	listener.messageAdded(message);
}

public void sendMessage(Message message)
{
MessageStatus status=new MessageStatus(myContact.getUniqueId(),MessageStatus.Type.PENDING,new Date());
message.addStatus(status);
this.member.setLastMessage(message);
addMessage(message);
}

public void messageRecieved(String senderId,Message message,Date date)
{
MessageStatus status=new MessageStatus(senderId,MessageStatus.Type.RECIEVED,date);
message.addStatus(status);
member.setLastMessage(message);
addMessage(message);
}

public List<ChatListener> getListeners() {
	return listeners;
}

public void addChatListener(ChatListener listener)
{
this.listeners.add(listener);	
}

public void removeListener(ChatListener listener)
{
this.listeners.remove(listener);	
}


public void sortMessages(Comparator<Message> comparator)
{
	/*for(Sendable message:messages)
		System.out.println(message.getStatuses().get(0).getTime());*/
	Collections.sort(messages, comparator);
	/*System.out.println("After sorting");
	
	for(Sendable message:messages)
		System.out.println(message.getStatuses().get(0).getTime());*/
	
	
	
}

@Override
public String getUniqueId() {
	return member.getUniqueId();
}

@Override
public void removeListenerHirearchy() {
	myContact.removeListenerHirearchy();
	member.removeListenerHirearchy();
	if (messages!=null)
		for(Message message:messages)
			message.removeListenerHirearchy();
	for(int i=listeners.size();i==0;i--)
			listeners.remove(i);
}

}
