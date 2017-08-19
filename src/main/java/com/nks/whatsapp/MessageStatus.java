package com.nks.whatsapp;

import java.io.Serializable;
import java.util.Date;

public class MessageStatus implements Serializable{

private static final long serialVersionUID = 5920205556402660124L;
private Type type;
private Date time;
private String memberId;
	
public MessageStatus(String memberId,Type type,Date time)
{
this.memberId=memberId;
this.type=type;
this.time=time;
}



public String getMemberId() {
return this.memberId;
}



public Type getType()
{
return type;	
}

public Date getTime() {
	return time;
}




	public enum Type
	{
	RECIEVED,
	PENDING,
	SENT,
	DELIVERED,
	SEEN;
	
	}

}
