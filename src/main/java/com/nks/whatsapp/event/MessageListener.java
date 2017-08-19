package com.nks.whatsapp.event;

import com.nks.whatsapp.MessageStatus;

public interface MessageListener extends Listener{

public void messageStatusChanged(MessageStatus newStatus);	
	
}
