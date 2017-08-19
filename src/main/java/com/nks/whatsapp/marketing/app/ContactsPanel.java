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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nks.whatsapp.Chat;
import com.nks.whatsapp.Member;
import com.nks.whatsapp.WhatsApp;
import com.nks.whatsapp.event.EventUtility;
import com.nks.whatsapp.event.WhatsAppListener;


public class ContactsPanel extends JPanel implements ListPanelListener,ActionListener,DocumentListener,WhatsAppListener{

private static final long serialVersionUID = 2647735107932557683L;

private ListPanel mainPanel=new ListPanel(PanelUnit.CONTACTS);
private List<MemberListPanelListener> listeners=new ArrayList<MemberListPanelListener>();

private JPanel standardTopPanel;
private JPanel searchTopPanel;

private GenericMessagePanel namePanel;
private JPanel top;
private JPanel buttonsPanel;
private JPanel tabPanel;
private ImageComponent search;
private ImageComponent menu;
private ImageComponent addUser;

private ActionableJPanel chatTabPanel;
private ActionableJPanel contactTabPanel;
private JLabel chatLabel;

private ImageComponent backButton;
private JTextField searchField;

private FilterPopup filter;

private WhatsApp whatsApp;
private long listenerId=EventUtility.getUniqueListenerId();

public ContactsPanel()
{
	super(new BorderLayout(0,0));
	jbInit();
}

private void jbInit()
{
add(mainPanel,BorderLayout.CENTER);	
initTopPanel();
initSearchPanel();
add(standardTopPanel,BorderLayout.NORTH,1);	
}

private void initTopPanel()
{
	standardTopPanel=new JPanel(new BorderLayout(0,0));
	namePanel=new GenericMessagePanel(GenericMessagePanel.TYPE_LARGE_HEADING);
	top=new JPanel(new BorderLayout(0,0));
	namePanel.setActionCommand("name");
	namePanel.addActionListener(this);
	top.add(namePanel,BorderLayout.WEST);
	buttonsPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
	search=new ImageComponent(false);
	search.updateImage(ImageUtil.getSearchImage(Color.WHITE));
	search.setActionCommand("search");
	search.addActionListener(this);
	menu=new ImageComponent(false);
	menu.updateImage(ImageUtil.getMenuImage(Color.WHITE));
	menu.setActionCommand("menu");
	menu.addActionListener(this);
	
	addUser=new ImageComponent(false);
	addUser.updateImage(ImageUtil.getAddUserImage(Color.WHITE));
	addUser.setActionCommand("addUser");
	addUser.addActionListener(this);
	
	buttonsPanel.add(search);
	buttonsPanel.add(addUser);
	buttonsPanel.add(menu);
	
	top.add(buttonsPanel,BorderLayout.EAST);
	standardTopPanel.add(top,BorderLayout.NORTH);
	
	tabPanel=new JPanel(new GridLayout(1,2));
	chatTabPanel=new ActionableJPanel(new BorderLayout(0,0));
	chatTabPanel.setActionCommand("chat");
	chatTabPanel.addActionListener(this);
	chatLabel=new JLabel("CHATS",SwingConstants.CENTER);
	chatLabel.setFont(chatLabel.getFont().deriveFont(Font.BOLD));
	chatLabel.setForeground(new Color(255,255,255,200));
	chatTabPanel.add(chatLabel,BorderLayout.CENTER);
	tabPanel.add(chatTabPanel);

	contactTabPanel=new ActionableJPanel(new BorderLayout(0,0));
	contactTabPanel.setActionCommand("contact");
	contactTabPanel.addActionListener(this);
	JLabel contactLabel=new JLabel("CONTACTS",SwingConstants.CENTER);
	contactLabel.setFont(contactLabel.getFont().deriveFont(Font.BOLD));
	contactLabel.setForeground(Color.WHITE);
	contactTabPanel.add(contactLabel,BorderLayout.CENTER);
	JPanel sep=new JPanel(new GridLayout(1,1));
	for(int i=0;i<2;i++)
		{
		JSeparator sp=new JSeparator();
		sp.setForeground(Color.WHITE);
		sep.add(sp);
		}
	contactTabPanel.add(sep,BorderLayout.SOUTH);
	tabPanel.add(contactTabPanel);
	standardTopPanel.add(tabPanel,BorderLayout.SOUTH);
	mainPanel.addListPanelListener(this);

	filter=new FilterPopup();
	filter.setBackGround(Constants.getNormalApplicationBackgroudColor());
}

private void initSearchPanel()
{
	searchTopPanel=new JPanel(new BorderLayout(0,0));
	backButton=new ImageComponent(false);
	backButton.updateImage(ImageUtil.getGoBackImage());
	backButton.setActionCommand("back");
	backButton.addActionListener(this);
	searchField=new JTextField();
	searchField.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
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

public void setMainPanelBackground(Color color)
{
if(mainPanel!=null)
	mainPanel.setBackground(color);
}


public void updateWhatsApp(WhatsApp whatsApp){
	if(this.whatsApp!=null)	whatsApp.removeWhatsAppListener(this);
	mainPanel.setMembers(whatsApp, whatsApp.getMembers().values());
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
	if(e.getActionCommand().equals("chat"))
		for(MemberListPanelListener listener:listeners)
			listener.showChatPanel();
	else if(e.getActionCommand().equals("name"))
		for(MemberListPanelListener listener:listeners)
			listener.showMyContact();
	else if(e.getActionCommand().equals("search"))
	{this.showSearchPanel();searchField.requestFocusInWindow();}
	else if(e.getActionCommand().equals("menu"))
		filter.show((Component)e.getSource(), 0-180, 30);
	else if(e.getActionCommand().equals("addUser"))
		for(MemberListPanelListener listener:listeners)
			listener.showAddUser((Component)e.getSource(),false);

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
	this.mainPanel.addMember(whatsApp, member, true);
}

@Override
public void chatAdded(Chat chat) {
}




class FilterPopup extends JPopupMenu implements ChangeListener 
{
private static final long serialVersionUID = -1408856399114655385L;

private JCheckBox showWhatsApp;
private JCheckBox showGroups;
private JCheckBox showNonWhatsApp;
private JCheckBox showMembers;

private JPanel main;

private FilterPopup()
{
init();	
}

private void init()
{
	main=new JPanel(new GridLayout(5,1));
	
	showWhatsApp=new JCheckBox("Show WhatsApp Users");
	showGroups=new JCheckBox("Show Groups");
	showNonWhatsApp=new JCheckBox("Show Non WhatsApp Users");
	showMembers=new JCheckBox("Show Contacts");
	
	showWhatsApp.setSelected(true);
	showGroups.setSelected(true);
	showNonWhatsApp.setSelected(true);
	showMembers.setSelected(true);
	main.add(showWhatsApp);
	main.add(showNonWhatsApp);
	main.add(new JSeparator());
	main.add(showMembers);
	main.add(showGroups);
	this.add(main);

	showWhatsApp.addChangeListener(this);
	showGroups.addChangeListener(this);
	showNonWhatsApp.addChangeListener(this);
	showMembers.addChangeListener(this);

}

private void setBackGround(Color color)
{
setComponentHirearchyBackgroundColor(main,color);
}

@Override
public void stateChanged(ChangeEvent e) {
	ContactsPanel.this.mainPanel.filter(showWhatsApp.isSelected(), showNonWhatsApp.isSelected(), showMembers.isSelected(), showGroups.isSelected());
}
}
}
