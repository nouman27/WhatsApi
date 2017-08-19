package com.nks.whatsapp;

import java.awt.Dimension;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.nks.whatsapp.event.MemberListener;

public abstract class Member implements Serializable,UniqueIdentity,EventEntity{


private static final long serialVersionUID = 4795729124496289146L;
public static final String SYSTEM_MEMBER_ID="system";

private String name;
private Image image;
private String uniqueId;
private MessageStatus lastStatus;
private int lastMessageType;
private String lastMessage;
private boolean whatsApp;
private transient String[] nameTokens;
private transient Map<Dimension,Image> images=new Hashtable<Dimension,Image>();
protected transient List<MemberListener> listeners=new ArrayList<MemberListener>();



protected Member(String uniqueId,String name)
{
this.uniqueId=uniqueId;
this.name=name;	
}

public String getName(){
	return name;
}


@Override
public String getUniqueId() {
	return uniqueId;
}

public void addMemberListener(MemberListener listener)
{
this.listeners.add(listener);	
}

public void removeListener(MemberListener listener)
{
this.listeners.remove(listener);	
}



public void setImag(Image image)
{
this.image=image;
for(MemberListener listener:listeners)
		listener.imageChanged(image);
}


public Image getImage()
{
	return image;
}

protected static String generareUniquId()
{
return Integer.toString(new Random().nextInt(99999999));
}


public boolean isWhatsApp() {
	return whatsApp;
}

public void setWhatsApp(boolean whatsApp) {
	this.whatsApp = whatsApp;
}

public MessageStatus getLastStatus() {
	return lastStatus;
}

public int getLastMessageType() {
	return lastMessageType;
}

public String getLastMessage() {
	return lastMessage;
}

public void setLastMessage(Message message) {
	this.lastStatus = message.getLastStatus();
	this.lastMessageType=message.getType();
	if(message instanceof TextMessage)
		this.lastMessage=((TextMessage)message).getMessage();
	else
		this.lastMessage=null;
	for(MemberListener listener:listeners)
		listener.lastMessageChanged(message);

}

public abstract boolean qualifyForSearch(String text);

public boolean isNameStartWith(String text)
{
if(nameTokens==null)
	initNameTokens();
for(String name:nameTokens)
	if(name.toLowerCase().startsWith(text.toLowerCase()))
		return true;
return false;
}

private void initNameTokens()
{
	nameTokens=name.split(" ");
}

public Image getImage(int width,int height)
{
Dimension dimension=new Dimension(width,height);	
if(images.containsKey(dimension))return images.get(dimension);
if(image==null)return null;
Image newImage=image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
images.put(dimension, newImage);
return newImage;
}

@Override
public void removeListenerHirearchy() {
	// TODO Auto-generated method stub
	
}

@Override
public int hashCode() {
return this.uniqueId.hashCode();
}

@Override
public boolean equals(Object obj) {
	try{
		return this.uniqueId.equals(((Member)obj).uniqueId);
	}catch(ClassCastException ccEX){return false;}
}

@Override
public String toString() {
return name;
}


}
