package com.nks.whatsapp.protocol.handlers;

import java.io.IOException;

import com.nks.whatsapp.protocol.ProtocolNode;
import com.nks.whatsapp.protocol.WhatsPort;

public class IqHandler implements Handler{

	private ProtocolNode node;
	private WhatsPort parent;
	private String phoneNumber;
	
	public IqHandler(WhatsPort parent,ProtocolNode node){
	this.parent=parent;
	this.node=node;
	this.phoneNumber=parent.getMyNumber();	
	}
	
	@Override
	public void process() throws IOException{
		// TODO Auto-generated method stub
		if(node.getChild("media")!=null || node.getChild("duplicate")!=null)
		parent.processUploadResponse(node);
	}

}
