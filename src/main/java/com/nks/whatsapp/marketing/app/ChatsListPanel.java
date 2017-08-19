package com.nks.whatsapp.marketing.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nks.whatsapp.Chat;
import com.nks.whatsapp.Member;
import com.nks.whatsapp.WhatsApp;
import com.nks.whatsapp.event.EventUtility;
import com.nks.whatsapp.event.WhatsAppListener;


public class ChatsListPanel extends JPanel implements ListPanelListener,ActionListener,DocumentListener,WhatsAppListener{

private static final long serialVersionUID = 2647735107932557683L;

private ListPanel mainPanel=new ListPanel(PanelUnit.CHATS);
private List<MemberListPanelListener> listeners=new ArrayList<MemberListPanelListener>();

private JPanel standardTopPanel;
private JPanel searchTopPanel;

private GenericMessagePanel namePanel;
private JPanel top;
private JPanel buttonsPanel;
private JPanel tabPanel;
private ImageComponent search;
private ActionableJPanel chatTabPanel;
private ActionableJPanel contactTabPanel;
private JLabel chatLabel;
private ImageComponent backButton;
private JTextField searchField;

private WhatsApp whatsApp;
private long listenerId=EventUtility.getUniqueListenerId();

public ChatsListPanel()
{
	super(new BorderLayout(0,0));
	jbInit();
}

private void jbInit()
{
add(mainPanel,BorderLayout.CENTER,0);	
initTopPanel();
initSearchPanel();
add(standardTopPanel,BorderLayout.NORTH,1);	
}

private void initTopPanel()
{
	standardTopPanel=new JPanel(new BorderLayout(0,0));
	namePanel=new GenericMessagePanel(GenericMessagePanel.TYPE_LARGE_HEADING);
	top=new JPanel(new BorderLayout(0,0));
	buttonsPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
	tabPanel=new JPanel(new GridLayout(1,2));
	search=new ImageComponent(false);
	chatTabPanel=new ActionableJPanel(new BorderLayout(0,0));
	contactTabPanel=new ActionableJPanel(new BorderLayout(0,0));
	namePanel.setActionCommand("name");
	namePanel.addActionListener(this);
	top.add(namePanel,BorderLayout.WEST);
	search.updateImage(ImageUtil.getSearchImage(Color.WHITE));
	search.setActionCommand("search");
	search.addActionListener(this);
	buttonsPanel.add(search);
	top.add(buttonsPanel,BorderLayout.EAST);
	standardTopPanel.add(top,BorderLayout.NORTH);
	chatTabPanel.setActionCommand("chat");
	chatTabPanel.addActionListener(this);
	chatTabPanel.setBackground(Color.black);
	chatLabel=new JLabel("CHATS",SwingConstants.CENTER);
	chatLabel.setFont(chatLabel.getFont().deriveFont(Font.BOLD));
	chatLabel.setForeground(Color.WHITE);
	chatTabPanel.add(chatLabel,BorderLayout.CENTER);
	JPanel sep=new JPanel(new GridLayout(1,1));
	for(int i=0;i<2;i++)
		{
		JSeparator sp=new JSeparator();
		sp.setForeground(Color.WHITE);
		sep.add(sp);
		}
	chatTabPanel.add(sep,BorderLayout.SOUTH);
	tabPanel.add(chatTabPanel);
	contactTabPanel.setActionCommand("contact");
	contactTabPanel.addActionListener(this);
	JLabel contactLabel=new JLabel("CONTACTS",SwingConstants.CENTER);
	contactLabel.setFont(contactLabel.getFont().deriveFont(Font.BOLD));
	contactLabel.setForeground(new Color(255,255,255,200));
	contactTabPanel.add(contactLabel,BorderLayout.CENTER);
	tabPanel.add(contactTabPanel);
	standardTopPanel.add(tabPanel,BorderLayout.SOUTH);
	mainPanel.addListPanelListener(this);
}

private void initSearchPanel()
{
	searchTopPanel=new JPanel(new BorderLayout(0,0));
	backButton=new ImageComponent(false);
	backButton.updateImage(ImageUtil.getGoBackImage());
	backButton.setActionCommand("back");
	backButton.addActionListener(this);
	searchField=new JTextField();
	searchField.getDocument().addDocumentListener(this);
	searchTopPanel.add(backButton,BorderLayout.WEST);
	searchTopPanel.add(searchField,BorderLayout.CENTER);
	setComponentHirearchyBackgroundColor(searchTopPanel,Color.WHITE);

}

private void showStandardPanel()
{
this.remove(1);
add(standardTopPanel,BorderLayout.NORTH,1);	
revalidate();
repaint();
}

private void showSearchPanel()
{
	this.remove(1);
	searchField.getDocument().removeDocumentListener(this);
	searchField.setText("");
	searchField.getDocument().addDocumentListener(this);
	searchField.setCaretPosition(0);
	add(searchTopPanel,BorderLayout.NORTH,1);	
	revalidate();
	repaint();
}
public void setBackGround(Color color)
{
	setComponentHirearchyBackgroundColor(standardTopPanel,color);
	}

private void setComponentHirearchyBackgroundColor(Component comp,Color backGround)
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

public void setMainPanelBackground(Color color)
{
if(mainPanel!=null)
	mainPanel.setBackground(color);
}

public void updateWhatsApp(WhatsApp whatsApp){
	if(this.whatsApp!=null) whatsApp.removeWhatsAppListener(this);
	
	List<Member> members=new ArrayList<Member>();
	Map<String,Chat> chats=whatsApp.getChats();
	for(Chat chat:chats.values())
		members.add(chat.getMember());
	mainPanel.setMembers(whatsApp, members);
	namePanel.updateMessage(whatsApp.getMyContact().getName());
	this.validate();
}


@Override
public void panelUnitAdded(PanelUnit panelUnit) {
	// TODO Auto-generated method stub
	
}

@Override
public void panelDetailPressed(PanelUnit panelUnit) {
for(MemberListPanelListener listener:listeners)
	listener.showChat(panelUnit.getMember());
}

@Override
public void panelImagePressed(PanelUnit panelUnit) {
	for(MemberListPanelListener listener:listeners)
		listener.showDetail(this,panelUnit.getMember(),true);
}

@Override
public void actionPerformed(ActionEvent e) {
	if(e.getActionCommand().equals("contact"))
		for(MemberListPanelListener listener:listeners)
			listener.showContactPanel();
	else if(e.getActionCommand().equals("name"))
		for(MemberListPanelListener listener:listeners)
			listener.showMyContact();
	else if(e.getActionCommand().equals("search"))
		{this.showSearchPanel();searchField.requestFocusInWindow();}
	else if(e.getActionCommand().equals("back"))
		this.showStandardPanel();
}

public void addChatListPanelListener(MemberListPanelListener listener)
{listeners.add(listener);}

@Override
public void insertUpdate(DocumentEvent e) {
mainPanel.search(searchField.getText());
}

@Override
public void removeUpdate(DocumentEvent e) {
	mainPanel.search(searchField.getText());
}

@Override
public void changedUpdate(DocumentEvent e) {
	mainPanel.search(searchField.getText());
}

@Override
public long getId() {
return this.listenerId;
}

@Override
public void memberAdded(Member member) {
}

@Override
public void chatAdded(Chat chat) {
this.mainPanel.addMember(whatsApp, chat.getMember(), true);
}



}
