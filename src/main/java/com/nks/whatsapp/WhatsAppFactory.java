package com.nks.whatsapp;

import java.util.List;
import java.util.Map;

import com.nks.whatsapp.protocol.Registration;


public class WhatsAppFactory {

public static final String METHOD_SMS="sms";
public static final String METHOD_VOICE="voice";

private static WhatsAppStorage storage;
private static boolean initiated;


public static Map<Integer,String> getCountryList(){
	return Registration.getCountriesList();
}

public static List<String> getNetworkList(String countryCode){
	return Registration.getNetworkList(countryCode);
}

public static void isPhoneNumberValid(String phoneNumber){
	if (phoneNumber==null || phoneNumber.length()<9) throw new RuntimeException("Phone Number lenght must be greater than 8 digits");
	try{Long.parseLong(phoneNumber);}catch(java.lang.NumberFormatException nnEx){throw new RuntimeException("Phone Number can contains only Numeric values");}
	Map<String,String> dissectedPhone=Registration.dissectPhone(phoneNumber);
	if(dissectedPhone==null) throw new RuntimeException("Phone number is invalid");
	String cc=dissectedPhone.get("cc");
	System.out.println(dissectedPhone.get("country"));
	if (phoneNumber.substring(cc.length()).startsWith("0"))
		throw new RuntimeException("Phone number is invalid,Please remove preceeding 0s after country code");
}
public static void initStorage() throws StorageException{
	storage=new SQLiteStorage("data.db");
	getStorage();
}
static WhatsAppStorage getStorage() throws StorageException{
	if (!initiated)
		try{storage.initStorage();initiated=true;}catch(StorageException strEx){initiated=false;throw strEx;}
	return storage;
}

public static void requestForCode(String method,String number,String carrier) throws StorageException{
	Registration registration=new Registration(number);
	String[] response=registration.codeRequest(method, carrier);
	getStorage().deleteIdentity(number);
	if(response[1]!=null)
		{getStorage().addIdentity(number, response[0]);
		Contact myContact=new Contact(number,number);
		Credentials credentials=new Credentials(myContact,response[1]);
		getStorage().addCredentials(credentials);
		}
}
	 
public static void registerCode(String number,String code,String name) throws StorageException{
	Registration registration=new Registration(number);
	String identity=getStorage().getIdentity(number);
	if(identity==null)
		throw new RuntimeException("Unable to get Identity from storage, Please first request for code");
	String pw=registration.codeRegisterForIdentity(code, identity);
	if(pw!=null){
		Contact myContact=new Contact(number,number);
		Credentials credentials=new Credentials(myContact,pw);
		getStorage().addCredentials(credentials);
	}
	else
		throw new RuntimeException("Response returned no password for number:"+number);
}


public static List<Contact> getRegisteredContacts() throws StorageException{
	return getStorage().getRegisteredContacts();
}

public static WhatsApp getWhatsApp(Contact myContact) throws StorageException
{
/*Credentials credentials=getStorage().getCredentials(myContact);	
if(credentials==null){ getStorage().removeMyContact(myContact);throw new RuntimeException("Couldnt find credentials for Contact, Please register the contact again");}*/
return new WhatsApp(getStorage(),myContact);
}





}
