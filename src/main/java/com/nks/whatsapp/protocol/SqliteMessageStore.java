package com.nks.whatsapp.protocol;

import java.util.Map;

public class SqliteMessageStore implements MessageStoreInterface{

	
	public SqliteMessageStore(String number,String customPat){
		// TODO Auto-generated method stub
			
	}
	
	public SqliteMessageStore(String number){
		this(number,null);
	}
	@Override
	public void saveMessage(String from, String to, String text, String id,
			String t) {
		// TODO Auto-generated method stub
		
	}

	public void setPending(String id,String jId){
		
	}

	public Map<String,Object>[] getPending(String jId){
		//return new HashMap<String,Object>[]{new HashMap<String,Object>()};
		return null;
	}
}
