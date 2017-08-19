package com.nks.whatsapp;

import java.io.Serializable;


public class Credentials implements Serializable{

private static final long serialVersionUID = 1L;
private String encryptedPassword;
private Contact myContact;

public Credentials(Contact myContact,String encryptedPassword)
{
this.myContact=myContact;
this.setEncryptedPassword(encryptedPassword);
}

public String getEncryptedPassword() {
	return encryptedPassword;
}

public void setEncryptedPassword(String encryptedPassword) {
	this.encryptedPassword = encryptedPassword;
}




public Contact getMyContact() {
	return myContact;
}





}
