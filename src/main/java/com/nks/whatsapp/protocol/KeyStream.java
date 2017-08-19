package com.nks.whatsapp.protocol;

import javax.crypto.Mac;

public class KeyStream {

public static String AuthMethod = "WAUTH-2"; 
private static final int DROP = 768;

private int seq=0;
private String macKey;
private RC4 rc4;

	public KeyStream(String key,String macKey)
	{
		rc4=new RC4(key,DROP);
		this.macKey=macKey;
	}

	public static String[] generateKeys(String password,String nonce)
	{
		String[] array=new String[]{"key","key","key","key"};
		int[] array2=new int[]{1,2,3,4};
		StringBuilder nonce1=new StringBuilder(nonce);
		nonce1.append('0');
		for(int j=0;j<array.length;j++){
			nonce1.setCharAt(nonce.length(),(char)array2[j]);
			String foo=Util.wa_pbkdf2("sha1", password, nonce1.toString(), 2, 20,true);
			array[j]=foo;
		}
	return array;		
	}
	
	public String decodeMessage(String buffer, int macOffset, int offset, int length)
    {
		String mac = computeMac(buffer, offset, length);
      	//validate mac
        for (int i = 0; i < 4; i++) {
            int foo = Util.ord(buffer.charAt(macOffset+i));
            int bar = Util.ord(mac.charAt(i));
            if (foo != bar) {
                throw new RuntimeException("MAC mismatch: "+foo+" !="+bar);
            }
        }
        return rc4.cipher(buffer, offset, length);
    }

	public String encodeMessage(String buffer, int macOffset, int offset, int length)
    {
		
		String data = rc4.cipher(buffer, offset, length);
     	String mac = computeMac(data, offset, length);
     	return data.substring(0, macOffset)+mac.substring(0,4)+((data.length()>=macOffset+4)?data.substring(macOffset+4):"");
     	
    }
	
	 private String computeMac(String buffer, int offset, int length)
	    {
		 
		 Mac hmac = Util.hash_init("sha1", macKey);
	     String sb=buffer.substring(offset,offset+length);   
	   	 Util.hash_update(hmac,sb );
	        String array = new String(new char[]{(char)(seq >> 24),(char)(seq >> 16),(char)(seq >> 8),(char)(seq)});
	        Util.hash_update(hmac, array);
	        seq++;
	       return Util.hash_final(hmac, true);
	           
	    }

	 
	 
}
