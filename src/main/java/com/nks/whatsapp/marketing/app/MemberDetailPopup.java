package com.nks.whatsapp.marketing.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.nks.whatsapp.Group;
import com.nks.whatsapp.Member;

public class MemberDetailPopup extends JPopupMenu implements ActionListener{

	private List<MemberListPanelListener> listeners=new ArrayList<MemberListPanelListener>();
	
	private static final long serialVersionUID = 1L;

	private Member member;
	//private JPanel mainPanel;
	private ImageComponent imageComponent;
	
	private JPanel tabPanel;
	private ImageComponent chatTab;
	private ImageComponent infoTab;
	
	public MemberDetailPopup()
	{
		init();
	}

private void init()
{
this.setBorder(null);
imageComponent=new ImageComponent(Color.CYAN,250,250,false);	
tabPanel=new JPanel(new GridLayout(1,1));
tabPanel.setPreferredSize(new Dimension(200,30));

chatTab=new ImageComponent(Constants.getNormalApplicationBackgroudColor(),125,30,false);
chatTab.setActionCommand("chat");
chatTab.addActionListener(this);
chatTab.updateImage(ImageUtil.getChatImage());
tabPanel.add(chatTab);

infoTab=new ImageComponent(Constants.getNormalApplicationBackgroudColor(),125,30,false);
infoTab.updateImage(ImageUtil.getInfoImage(Color.LIGHT_GRAY,25));
infoTab.setActionCommand("info");
infoTab.addActionListener(this);

tabPanel.add(infoTab);

add(imageComponent);
add(tabPanel);
}
	
public void updateMember(Member member)
{
this.member=member;	
Image image=member.getImage(250, 250);
if(image==null)
	{
	if(member instanceof Group)
		image=ImageUtil.getNoGroupPhotoImage(100);
	else
		image=ImageUtil.getNoMemberPhotoImage(Color.LIGHT_GRAY,100);
	}
imageComponent.updateImage(image);
repaint();
}

@Override
public Dimension getPreferredSize() {
	return new Dimension(250,280);
}

public void addMemberListPanelListener(MemberListPanelListener listener)
{
	listeners.add(listener);
	
}

@Override
public void actionPerformed(ActionEvent e) {
if(e.getActionCommand().equals("chat"))
	{
	this.setVisible(false);
	for(MemberListPanelListener listener:listeners)
		listener.showChat(member);
	}
else if (e.getActionCommand().equals("info"))
{
this.setVisible(false);
for(MemberListPanelListener listener:listeners)
	listener.showDetail(null, member,false);
}
 
}

}
