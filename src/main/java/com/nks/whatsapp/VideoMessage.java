package com.nks.whatsapp;

import java.io.Serializable;

public class VideoMessage extends Message implements Serializable{

	private static final long serialVersionUID = -4768748881011353631L;

	public VideoMessage(String uniqueId,String senderId)
	{
		super(uniqueId,senderId);
		
	}
	@Override
	public int getType() {
	return Message.VIDEO;
	}

}
