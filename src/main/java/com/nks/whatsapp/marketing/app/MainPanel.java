package com.nks.whatsapp.marketing.app;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.nks.whatsapp.Chat;
import com.nks.whatsapp.Member;
import com.nks.whatsapp.WhatsApp;

public class MainPanel extends JPanel implements MemberListPanelListener,ActionListener{

private static final long serialVersionUID = -8452359507504685562L;

private ContactsPanel contacts;
private ChatsListPanel chats;
private ChatPanel chat;
private CardLayout layout=new CardLayout();
private MemberDetailPopup memberDetailPopup;
private WhatsApp whatsApp;
private AddMemberPopup addMemberPopup;

public 	MainPanel(MainApplication application)

{
init();
}
	
private void init()
{
	this.setLayout(layout);
	contacts=new ContactsPanel();
	chats=new ChatsListPanel();
	chat=new ChatPanel();
	contacts.setMainPanelBackground(Constants.getNormalApplicationBackgroudColor());
	chats.setMainPanelBackground(Constants.getNormalApplicationBackgroudColor());
	contacts.addChatListPanelListener(this);
	chats.addChatListPanelListener(this);
	contacts.setBackGround(Constants.getTopPanelBackgroudColor());
	chats.setBackGround(Constants.getTopPanelBackgroudColor());
	chat.setBackGround(Constants.getTopPanelBackgroudColor());
	chat.addChatListPanelListener(this);
	this.add(contacts);
	this.add(chats);
	this.add(chat);
	layout.addLayoutComponent(chats, "chats");
	layout.addLayoutComponent(contacts, "contact");
	layout.addLayoutComponent(chat, "chat");
	
	memberDetailPopup=new MemberDetailPopup(); 
	memberDetailPopup.setBorderPainted(false);
	memberDetailPopup.addMemberListPanelListener(this);

	
	addMemberPopup=new AddMemberPopup();
	addMemberPopup.setBackGround(Constants.getNormalApplicationBackgroudColor());
	}

public void updatePanel(WhatsApp whatsApp)
{
	this.whatsApp=whatsApp;
	chats.updateWhatsApp(whatsApp);
	contacts.updateWhatsApp(whatsApp);
}

@Override
public void showDetail(Component caller,Member member,boolean popup) {
if(popup)
{
	memberDetailPopup.updateMember(member);
	memberDetailPopup.show(caller, (caller.getWidth()/2)-100,70);
}
}

@Override
public void showChat(Member member) {
	
	try{
		Chat c=whatsApp.getChat(member);
		chat.updateChat(whatsApp,c);
		this.layout.show(this, "chat");
		validate();
	}catch(IOException ex){JOptionPane.showMessageDialog(this, "Unable to load chat for member:"+member.getName());return;}
	
}

@Override
public void showContactPanel() {
layout.show(this, "contact");
validate();
}

@Override
public void showMyContact() {
}

public void showAddGroup()
{
	
}

public void showAddContact()
{
	
}


@Override
public void showChatPanel() {
	this.layout.show(this, "chats");
	validate();
}

@Override
public void showAddUser(Component caller,boolean group) {
this.addMemberPopup.show(caller, 0-addMemberPopup.getPreferredSize().width, 30);
}

@Override
public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
	
}

private static void setComponentHirearchyBackgroundColor(Component comp,Color backGround)
{
if(comp!=null)
	{
	comp.setBackground(backGround);
	if(comp instanceof Container)
		{
		Component childs[]=((Container)comp).getComponents();
		for(Component child:childs)
			setComponentHirearchyBackgroundColor(child,backGround);
		}
	}
}


class AddMemberPopup extends JPopupMenu implements ActionListener 
{
private static final long serialVersionUID = -1408856399114655385L;

private ImageComponent addContact;
private ImageComponent addGroup;

private JPanel main;

private AddMemberPopup()
{
	setLightWeightPopupEnabled(false);
	init();	
}

private void init()
{
	this.setBorder(null);
	main=new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP,5,5));
	main.setOpaque(false);
	addContact=new ImageComponent(new Color(223,229,231),40,40,true);
	addContact.updateImage(ImageUtil.getNoMemberPhotoImage(Color.WHITE,30));
	addGroup=new ImageComponent(40,40,true);
	addGroup.updateImage(ImageUtil.getNoGroupPhotoImage(40));
	
	addContact.setOpaque(false);
	addGroup.setOpaque(false);
	
	main.add(addContact);
	main.add(addGroup);
	this.add(main);
	

	
	addContact.setActionCommand("addContact");
	addGroup.setActionCommand("addGroup");
	addContact.addActionListener(this);
	addGroup.addActionListener(this);
	
}

private void setBackGround(Color color)
{
setComponentHirearchyBackgroundColor(main,color);
}

@Override
public void actionPerformed(ActionEvent e) {
	if(e.getActionCommand().equals("addContact"))
		MainPanel.this.showAddContact();
	if(e.getActionCommand().equals("addGroup"))
		MainPanel.this.showAddGroup();
}

@Override
public void setVisible(boolean visible) {
    if (visible == isVisible())
        return;
    super.setVisible(visible);
    if (visible) {
        try {
            Window w = SwingUtilities.getWindowAncestor(this);
            w.setBackground(Color.WHITE);
            w.setOpacity(1f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

}

}




