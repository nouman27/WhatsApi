package com.nks.whatsapp.protocol.handlers;

import com.nks.whatsapp.protocol.ProtocolNode;
import com.nks.whatsapp.protocol.WhatsPort;

public class NotificationHandler implements Handler{

	private ProtocolNode node;
	private WhatsPort parent;
	private String phoneNumber;
	
	public NotificationHandler(WhatsPort parent,ProtocolNode node){
	this.parent=parent;
	this.node=node;
	this.phoneNumber=parent.getMyNumber();
	}
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	}

}
