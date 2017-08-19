package com.nks.whatsapp;

import java.io.Serializable;

public class PhotoMessage extends Message implements Serializable{
private static final long serialVersionUID = 5650812844375036929L;

	public PhotoMessage(String uniqueId,String senderId)
	{
		super(uniqueId,senderId);
		
	}

	@Override
	public int getType() {
		return Message.PHOTO;
	}

	
}
