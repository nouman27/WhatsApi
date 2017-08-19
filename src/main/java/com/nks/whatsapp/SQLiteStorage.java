package com.nks.whatsapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteStorage implements WhatsAppStorage{

	private String storageFile;
	private Connection connection;
	
	public SQLiteStorage(String storageFile){
		this.storageFile=storageFile;
	}
	
	@Override
	public void initStorage() throws StorageException {
		try{
		Class.forName("org.sqlite.JDBC");
		}catch(ClassNotFoundException cnfEx){throw new StorageException("Database driver class org.sqlite.JDBC not found, please check class path and make sure sqlite driver exists in it",cnfEx);}	
	String url="jdbc:sqlite:"+storageFile;
	Statement stmt=null;
	try{
	connection=DriverManager.getConnection(url);
	connection.setAutoCommit(false);
	stmt=connection.createStatement();
	stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MYPHONES (phoneNumber TEXT PRIMARY KEY,name TEXT,identity TEXT,password TEXT,lastStatus Text)");
	stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MEMBERS (phoneNumber TEXT,id TEXT,name TEXT,isGroup INT(1),local INT(1),displayStatus TEXT,statusLastSet INTEGER,lastSeen INTEGER,PRIMARY KEY(phoneNumber,id))");
	/*stmt.executeUpdate("CREATE TABLE IF NOT EXISTS GROUP_CONTACTS (phoneNumber TEXT PRIMARY KEY,groupId TEXT PRIMARY KEY,id TEXT PRIMARY KEY,role INT(1))");
	stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CHATS (phoneNumber TEXT PRIMARY KEY,memberId TEXT PRIMARY KEY,lastMessageId TEXT)");
	stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MESSAGES (phoneNumber TEXT PRIMARY KEY,memberId TEXT PRIMARY KEY,id TEXT PRIMARY KEY,type INT(1),textVal TEXT)");
	stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MESSAGE_STATUS (phoneNumber TEXT PRIMARY KEY,memberId TEXT PRIMARY KEY,messageId TEXT PRIMARY KEY,memberId TEXT PRIMARY KEY,status INT(1) PRIMARY KEY,)");*/	
	}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
	finally{try{stmt.close();connection.close();}catch(Exception ex){}}
	}

	@Override
	public void addIdentity(String phoneNumber, String identity) throws StorageException {
	PreparedStatement stmt=null;
	PreparedStatement insertStmt=null;
	ResultSet resultSet=null;
	try{
		stmt=connection.prepareStatement("SELECT identity from MYPHONES WHERE phoneNumber=?");
		stmt.setString(1, phoneNumber);
		resultSet=stmt.executeQuery();
		if(!resultSet.next()) 
			{
			insertStmt=connection.prepareStatement("INSERT INTO MYPHONES(phoneNumber,identity) VALUES (?,?)");
			insertStmt.setString(1, phoneNumber);
			insertStmt.setString(2, identity);
			insertStmt.executeUpdate();
			}
		else
			{
			insertStmt=connection.prepareStatement("UPDATE MYPHONES SET identity=? WHERE phoneNumber=?");
			insertStmt.setString(1, identity);
			insertStmt.setString(2, phoneNumber);
			insertStmt.executeUpdate();
			}
		connection.commit();
	}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
	finally{try{resultSet.close();insertStmt.close();stmt.close();}catch(Exception ex){}}
	}

	@Override
	public void deleteIdentity(String phoneNumber) throws StorageException{
		PreparedStatement insertStmt=null;
		try{
			insertStmt=connection.prepareStatement("UPDATE MYPHONES SET identity=? WHERE phoneNumber=?");
			insertStmt.setString(1, null);
			insertStmt.setString(2, phoneNumber);
			insertStmt.executeUpdate();
			connection.commit();
		}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
		
		finally{try{insertStmt.close();}catch(Exception ex){}}

	}

	@Override
	public String getIdentity(String phoneNumber) throws StorageException {
		PreparedStatement stmt=null;
		ResultSet resultSet=null;
		try{
			stmt=connection.prepareStatement("SELECT identity from MYPHONES WHERE phoneNumber=?");
			stmt.setString(1, phoneNumber);
			resultSet=stmt.executeQuery();
			if(resultSet.next()) 
				return resultSet.getString("identity");
			else return null;
		}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
		finally{try{resultSet.close();stmt.close();}catch(Exception ex){}}
		
	}

	@Override
	public void addCredentials(Credentials credentials) throws StorageException {
		PreparedStatement stmt=null;
		PreparedStatement insertStmt=null;
		ResultSet resultSet=null;
		try{
			stmt=connection.prepareStatement("SELECT password from MYPHONES WHERE phoneNumber=?");
			stmt.setString(1, credentials.getMyContact().getUniqueId());
			resultSet=stmt.executeQuery();
			if(!resultSet.next()) 
				{
				insertStmt=connection.prepareStatement("INSERT INTO MYPHONES(phoneNumber,password) VALUES (?,?)");
				insertStmt.setString(1, credentials.getMyContact().getUniqueId());
				insertStmt.setString(2, credentials.getEncryptedPassword());
				insertStmt.executeUpdate();
				}
			else
				{
				insertStmt=connection.prepareStatement("UPDATE MYPHONES SET password=? WHERE phoneNumber=?");
				insertStmt.setString(1, credentials.getEncryptedPassword());
				insertStmt.setString(2, credentials.getMyContact().getUniqueId());
				insertStmt.executeUpdate();
				}
			connection.commit();
		}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
		finally{try{resultSet.close();insertStmt.close();stmt.close();}catch(Exception ex){}}

	}

	@Override
	public Credentials getCredentials(Contact contact) throws StorageException{
		PreparedStatement stmt=null;
		ResultSet resultSet=null;
		try{
			stmt=connection.prepareStatement("SELECT password from MYPHONES WHERE phoneNumber=?");
			stmt.setString(1, contact.getUniqueId());
			resultSet=stmt.executeQuery();
			if(resultSet.next()) 
				return new Credentials(contact,resultSet.getString("password"));
			else return null;
		}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
		finally{try{resultSet.close();stmt.close();}catch(Exception ex){}}
	}

	@Override
	public void deleteCredentials(Contact contact) throws StorageException {
		PreparedStatement insertStmt=null;
		try{
			insertStmt=connection.prepareStatement("UPDATE MYPHONES SET password=? WHERE phoneNumber=?");
			insertStmt.setString(1, null);
			insertStmt.setString(2, contact.getUniqueId());
			insertStmt.executeUpdate();
			connection.commit();
		}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
		finally{try{insertStmt.close();}catch(Exception ex){}}
	}

	@Override
	public List<Contact> getRegisteredContacts() throws StorageException {
		List<Contact> registeredContacts=new ArrayList<Contact>();
		/*PreparedStatement stmt=null;
		ResultSet resultSet=null;
		try{
			stmt=connection.prepareStatement("SELECT phoneNumber,name from MYPHONES");
			resultSet=stmt.executeQuery();
			while(resultSet.next()) 
				registeredContacts.add(new Contact(resultSet.getString("phoneNumber"),resultSet.getString("name")));
		return registeredContacts;
		}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
		finally{try{resultSet.close();stmt.close();}catch(Exception ex){}}*/
		registeredContacts.add(new Contact("Nouman Khalid","923214467134"));
		registeredContacts.add(new Contact("Sadaf Nouman","923218422025"));
		
		return registeredContacts;
	}

	
	
	
		public static void main(String[] args){
		SQLiteStorage storage=new SQLiteStorage("test.db");
		try{
		
		storage.initStorage();
		//storage.addIdentity("00923214467134", "32dsae35rase43434=");
		System.out.println("Identity is "+storage.getIdentity("00923214467134"));
		/*System.out.println("Identity is "+storage.getIdentity("00923214467134"));
		storage.deleteIdentity("00923214467134");
		System.out.println("Identity is "+storage.getIdentity("00923214467134"));*/
		
		}catch(Exception ex){ex.printStackTrace();System.exit(0);}
	
	}

		@Override
		public String getNextMessageId(Contact contact) throws StorageException{
		PreparedStatement stmt=null;
		ResultSet resultSet=null;
			try{
				stmt=connection.prepareStatement("SELECT MAX(id) from MESSAGES WHERE phoneNumber=?");
				stmt.setString(1, contact.getPhoneNumber());
				resultSet=stmt.executeQuery();
				if (resultSet.next())
				{
				String currentId=resultSet.getString(1);
				try{return Integer.toString(Integer.parseInt(currentId)+1);}catch(java.lang.NumberFormatException nnEx){throw new StorageException("Id of last Message is not an integer"+currentId);}
				}
			else return "1";
			}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
			finally{try{resultSet.close();stmt.close();}catch(Exception ex){}}
		
		}

		
		@Override
		public String getNextGroupId(Contact contact) throws StorageException{
			PreparedStatement stmt=null;
			ResultSet resultSet=null;
				try{
					stmt=connection.prepareStatement("SELECT MAX(id) from MEMBERS WHERE phoneNumber=? and group=1");
					stmt.setString(1, contact.getPhoneNumber());
					resultSet=stmt.executeQuery();
					if (resultSet.next())
						{
						String currentId=resultSet.getString(1);
						try{return Integer.toString(Integer.parseInt(currentId)+1);}catch(java.lang.NumberFormatException nnEx){throw new StorageException("Id of last group is not an integer"+currentId);}
						}
					else return "1";
				}catch(SQLException sqlEx){throw new StorageException(sqlEx);}
				finally{try{resultSet.close();stmt.close();}catch(Exception ex){}}
		}

		@Override
		public List<Member> getContacts(Contact myContact)
				throws StorageException {
			List<Member> membersList=new ArrayList<Member>();
			membersList.add(new Contact("Aihtisham Ullah","923214223231"));
			membersList.add(new Contact("Refaqat Hussain","923214223432"));
			membersList.add(new Contact("Tauheed","92434343243"));
			membersList.add(new Contact("Naveed","443423432432"));
			membersList.add(new Contact("Daniyal","923214323211"));
			membersList.add(new Contact("Gogo Pan Masala","91"));
			membersList.add(new Contact("Mani Jani","92"));
			membersList.add(new Contact("Dullah Raja","Dulha Raja"));
			membersList.add(new Group("FFC Model High School","01",false));
			membersList.add(new Group("My Family","02",false));
			membersList.add(new Group("WhatsApp Marketing","Marketing",true));
			return membersList;
		}

		@Override
		public List<Chat> getChats(Contact myContact) {
			// TODO Auto-generated method stub
			return new ArrayList<Chat>();
		}

		@Override
		public void saveMyContact(Contact myContact) {
			// TODO Auto-generated method stub
		}

		@Override
		public void addMember(Contact myContact, Member member) throws StorageException {
		}

		@Override
		public void addChat(Contact myContact, Chat chat) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addContactToGroup(Contact myContact, Group group,
				Contact another) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addMessageToChat(Contact myContact,Chat chat, Message message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeMyContact(Contact contact) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeMember(Contact myContact, Member member) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeContactFromGroup(Contact myContact, Group group,
				Contact another) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeMessageFromChat(Contact myContact,Chat chat, Message message) {
			// TODO Auto-generated method stub
			
		}
	
}
