package com.nks.whatsapp.event;

import java.awt.Image;

import com.nks.whatsapp.Message;

public interface MemberListener extends Listener{
public void lastMessageChanged(Message lastMessage);
public void imageChanged(Image newImage);
public void nameChanged(String newName);
public void statusChanged(String newStatus);
}
