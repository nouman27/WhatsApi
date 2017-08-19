package com.nks.whatsapp;

import java.util.List;

public interface WhatsAppStorage {

public void initStorage() throws StorageException;
	
public void addIdentity(String phoneNumber,String identity) throws StorageException;	
public void deleteIdentity(String phoneNumber) throws StorageException;	
public String getIdentity(String phoneNumber) throws StorageException;	


public void addCredentials(Credentials credentials) throws StorageException;
public Credentials getCredentials(Contact contact) throws StorageException;
public void deleteCredentials(Contact contact) throws StorageException;


public List<Contact> getRegisteredContacts() throws StorageException;
public void saveMyContact(Contact myContact)throws StorageException;
public void removeMyContact(Contact myContact)throws StorageException;


public String getNextMessageId(Contact contact) throws StorageException;
public String getNextGroupId(Contact contact) throws StorageException;





public List<Member> getContacts(Contact myContact) throws StorageException;
public void addMember(Contact myContact,Member member)throws StorageException;
public void addContactToGroup(Contact myContact,Group group,Contact another)throws StorageException;
public void removeMember(Contact myContact,Member member)throws StorageException;
public void removeContactFromGroup(Contact myContact,Group group,Contact another)throws StorageException;

public List<Chat> getChats(Contact myContact)throws StorageException;
public void addChat(Contact myContact,Chat chat)throws StorageException;

public void addMessageToChat(Contact myContact,Chat chat,Message message)throws StorageException;
public void removeMessageFromChat(Contact myContact,Chat chat,Message message)throws StorageException;



}
