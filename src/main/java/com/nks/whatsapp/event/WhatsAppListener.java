package com.nks.whatsapp.event;

import com.nks.whatsapp.Chat;
import com.nks.whatsapp.Member;

public interface WhatsAppListener extends Listener{
public void memberAdded(Member member);
public void chatAdded(Chat chat);
}
