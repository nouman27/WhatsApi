package com.nks.whatsapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.nks.whatsapp.event.EventUtility;
import com.nks.whatsapp.event.WhatsAppListener;

public class WhatsApp implements EventEntity, WhatsAppListener {

	private WhatsAppStorage storage;

	private Contact myContact;

	private Map<String, Member> members;
	private Map<String, Chat> chats;
	private transient List<WhatsAppListener> listeners = new ArrayList<WhatsAppListener>();

	WhatsApp(WhatsAppStorage storage, Contact myContact) {
		this.myContact = myContact;
		this.addWhatsAppListener(this);
		this.storage = storage;
	}

	public Map<String, Member> getMembers() {
		if (members == null)
			try {
				members = new Hashtable<String, Member>();
				List<Member> storageMembers = storage.getContacts(myContact);
				for (Member member : storageMembers)
					members.put(member.getUniqueId(), member);
			} catch (StorageException strEx) {
				members = null;
				throw new RuntimeException(strEx);
			}
		return members;
	}

	public Map<String, Chat> getChats() {
		if (chats == null)
			try {
				chats = new Hashtable<String, Chat>();
				List<Chat> storageChats = storage.getChats(this.myContact);
				for (Chat chat : storageChats)
					chats.put(chat.getMember().getUniqueId(), chat);
			} catch (StorageException strEx) {
				chats = null;
				throw new RuntimeException(strEx);
			}
		return chats;
	}

	public void addMember(Member member) {
		for (WhatsAppListener listener : listeners)
			listener.memberAdded(member);
		members.put(member.getUniqueId(), member);
	}

	public void addChat(Chat chat) {
		for (WhatsAppListener listener : listeners)
			listener.chatAdded(chat);
		chats.put(chat.getUniqueId(), chat);
	}

	public Member getMember(String uniqueId) {
		return this.members.get(uniqueId);
	}

	public Contact getMyContact() {
		return myContact;
	}

	public Chat getChat(Member member) throws IOException {
		return IO.getChat(myContact, member);
	}

	public List<WhatsAppListener> getListeners() {
		return listeners;
	}

	public void addWhatsAppListener(WhatsAppListener listener) {
		this.listeners.add(listener);
	}

	public void removeWhatsAppListener(WhatsAppListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListenerHirearchy() {
		for (int i = listeners.size(); i == 0; i--)
			listeners.remove(i);
		myContact.removeListenerHirearchy();
		if (members != null)
			for (Member member : members.values())
				member.removeListenerHirearchy();
		if (chats != null)
			for (Chat chat : chats.values())
				chat.removeListenerHirearchy();
	}

	@Override
	public long getId() {
		return EventUtility.getUniqueListenerId();
	}

	@Override
	public void memberAdded(Member member) {
		try {
			storage.addMember(myContact, member);
		} catch (StorageException strEx) {
			throw new RuntimeException(strEx);
		}
	}

	@Override
	public void chatAdded(Chat chat) {
		try {
			storage.addChat(myContact, chat);
		} catch (StorageException strEx) {
			throw new RuntimeException(strEx);
		}
	}

}
