package com.nks.whatsapp;

import java.io.Serializable;

public class TextMessage extends Message implements Serializable{
private static final long serialVersionUID = -691853869512498479L;
private String message;
public TextMessage(String uniqueId,String senderId,String message)
{
this(uniqueId,senderId,message,false);	
}

public TextMessage(String uniqueId,String senderId,String message,boolean system)
{
super(uniqueId,senderId);
this.message=message;
}

public String getMessage() {
	return message;
}

@Override
public int getType() {
return Message.TEXT;
}

public static class TextMessagePart{	

private TextMessagePart(){
	
}

}

public static class StringMessagePart extends TextMessagePart{

	
}

}
