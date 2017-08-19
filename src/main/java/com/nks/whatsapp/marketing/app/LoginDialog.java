package com.nks.whatsapp.marketing.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.nks.whatsapp.Contact;
import com.nks.whatsapp.StorageException;
import com.nks.whatsapp.WhatsAppFactory;

public class LoginDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private CredentialsTable credentialsTable;
	private int option;
	
	private JPanel actionsPanel;
	private JButton okButton;
	private JButton cancelButton;
	
	public static final int CANCEL=0;
	public static final int OK=1;
	public static final int ADD=2;
	
public LoginDialog(Window parent)
{
	super(parent);
	jInit();
}
	
private void jInit()
{
	//this.setTitle("Login");
	this.setUndecorated(true);
	credentialsTable=new CredentialsTable();
	okButton=new JButton("Login");
	okButton.setActionCommand("login");
	cancelButton=new JButton("Cancel");
	cancelButton.setActionCommand("cancel");

	okButton.addActionListener(this);
	cancelButton.addActionListener(this);

	actionsPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
	actionsPanel.add(okButton);
	actionsPanel.add(cancelButton);
	
	GridLayout grid=new GridLayout(2,1);
	grid.setHgap(1);
	mainPanel=new JPanel(grid);
	
	mainPanel.add(credentialsTable);
	this.setLayout(new BorderLayout(0,0));
	this.add(mainPanel,BorderLayout.CENTER);
	this.add(actionsPanel,BorderLayout.SOUTH);
	this.setSize(new Dimension(250,144));
	this.setResizable(false);
	this.setModal(true);
	this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	((JPanel)getContentPane()).setBorder(new LineBorder(Color.LIGHT_GRAY,1));
}

public void setBackGround(Color color)
{
	credentialsTable.setBackground(color);
	actionsPanel.setBackground(color);
	okButton.setBackground(color);
	cancelButton.setBackground(color);
	mainPanel.setBackground(color);
}

public int showDialog(Contact contact)
{
	option=CANCEL;
	this.credentialsTable.updateNumbersList();
	this.credentialsTable.setPhoneNumber(contact);
	setVisible(true);
	return option;
}

public Contact getSelectedContact(){
	return credentialsTable.getPhoneNumber();
}

@Override
public void actionPerformed(ActionEvent e) {
String commantString=e.getActionCommand();
if(commantString.equals("login"))
	{if(credentialsTable.getPhoneNumber()==Contact.EMPTY_CONTACT){JOptionPane.showMessageDialog(this, "Please Select a phone from the list or Click on 'Add New Number'");}else {this.option=OK;this.setVisible(false);}}
if(commantString.equals("cancel"))
{this.option=CANCEL;this.setVisible(false);}
}



class CredentialsTable extends JPanel implements ActionListener{
private static final long serialVersionUID = 1L;
private JComboBox<Contact> phoneNumberField=new JComboBox<Contact>(); 
private JButton addPhoneButton=new JButton("Add New Number");

public CredentialsTable()
{
GridLayout grid=new GridLayout(2,2);
grid.setVgap(1);
grid.setHgap(1);

this.setLayout(grid);
this.add(new JLabel("Phone Number:"));
phoneNumberField.setBorder(new LineBorder(Color.LIGHT_GRAY,1,true));
this.add(phoneNumberField);
this.add(new JLabel("Or "));
addPhoneButton.setBorder(new LineBorder(Color.LIGHT_GRAY,1,true));
addPhoneButton.addActionListener(this);
this.add(addPhoneButton);

}

public void updateNumbersList(){
	List<Contact> contacts=null;
	try{
	contacts=WhatsAppFactory.getRegisteredContacts();
	}catch(StorageException storageEx){JOptionPane.showMessageDialog(this, "Error Getting Contacts from Storage "+storageEx.getMessage(), "Error Getting Contacts",JOptionPane.ERROR_MESSAGE);return;}
	phoneNumberField.removeAllItems();
	phoneNumberField.addItem(Contact.EMPTY_CONTACT);
	for(Contact contact:contacts)
		phoneNumberField.addItem(contact);	
}

public void setPhoneNumber(Contact contact)
{
	if(contact==null)
		phoneNumberField.setSelectedItem(Contact.EMPTY_CONTACT);
	else
		phoneNumberField.setSelectedItem(contact);
}

private Contact getPhoneNumber()
{
return 	(Contact)phoneNumberField.getSelectedItem();
}

@Override
public void actionPerformed(ActionEvent e) {
LoginDialog.this.option=ADD;
LoginDialog.this.setVisible(false);
}


}
}
