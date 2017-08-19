package com.nks.whatsapp.marketing.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.nks.whatsapp.Contact;
import com.nks.whatsapp.Group;
import com.nks.whatsapp.Member;
import com.nks.whatsapp.Message;
import com.nks.whatsapp.MessageStatus;
import com.nks.whatsapp.WhatsApp;
import com.nks.whatsapp.event.EventUtility;

public class PanelUnit extends JPanel implements ActionListener{
private static final long serialVersionUID = -117791774197876105L;

public static final int CHATS=0;	
public static final int CONTACTS=1;	
public static final int CHAT=2;	


private int type;
private Member member;

private ImageComponent image;
private JPanel leftPanel;

private GenericMessagePanel upperDummyPanel;
private GenericMessagePanel topPanel;
private JPanel topRightPanel;
private GenericMessagePanel topDate;
private ImageComponent attachment;
private ImageComponent menu;
private GenericMessagePanel lowerPanel;
private GenericMessagePanel lowerDummyPanel;

private ActionableJPanel rightPanel;
private JPanel rightTopPanel;

private List<PanelUnitListener> listeners=new ArrayList<PanelUnitListener>();

private long listenerId=EventUtility.getUniqueListenerId();


public PanelUnit(int type)
{
super(new BorderLayout(0,0));
this.type=type;
init();
}


public void updateMember(WhatsApp whatsApp,Member member)
{
/*if(this.member!=null)
	this.member.removeListener(this);
this.member=member;
this.member.addMemberListener(this);*/

	updateImage(member.getImage());
if(type==CHATS)
{
	String memberText=null;
	MessageStatus lastStatus=member.getLastStatus();
	if(lastStatus!=null)
	{
	String originatorId=lastStatus.getMemberId();
	if(!originatorId.equals(whatsApp.getMyContact().getUniqueId()) && !originatorId.equals(member.getUniqueId()))
			memberText=(whatsApp.getMember(originatorId)==null?originatorId:whatsApp.getMember(originatorId).getName());
	}
	updateLastStatus(lastStatus,memberText,member.getLastMessageType(),member.getLastMessage());
}
else if(type==CONTACTS)
	updateMemberStatus();

else if(type==CHAT)
{
	updateMemberLastSeen();
}
repaint();
}

@Override
public void setBackground(Color color)
{
super.setBackground(color);
for(Component comp:getComponents())
	comp.setBackground(color);
if(image!=null)
{
leftPanel.setBackground(color);
image.setBackground(color);
upperDummyPanel.setBackground(color);
topPanel.setBackground(color);
topRightPanel.setBackground(color);
lowerPanel.setBackground(color);
lowerDummyPanel.setBackground(color);
rightTopPanel.setBackground(color);
}
}
private void init()
{
leftPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
if(type==CHAT)
	{
	ImageComponent backButton=new ImageComponent(false);
	backButton.updateImage(ImageUtil.getGoBackImage(new Color(255,255,255,254)));
	backButton.setActionCommand("back");
	backButton.addActionListener(this);
	leftPanel.add(backButton);
	}
image=new ImageComponent(type==CHAT?Constants.getRowHeightForPanelUnit()-20:Constants.getRowHeightForPanelUnit(),type==CHAT?Constants.getRowHeightForPanelUnit()-20:Constants.getRowHeightForPanelUnit(),true);
image.setActionCommand("left");
image.addActionListener(this);
leftPanel.add(image);
add(leftPanel,BorderLayout.WEST);
rightPanel=new ActionableJPanel(new GridLayout(type==CHAT?2:3,1));
rightPanel.setActionCommand("right");
rightPanel.addActionListener(this);
rightTopPanel=new JPanel(new BorderLayout(0,0));
rightTopPanel.setOpaque(false);
upperDummyPanel=new GenericMessagePanel(GenericMessagePanel.TYPE_HEADING);
topPanel=new GenericMessagePanel(GenericMessagePanel.TYPE_HEADING);
lowerDummyPanel=new GenericMessagePanel(GenericMessagePanel.TYPE_LINE);
if(type==CHAT)
	lowerPanel=new GenericMessagePanel(GenericMessagePanel.TYPE_DATE);
	else
	lowerPanel=new GenericMessagePanel(GenericMessagePanel.TYPE_NORMAL);
topRightPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
if(type==CHAT)
{
attachment=new ImageComponent(false);
attachment.updateImage(ImageUtil.getAttachmentImage(Color.WHITE));
attachment.setActionCommand("attachment");
attachment.addActionListener(this);

menu=new ImageComponent(false);
menu.updateImage(ImageUtil.getMenuImage(Color.WHITE));
menu.setActionCommand("menu");
menu.addActionListener(this);

topRightPanel.add(attachment);
topRightPanel.add(menu);

}
else
{
topDate=new GenericMessagePanel(GenericMessagePanel.TYPE_DATE);
topRightPanel.add(topDate);
}
rightTopPanel.add(topRightPanel,BorderLayout.EAST);
rightTopPanel.add(topPanel,BorderLayout.WEST);
rightPanel.add(rightTopPanel);
rightPanel.add(lowerPanel);
if(type!=CHAT)
rightPanel.add(lowerDummyPanel);
rightPanel.setPreferredSize(new Dimension(Constants.getRowHeightForPanelUnit()*2,Constants.getRowHeightForPanelUnit()-25));
add(rightPanel,BorderLayout.CENTER);
}


public Member getMember() {
	return member;
}


public void updateImage(Image im)
{
if(im==null)
	{
	if(member instanceof Group)
		im=ImageUtil.getNoGroupPhotoImage(type==CHAT?Constants.getRowHeightForPanelUnit()-20:Constants.getRowHeightForPanelUnit());
	else
	{
		image.setImageBackground(new Color(223,229,231));
		im=ImageUtil.getNoMemberPhotoImage(Color.WHITE,25);
	}
	}
else
	if(type==CHAT)
	im=im.getScaledInstance(Constants.getRowHeightForPanelUnit()-20, Constants.getRowHeightForPanelUnit()-20, Image.SCALE_SMOOTH);
	else
	im=im.getScaledInstance(Constants.getRowHeightForPanelUnit(), Constants.getRowHeightForPanelUnit(), Image.SCALE_SMOOTH);
	
image.updateImage(im);
}

public void updateLastStatus(MessageStatus lastStatus,String memberText,int messageType,String messageText)
{
topPanel.updateMessage(member.getName());
if(lastStatus!=null)
{
this.topDate.updateMessage(Constants.getDateFormatter().format(lastStatus.getTime()));	
this.lowerPanel.updateMessage(memberText,lastStatus,messageType,messageText);
}
else
{
this.topDate.updateMessage("");	
this.lowerPanel.updateMessage("");
}
}




@Override
public Dimension getPreferredSize() {
	return new Dimension(345,type==CHAT?45:63);
}



public void updateMemberName()
{
	topPanel.updateMessage(member.getName());
}

public void updateMemberStatus()
{
topPanel.updateMessage(member.getName());
if(member instanceof Contact)
{
Contact contact=(Contact)member;
lowerPanel.updateMessage(contact.getStatus()==null?contact.getPhoneNumber():contact.getStatus());
}	
else if(member instanceof Group)
{
lowerPanel.updateMessage("");
}	
topDate.updateMessage(member.isWhatsApp()?"Whats App":"Computer");
}

public void updateMemberLastSeen()
{
topPanel.updateMessage(member.getName(),Color.WHITE);
if(member instanceof Contact)
{
Contact contact=(Contact)member;
lowerPanel.updateMessage(contact.getLastSeen()==null?"":"last seen "+Constants.getLastSeenDateFormatter().format(contact.getLastSeen().getTime()),Color.WHITE);
}	
else if(member instanceof Group)
{
lowerPanel.updateMessage("");
}	

}

@Override
public void actionPerformed(ActionEvent e) {
if(e.getActionCommand().equals("right"))
	for(PanelUnitListener listener:listeners)
		listener.detailPressed(this);
	
if(e.getActionCommand().equals("left"))
	for(PanelUnitListener listener:listeners)
		listener.imagePressed(this);

if(e.getActionCommand().equals("back"))
	for(PanelUnitListener listener:listeners)
		listener.backPressed(this);

if(e.getActionCommand().equals("attachment"))
	for(PanelUnitListener listener:listeners)
		listener.attachmentPressed(this);

	if(e.getActionCommand().equals("menu"))
		for(PanelUnitListener listener:listeners)
			listener.menuPressed(this);

}

public void addPanelUnitListener(PanelUnitListener listener)
{this.listeners.add(listener);}


public boolean qualifyForSearch(String searchText)
{
return member.qualifyForSearch(searchText);	
}


public long getId() {
return this.listenerId;
}


public void lastMessageChanged(Message lastMessage) {
	// TODO Auto-generated method stub
}

public void imageChanged(Image newImage) {
	// TODO Auto-generated method stub
}
}
