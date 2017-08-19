package com.nks.whatsapp.event;

import com.nks.whatsapp.Contact;

public interface GroupListener extends MemberListener{
public void contactAdded(Contact newContact);	
public void contactRemoved(Contact removedContact);
}
