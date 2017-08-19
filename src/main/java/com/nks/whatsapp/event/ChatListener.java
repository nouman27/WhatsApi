package com.nks.whatsapp.event;

import com.nks.whatsapp.Message;

public interface ChatListener extends Listener{
public void messageAdded(Message message);
}
