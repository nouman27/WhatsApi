package com.nks.whatsapp.marketing.app;

import java.awt.Component;
import java.util.EventListener;

import com.nks.whatsapp.Member;

public interface MemberListPanelListener extends EventListener{
public void showDetail(Component caller,Member member,boolean popup);
public void showChat(Member member);
public void showContactPanel();
public void showChatPanel();

public void showMyContact();
public void showAddUser(Component caller,boolean group);
}
