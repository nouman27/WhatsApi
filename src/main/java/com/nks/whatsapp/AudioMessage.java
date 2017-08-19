package com.nks.whatsapp;

import java.io.Serializable;

public class AudioMessage extends Message implements Serializable{

	private static final long serialVersionUID = -7707915332642053079L;

	public AudioMessage(String uniqueId,String senderId)
	{
		super(uniqueId,senderId);
	}
	@Override
	public int getType() {
		return Message.AUDIO;
	}

}
