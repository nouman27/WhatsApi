package com.nks.whatsapp.event;

import java.util.Date;

public interface ContactListener extends MemberListener {

public void lastSeenChanged(Date lastSeen);
}
