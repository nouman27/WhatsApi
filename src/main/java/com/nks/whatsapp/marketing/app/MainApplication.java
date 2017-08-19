package com.nks.whatsapp.marketing.app;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.nks.whatsapp.StorageException;
import com.nks.whatsapp.WhatsApp;
import com.nks.whatsapp.WhatsAppFactory;

public class MainApplication extends JFrame{

private static final long serialVersionUID = 1L;



private transient LoginDialog loginDialog;
private MainPanel mainPanel;
	
public MainApplication()
{
	try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception ex){}
	jbInit();
}
private void jbInit()
{
	try{
		initializeResources();
		}catch(Exception ex){ex.printStackTrace();JOptionPane.showMessageDialog(this, ex.getMessage()+ ", Error Initializing system, exitting");System.exit(0);}	
	setLayout(new BorderLayout(0,0));
	setSize(new Dimension(200,500));
	loginDialog=new LoginDialog(this);
	loginDialog.setBackGround(Constants.getNormalApplicationBackgroudColor());
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	mainPanel=new MainPanel(this);
	this.add(mainPanel,BorderLayout.CENTER);
	this.setSize(390,685);
	//this.setResizable(false);
	setVisible(true);
	
}

private void initializeResources() throws Exception
{
	SVGImages.loadImages();	
	SVGImages.checkDefaultLoaded();
}


public static void main(String[] args)
{
	MainApplication main=new MainApplication();
	try{WhatsAppFactory.initStorage();}catch(StorageException strEx){JOptionPane.showMessageDialog(main, "Unable to initialize storage because of "+strEx.getMessage(), "Unable to load Storage", JOptionPane.ERROR_MESSAGE);strEx.printStackTrace();System.exit(0);}
	main.startApplication();
}

public void startApplication()
{
	loginDialog.setLocation(100,300);
	int option=loginDialog.showDialog(null);
	switch(option){
	case LoginDialog.OK:{try{refreshPanels(WhatsAppFactory.getWhatsApp(loginDialog.getSelectedContact()));}catch(StorageException strEx){JOptionPane.showMessageDialog(this, "Unable to load Phone data because "+strEx.getMessage(), "Unable to load Phone", JOptionPane.ERROR_MESSAGE);this.loginDialog.showDialog(null);}break;}
	case LoginDialog.ADD:{JOptionPane.showMessageDialog(this, "Add Contact");System.exit(0);}
	case LoginDialog.CANCEL:{try{Thread.sleep(200);}catch(Exception ex){}System.exit(0);}
	}
}



public void refreshPanels(WhatsApp whatsApp)
{
	mainPanel.updatePanel(whatsApp);
}


}

