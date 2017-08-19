package com.nks.whatsapp;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;


public class IO {

private static String imageFolder="C:\\images";	
	
@SuppressWarnings("unchecked")
static List<Member> getContacts(Contact myContact) throws IOException,ClassNotFoundException
{
ObjectInputStream objIn=null;
try{
File f=new File("profile/"+myContact.getUniqueId());
if(!f.exists())
	//return new ArrayList<Member>();
	return getDummyContacts(myContact,8);
objIn=new ObjectInputStream(new FileInputStream(f));
return (List<Member>)objIn.readObject();
}
finally{try{objIn.close();}catch(Exception ex){}}
}
	
@SuppressWarnings("unchecked")
private static List<Member> getDummyContacts(Contact myContact,int maxContacts)
{
Random random=new Random();
List<Member> contacts=(List<Member>)getDummyContacts(random,maxContacts);
File[] fs=new File(imageFolder).listFiles();
List<File> files=null;
if(fs!=null)
	files=Arrays.asList(fs);

for(Member contact:contacts)
	{
	if(files.size()==0) break;
	try{
		int index=random.nextInt(files.size());
		Image img=ImageIO.read(files.get(index));
		if(img!=null)
			{
			contact.setImag(img);
			files.remove(index);
			}
	}catch(Exception ex){continue;}
	}

Group familyGroup=new Group("My Family",true);
familyGroup.contacts=(List<Contact>) getDummyContacts(random,15);
contacts.add(familyGroup);

for(Member contact:contacts)
	if(random.nextBoolean())
		{
		Message message=null;
		int type=random.nextInt(5);
		switch(type)
		{
		case 1:{message=new AudioMessage(getRandomId(),random.nextBoolean()?myContact.getUniqueId():(contact instanceof Group?((Group)contact).contacts.get(random.nextInt(((Group)contact).contacts.size())).getUniqueId():contact.getUniqueId()));break;}
		case 2:{message=new VideoMessage(getRandomId(),random.nextBoolean()?myContact.getUniqueId():(contact instanceof Group?((Group)contact).contacts.get(random.nextInt(((Group)contact).contacts.size())).getUniqueId():contact.getUniqueId()));break;}
		case 3:{message=new PhotoMessage(getRandomId(),random.nextBoolean()?myContact.getUniqueId():(contact instanceof Group?((Group)contact).contacts.get(random.nextInt(((Group)contact).contacts.size())).getUniqueId():contact.getUniqueId()));break;}
		default:{message=new TextMessage(getRandomId(),random.nextBoolean()?myContact.getUniqueId():(contact instanceof Group?((Group)contact).contacts.get(random.nextInt(((Group)contact).contacts.size())).getUniqueId():contact.getUniqueId()),getRandomSentence(random));break;}
		}
		MessageStatus status=null;
		type=random.nextInt(6);
		switch(type)
		{
		case 1:{status=new MessageStatus(myContact.getUniqueId(),MessageStatus.Type.DELIVERED,getRandomDate(random));break;}
		case 2:{status=new MessageStatus(myContact.getUniqueId(),MessageStatus.Type.PENDING,getRandomDate(random));break;}
		case 3:{
			if(contact instanceof Contact)
				{status=new MessageStatus(contact.getUniqueId(),MessageStatus.Type.RECIEVED,getRandomDate(random));break;}
			else
				{status=new MessageStatus(((Group)contact).contacts.get(random.nextInt(((Group)contact).contacts.size())).getUniqueId(),MessageStatus.Type.RECIEVED,getRandomDate(random));break;}
			}
		case 4:{status=new MessageStatus(myContact.getUniqueId(),MessageStatus.Type.SEEN,getRandomDate(random));break;}
		default:{status=new MessageStatus(myContact.getUniqueId(),MessageStatus.Type.SENT,getRandomDate(random));break;}
		}
		message.addStatus(status);
		contact.setLastMessage(message);
		}

return contacts;
}

private static String getRandomId(){
	return Long.toString(new Date().getTime());
}
private static Date getRandomDate(Random random)
{

return new GregorianCalendar(2007+random.nextInt(9),1+random.nextInt(12),1+random.nextInt(31)).getTime();	
}
private static List<? extends Member> getDummyContacts(Random random,int maxContacts)
{
List<Member> contacts=new ArrayList<Member>();
	for(int i=0;i<maxContacts;i++)
	{
		Contact contact=new Contact(getRandomName(random,8,5)+" "+getRandomName(random,8,5),"+"+random.nextInt(999)+" "+random.nextInt(999)+" "+random.nextInt(9999999),random.nextBoolean());
		contact.setLastSeen(getRandomDate(random));
		contacts.add(contact);
	}
	return contacts;
}

private static Chat getDummyChat(Contact myContact,Member member)
{
Chat chat=new Chat(myContact,member);
Random random=new Random();
for(int i=0;i<random.nextInt(30);i++)
{
	boolean myMessage=random.nextBoolean();
	Message message=new TextMessage(getRandomId(),myMessage?myContact.getUniqueId():(member instanceof Group?((Group)member).contacts.get(random.nextInt(((Group)member).contacts.size())).getUniqueId():member.getUniqueId()),getRandomSentence(random));
	if(!myMessage)
	chat.messageRecieved(member.getUniqueId(), message,getRandomDate(random));
	else
	chat.sendMessage(message);
}
chat.sortMessages(Util.getSortByTimeComparator(false));
return chat;
}
private static String getRandomSentence(Random random)
{
int randomLength=random.nextInt(20);
StringBuilder sb=new StringBuilder();
for(int i=0;i<randomLength;i++)
	sb.append(getRandomName(random,10,0)+" ");
return sb.toString();	
}



private static String getRandomName(Random random,int maxAlphabets,int minimumAlphabets)
{
String[] alphabets=new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
StringBuilder sb=new StringBuilder();
for(int i=0;i<Math.max(random.nextInt(maxAlphabets),minimumAlphabets);i++)
	sb.append(i==0?alphabets[random.nextInt(alphabets.length)].toUpperCase():alphabets[random.nextInt(alphabets.length)]);
return sb.toString().toString();
}

static void saveContacts(Contact myContact,List<Member> contacts) throws IOException,ClassNotFoundException
{
ObjectOutputStream objOut=null;
try{
File path=new File("profile");
if(!path.exists())
	{if(!path.mkdir()) throw new IOException("Unable to create directory 'profile' make sure you have proper rights on "+new File("").getAbsolutePath());}
else if (path.isFile())
	throw new IOException("Directory 'profile' couldn't be found at "+new File("").getAbsolutePath()+", a file with same name already exists. please remove and try again");
File f=new File("profile/"+myContact.getUniqueId());
if(!f.exists())
	if(!f.createNewFile()) throw new IOException("Unable to create file "+f.getName()+" make sure you have proper rights on "+new File("").getAbsolutePath());
objOut=new ObjectOutputStream(new FileOutputStream(f));
objOut.writeObject(contacts);
}
finally{try{objOut.close();}catch(Exception ex){}}
}


static Chat getChat(Contact myContact,Member member) throws IOException
{
ObjectInputStream objIn=null;
try{
File f=new File("chats/"+myContact.getUniqueId()+"/"+member.getUniqueId());
if(!f.exists())
	//return new Chat(myContact,member);
	return getDummyChat(myContact,member);
objIn=new ObjectInputStream(new FileInputStream(f));
return (Chat)objIn.readObject();
}
catch(ClassNotFoundException cnEx){return null;}
finally{try{objIn.close();}catch(Exception ex){}}
}
	
static void saveChat(Contact myContact,Chat chat) throws IOException,ClassNotFoundException
{
ObjectOutputStream objOut=null;
try{
File path=new File("chats/"+myContact.getUniqueId());
if(!path.exists())
	{if(!path.mkdir()) throw new IOException("Unable to create directory 'chats/"+myContact.getUniqueId()+"' make sure you have proper rights on "+new File("").getAbsolutePath());}
else if (path.isFile())
	throw new IOException("Directory 'chats/"+myContact.getUniqueId()+"' couldn't be found at "+new File("").getAbsolutePath()+", a file with same name already exists. please remove and try again");
File f=new File("chats/"+myContact.getUniqueId()+"/"+chat.getMember().getUniqueId());
if(!f.exists())
	if(!f.createNewFile()) throw new IOException("Unable to create file "+f.getName()+" make sure you have proper rights on "+new File("").getAbsolutePath());
objOut=new ObjectOutputStream(new FileOutputStream(f));
objOut.writeObject(chat);
}
finally{try{objOut.close();}catch(Exception ex){}}
}


}
