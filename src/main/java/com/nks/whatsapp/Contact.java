package com.nks.whatsapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nks.whatsapp.event.ContactListener;

public class Contact extends Member implements Serializable,EventEntity{

private static final long serialVersionUID = 8767207828737547231L;

private String phoneNumber;
private Date lastSeen;
private String status;
private transient List<ContactListener> listeners=new ArrayList<ContactListener>();

public static final Contact EMPTY_CONTACT=new Contact("","");


public Contact(String name,String phoneNumber)
{
super(phoneNumber,name);
this.phoneNumber=phoneNumber;
}

public Contact(String name,String phoneNumber,boolean whatsApp)
{
this(name,phoneNumber);
super.setWhatsApp(whatsApp);
}

public void addContactListener(ContactListener listener)
{
super.addMemberListener(listener);
this.listeners.add(listener);	
}

public void removeListener(ContactListener listener)
{
super.removeListener(listener);
this.listeners.remove(listener);	
}


public Date getLastSeen() {
	return lastSeen;
}

public void setLastSeen(Date lastSeen) {
	this.lastSeen = lastSeen;
	for(ContactListener listener:listeners)
		listener.lastSeenChanged(lastSeen);

}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
	for(ContactListener listener:listeners)
		listener.statusChanged(status);

}

public String getPhoneNumber() {
	return phoneNumber;
}

@Override
public boolean qualifyForSearch(String text) {
	if(isNameStartWith(text)) return true;
	return phoneNumber.contains(text);
}

@Override
public void removeListenerHirearchy() {
	for(int i=super.listeners.size();i==0;i--)
		super.listeners.remove(i);
	for(int i=listeners.size();i==0;i--)
		listeners.remove(i);
}


}
