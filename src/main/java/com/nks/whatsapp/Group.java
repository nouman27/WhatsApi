package com.nks.whatsapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.nks.whatsapp.event.GroupListener;

public class Group extends Member implements Serializable,EventEntity{

private static final long serialVersionUID = 1L;
private boolean local;
private Contact createdBy;
private Calendar createdAt;
private transient List<GroupListener> listeners=new ArrayList<GroupListener>();


List<Contact> contacts=new ArrayList<Contact>();

	public Group(String name,String id,boolean local) {
		super(id,name);
		this.local=local;	
	}


	public Group(String name,boolean local) {
		this(name,Member.generareUniquId(),local);
	}

	
	public void addGroupListener(GroupListener listener)
	{
	super.addMemberListener(listener);
	this.listeners.add(listener);	
	}

	public void removeListener(GroupListener listener)
	{
	super.removeListener(listener);
	this.listeners.remove(listener);	
	}


	public Contact getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(Contact createdBy) {
		this.createdBy = createdBy;
	}


	public Calendar getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Calendar createdAt) {
		this.createdAt = createdAt;
	}


	public List<Contact> getContacts()
	{
		return this.contacts;
	}

	
	public void addContact(Contact newContact){
		if(contacts.contains(newContact))
			return; 
	contacts.add(newContact);
	for(GroupListener listener:listeners)
			listener.contactAdded(newContact);

	}

	public boolean removeContact(Contact contact){
		if (!contacts.remove(contact)) return false;
	for(GroupListener listener:listeners)
			listener.contactRemoved(contact);
	return true;

	}

	@Override
	public boolean qualifyForSearch(String text) {
		return isNameStartWith(text);
	}


	@Override
	public void removeListenerHirearchy() {
			for(int i=super.listeners.size();i==0;i--)
				super.listeners.remove(i);
			for(int i=listeners.size();i==0;i--)
				listeners.remove(i);
	}


	public boolean isLocal() {
		return local;
	}

	

}
