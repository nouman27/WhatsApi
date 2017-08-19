package com.nks.whatsapp.protocol;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

public class Util {

public static final int PLATFORM_ANDROID=0;	
public static final int PLATFORM_NOKIA=1;

private static int[] fromBase64URL =null;
private static int[] fromBase64 = null;

private static final char[] toBase64 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K', 'L', 'M','N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm','n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z','0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
private static final char[] toBase64URL = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M','N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm','n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z','0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};
public static final Charset standardCharset=StandardCharsets.ISO_8859_1;

public static int ord(String input)
	{
	 	if(input.length()>0)
	    	return (int)input.charAt(0);
	    return 0;
	}

public static int ord(char c)
{
 	return (int)c;
    
}

public static String byteToString(byte[] data){
	return new String(data,standardCharset);
}

public static String byteToString(byte[] data,int offSet,int length){
	return new String(data,offSet,length,standardCharset);
}
public static byte[] stringToByte(String data){
	return data.getBytes(standardCharset);
}

public static int array_search(String input,String[] array)
{
for(int i=0;i<array.length;i++)
	if(array[i].equals(input))
		return i;
return 0;
}


public static String bin2hex(String asciiValue)
{
    char[] chars = asciiValue.toCharArray();
    StringBuffer hex = new StringBuffer();
    for (int i = 0; i < chars.length; i++)
      hex.append(dechex((int) chars[i]));
    return hex.toString();
}

public static String hex2bin(String hexValue)
{
	StringBuilder output = new StringBuilder("");
    for (int i = 0; i < hexValue.length(); i += 2)
    {
        String str = hexValue.substring(i, i + 2);
        output.append((char) Integer.parseInt(str, 16));
    }
    return output.toString();
}

public static String gzuncompress(String input)
{

	//TODO implement	
return input;
}
 
public static int[] range(int lower,int higher)
{
int[] out=new int[higher-lower+1];
for (int i=0;i<out.length;i++)
	out[i]=i+lower;
return out;
}

public static void main(String[] args)
{
	FileInputStream fileInput=null;
try{
	fileInput=new FileInputStream(new File("C:\\Desert.jpg"));
	byte[] data=new byte[fileInput.available()];
	fileInput.read(data);
	System.out.println(createIcon(byteToString(data)));
}catch(Exception ex){ex.printStackTrace();System.exit(0);}
finally{try{fileInput.close();}catch(Exception ioEX){}}
}

public static String pack(String format,Object data)
{
//TODO implement this
return new String(new char[]{(char)0,(char)0,(char)0,(char)1});	
}


public static String wa_pbkdf2(String algorithm, String password, String salt, int count, int key_length)
{
return wa_pbkdf2(algorithm,password,salt,count,key_length, false);
}


public static String wa_pbkdf2(String algorithm, String password, String salt, int count, int key_length, boolean raw_output ) 
{
	
	algorithm = algorithm.toLowerCase();
    MessageDigest md=null;
    try{md=MessageDigest.getInstance(algorithm);}catch(NoSuchAlgorithmException nsEx){throw new java.lang.IllegalArgumentException("PBKDF2 ERROR: Invalid hash algorithm.");}
    if (count <= 0 || key_length <= 0) {
    	throw new java.lang.IllegalArgumentException("PBKDF2 ERROR: Invalid parameters.");
    }
   
    int hash_length = md.getDigestLength();
    float block_count = (float) Math.ceil(key_length / hash_length);
    StringBuilder output = new StringBuilder();
    for (int i = 1; i <= block_count; i++) {
        String last = salt+pack("N", i);
        String xorsum=null;
        last = xorsum = hash_hmac(algorithm, last, password,true);
       
        for (int j = 1; j < count; j++) {
        	last=hash_hmac(algorithm, last, password, true);
        	xorsum=xorStrings(xorsum,last);
        }
        output.append(xorsum);
    }
    if (raw_output) {
        return output.toString().substring(0, key_length);
    } else {
        return bin2hex(output.toString().substring(0, key_length));
    }
    
	
}


private static String xorStrings(String a1, String a2) {
    byte[] a=stringToByte(a1);
    byte[] b=stringToByte(a2);
    
    byte[] out = new byte[a.length];
    for (int i = 0; i < a.length; i++) {
        out[i] = (byte) (a[i] ^ b[i%b.length]);
    }
    return byteToString(out,0,out.length);
    
}

public static String hash(MessageDigest md,String data)
{
return hash(md,data,false);
}

public static String hash(String algorithm,String data){
return hash(algorithm,data,false);
}

public static String hash(String algorithm,String data,boolean raw_output)
{
	algorithm = algorithm.toLowerCase();
    MessageDigest md=null;
    try{md=MessageDigest.getInstance(algorithm);}catch(NoSuchAlgorithmException nsEx){throw new java.lang.IllegalArgumentException("hash ERROR: Invalid hash algorithm. "+algorithm);}
    return hash(md,data,raw_output);
}

public static String hash(MessageDigest md,String data,boolean raw_output)
{
md.update(stringToByte(data));
byte[] digest=md.digest();
if(raw_output)
	return byteToString(digest,0,digest.length);
else
	return new BigInteger(1,digest).toString(16);
}

public static String hash_hmac(String algorithm , String data , String key){
	return hash_hmac(algorithm ,data,key,false);
}


public static String hash_hmac(String algorithm , String data , String key , boolean raw_output )
{
Mac mac=hash_init(algorithm,key);
hash_update(mac,data);
return hash_final(mac,raw_output);
}

public static Mac hash_init(String algorithm,String key)
{
SecretKeySpec signingKey=new SecretKeySpec(stringToByte(key),"Hmac"+algorithm);	
Mac mac=null;
try{mac=Mac.getInstance("Hmac"+algorithm);}catch(NoSuchAlgorithmException nsEx){throw new java.lang.IllegalArgumentException("HASH HMAC: Invalid hash algorithm.");}
try{mac.init(signingKey);}catch(InvalidKeyException invEx){throw new java.lang.IllegalArgumentException("HASH HMAC: Invalid key.");}
return mac;
}

public static void hash_update(Mac mac,String data)
{
mac.update(stringToByte(data));
}

public static String hash_final(Mac mac,boolean raw_output)
{
byte[] rawHmac=mac.doFinal();
if(raw_output)
	return byteToString(rawHmac,0,rawHmac.length);
else
	return new BigInteger(1,rawHmac).toString(16);
}

public static String intArrayToString(int[] array)
{
StringBuilder sb=new StringBuilder();
for(int i=0;i<array.length;i++)
	sb.append(array[i]);
return sb.toString();
}

public static String extractNumber(String from)
{
    return from.replace("@s.whatsapp.net", "").replace("@g.us", "");
}

public static String pkcs5_unpad(String text)
{
    int pad = Util.ord(text.charAt(text.length()-1));
    if (pad > text.length()) {
        return null;
    }
    if (text.substring(text.length()-pad).indexOf((char)pad) != pad)
        return null;
    return text.substring(0,-1*pad);
}

private static int outLength(byte[] src, int sp, int sl,boolean isURL,boolean isMIME) {
initBase64();
int[] base64 = isURL ? fromBase64URL : fromBase64;
int paddings = 0;
int len = sl - sp;
if (len == 0)
return 0;
if (len < 2) {
if (isMIME && base64[0] == -1)
return 0;
throw new IllegalArgumentException("Input byte[] should at least have 2 bytes for base64 bytes");
}
if (isMIME) {
int n = 0;
while (sp < sl) {
int b = src[sp++] & 0xff;
 if (b == '=') {
 len -= (sl - sp + 1);
  break;
 }
 if ((b = base64[b]) == -1)
  n++;
 }
 len -= n;
} else {
if (src[sl - 1] == '=') {
 paddings++;
 if (src[sl - 2] == '=')
  paddings++;
     }
 }
if (paddings == 0 && (len & 0x3) !=  0)
 paddings = 4 - (len & 0x3);
return 3 * ((len + 3) / 4) - paddings;
}

private static  int outLength(int srclen,byte[] newline,int linemax,boolean doPadding) {
int len = 0;
if (doPadding) {
len = 4 * ((srclen + 2) / 3);
} else {
int n = srclen % 3;
 len = 4 * (srclen / 3) + (n == 0 ? 0 : n + 1);
 }
if (linemax > 0)                                  // line separators
 len += (len - 1) / linemax * newline.length;
 return len;
 }


//BASE64 MIME RFC2045
public static String base64_decode(String data)
{
	return base64_decode(data,false);
}
//BASE64 MIME RFC2045
public static String base64_decode(String data,boolean strict)
{
	return base64_decode(data,strict,false,true);
}


public static String base64_decode(String data,boolean strict,boolean isURL,boolean isMIME)
{
	byte[] src= stringToByte(data);
	byte[] dst = new byte[outLength(src, 0, data.length(),isURL,isMIME)];
	 int ret = base64_decode(src, 0, src.length, dst,strict,isURL,isMIME);
	 if (ret != dst.length) {
	 dst = Arrays.copyOf(dst, ret);
	 }
	 return byteToString(dst,0,dst.length);
}

private static void initBase64()
{
	if(fromBase64URL==null) 
	{
		fromBase64 = new int[256];	
		fromBase64URL = new int[256];
	
		Arrays.fill(fromBase64, -1);
		for (int i = 0; i < toBase64.length; i++)
		fromBase64[toBase64[i]] = i;
		fromBase64['='] = -2;
	
		  Arrays.fill(fromBase64URL, -1);
		  for (int i = 0; i < toBase64URL.length; i++)
		  fromBase64URL[toBase64URL[i]] = i;
		  fromBase64URL['='] = -2;
	}
}

public static int base64_decode(byte[] src, int sp, int sl, byte[] dst,boolean strict,boolean isURL,boolean isMIME)
{
	initBase64();
	int[] base64 = isURL ? fromBase64URL : fromBase64;
	int dp = 0;
	int bits = 0;
	int shiftto = 18;       // pos of first byte of 4-byte atom
	while (sp < sl) {
	int b = src[sp++] & 0xff;
	if ((b = base64[b]) < 0) {
	if (b == -2) {         // padding byte '='
	if (shiftto == 6 && (sp == sl || src[sp++] != '=') ||shiftto == 18) {
	throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit");
	}
	break;
	}
	if (isMIME)    // skip if for rfc2045
	continue;
	else
	if(strict)
		throw new IllegalArgumentException("Illegal base64 character " +Integer.toString(src[sp - 1], 16));
	}
	bits |= (b << shiftto);
	shiftto -= 6;
	if (shiftto < 0) {
	dst[dp++] = (byte)(bits >> 16);
	dst[dp++] = (byte)(bits >>  8);
	dst[dp++] = (byte)(bits);
	shiftto = 18;
	bits = 0;
	}
	}
	// reached end of byte array or hit padding '=' characters.
	if (shiftto == 6) {
	dst[dp++] = (byte)(bits >> 16);
	      } else if (shiftto == 0) {
	             dst[dp++] = (byte)(bits >> 16);
	             dst[dp++] = (byte)(bits >>  8);
	           } else if (shiftto == 12) {
	               throw new IllegalArgumentException("Last unit does not have enough valid bits");
	            }
	           while (sp < sl) {
	               if (isMIME && base64[src[sp++]] < 0)
	                   continue;
	                throw new IllegalArgumentException("Input byte array has incorrect ending byte at " + sp);
	           }
	            return dp;
}

//BASE64 MIME RFC 2045

public static String base64_encode(String data)
{
return 	base64_encode(data,false,new byte[] {'\r', '\n'},76,true);
}


public static String base64_encode(String data,boolean isURL,byte[] newline,int linemax,boolean doPadding)
{
	byte[] src= stringToByte(data);
	byte[] dst=new byte[outLength(src.length,newline,linemax,doPadding)];
	int ret = base64_encode(src, 0, src.length, dst,isURL,newline,linemax,doPadding);
	if (ret != dst.length)
	{
		
		byte[] t=Arrays.copyOf(dst, ret);
		return byteToString(t,0,t.length);
	}
	else
		return byteToString(dst,0,dst.length);
		
}


public static int base64_encode(byte[] src,int off,int end,byte[] dst,boolean isURL,byte[] newline,int linemax,boolean doPadding)
{
char[] base64 = isURL ? toBase64URL : toBase64;
int sp = off;
int slen = (end - off) / 3 * 3;
int sl = off + slen;
    if (linemax > 0 && slen  > linemax / 4 * 3)
    	slen = linemax / 4 * 3;
     int dp = 0;
   while (sp < sl) {
      int sl0 = Math.min(sp + slen, sl);
      for (int sp0 = sp, dp0 = dp ; sp0 < sl0; ) {
         int bits = (src[sp0++] & 0xff) << 16 |(src[sp0++] & 0xff) <<  8 |(src[sp0++] & 0xff);
         		dst[dp0++] = (byte)base64[(bits >>> 18) & 0x3f];
    dst[dp0++] = (byte)base64[(bits >>> 12) & 0x3f];
    dst[dp0++] = (byte)base64[(bits >>> 6)  & 0x3f];
  dst[dp0++] = (byte)base64[bits & 0x3f];
    }
     int dlen = (sl0 - sp) / 3 * 4;
     dp += dlen;
   sp = sl0;
       if (dlen == linemax && sp < end) {
       for (byte b : newline){
            dst[dp++] = b;
        }
     }
 }
if (sp < end) {               // 1 or 2 leftover bytes
  int b0 = src[sp++] & 0xff;
  dst[dp++] = (byte)base64[b0 >> 2];
  if (sp == end) {
   dst[dp++] = (byte)base64[(b0 << 4) & 0x3f];
     if (doPadding) {
         dst[dp++] = '=';
     dst[dp++] = '=';
    }
  } else {
   int b1 = src[sp++] & 0xff;
   dst[dp++] = (byte)base64[(b0 << 4) & 0x3f | (b1 >> 4)];
  dst[dp++] = (byte)base64[(b1 << 2) & 0x3f];
   if (doPadding) {
      dst[dp++] = '=';
   }
  }
 }
 return dp;
 }	


private static String str_repeat(char c,int length)
{
char[] array=new char[length];
Arrays.fill(array, c);
return new String(array);
}

public static String generateRequestToken(String country, String phone,int platform)
{
    switch (platform) {
    case PLATFORM_ANDROID:
      String signature = "MIIDMjCCAvCgAwIBAgIETCU2pDALBgcqhkjOOAQDBQAwfDELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFDASBgNVBAcTC1NhbnRhIENsYXJhMRYwFAYDVQQKEw1XaGF0c0FwcCBJbmMuMRQwEgYDVQQLEwtFbmdpbmVlcmluZzEUMBIGA1UEAxMLQnJpYW4gQWN0b24wHhcNMTAwNjI1MjMwNzE2WhcNNDQwMjE1MjMwNzE2WjB8MQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEUMBIGA1UEBxMLU2FudGEgQ2xhcmExFjAUBgNVBAoTDVdoYXRzQXBwIEluYy4xFDASBgNVBAsTC0VuZ2luZWVyaW5nMRQwEgYDVQQDEwtCcmlhbiBBY3RvbjCCAbgwggEsBgcqhkjOOAQBMIIBHwKBgQD9f1OBHXUSKVLfSpwu7OTn9hG3UjzvRADDHj+AtlEmaUVdQCJR+1k9jVj6v8X1ujD2y5tVbNeBO4AdNG/yZmC3a5lQpaSfn+gEexAiwk+7qdf+t8Yb+DtX58aophUPBPuD9tPFHsMCNVQTWhaRMvZ1864rYdcq7/IiAxmd0UgBxwIVAJdgUI8VIwvMspK5gqLrhAvwWBz1AoGBAPfhoIXWmz3ey7yrXDa4V7l5lK+7+jrqgvlXTAs9B4JnUVlXjrrUWU/mcQcQgYC0SRZxI+hMKBYTt88JMozIpuE8FnqLVHyNKOCjrh4rs6Z1kW6jfwv6ITVi8ftiegEkO8yk8b6oUZCJqIPf4VrlnwaSi2ZegHtVJWQBTDv+z0kqA4GFAAKBgQDRGYtLgWh7zyRtQainJfCpiaUbzjJuhMgo4fVWZIvXHaSHBU1t5w//S0lDK2hiqkj8KpMWGywVov9eZxZy37V26dEqr/c2m5qZ0E+ynSu7sqUD7kGx/zeIcGT0H+KAVgkGNQCo5Uc0koLRWYHNtYoIvt5R3X6YZylbPftF/8ayWTALBgcqhkjOOAQDBQADLwAwLAIUAKYCp0d6z4QQdyN74JDfQ2WCyi8CFDUM4CaNB+ceVXdKtOrNTQcc0e+t";
      String classesMd5 = "14w/wF67XBf2vTc+qALwKQ=="; // 2.16.148
      String key2 = base64_decode("eQV5aq/Cg63Gsq1sshN9T3gh+UUp0wIw0xgHYT1bnCjEqOJQKCRrWxdAe2yvsDeCJL+Y4G3PRD2HUF7oUgiGo8vGlNJOaux26k+A2F3hj8A=");
       String data = base64_decode(signature)+base64_decode(classesMd5)+phone;
       StringBuilder opad = new StringBuilder(str_repeat((char)0x5C, 64));
      StringBuilder ipad = new StringBuilder(str_repeat((char)0x36, 64));
      for (int i = 0; i < 64; i++) {
          opad.setCharAt(i,(char)(opad.charAt(i) ^ key2.charAt(i)));
          ipad.setCharAt(i,(char)(ipad.charAt(i) ^ key2.charAt(i)));
       }
       String output = hash("sha1", opad.toString()+hash("sha1", ipad.toString()+data, true), true);
       return base64_encode(output);
    case PLATFORM_NOKIA:
      String const_ = "PdA2DJyKoUrwLw1Bg6EIhzh502dF9noR9uFCllGk";
      String releaseTime = "1452554789539"; // 2.13.30
      String token = hash("md5",const_+releaseTime+phone,false);
      return token;
      default: throw new java.lang.IllegalArgumentException("given platform is not supported "+platform);
    }
}

public static String implode(String val,String[] array)
{
	StringBuilder ret=new StringBuilder();
	for(int i=0;i<array.length;i++)
		{
		if(i!=0)
			ret.append(val);
		ret.append(array[i]);
		}
	return ret.toString();
}

public static String hexToString(String hex)
{
	StringBuilder output = new StringBuilder();
    for (int i = 0; i < hex.length(); i+=2) {
        String str = hex.substring(i, i+2);
        output.append((char)Integer.parseInt(str, 16));
    }
	return output.toString();
	//return (char)(Integer.parseInt(hex,16));	
}

public static long time()
{
return System.currentTimeMillis()/1000;	
}

public static String file_get_contents(URI uri){
BufferedReader br=null;
StringBuilder sb=new StringBuilder();
try{
	br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(uri))));	
	String line=null;
	while((line=br.readLine())!=null)
		sb.append(line);
	return sb.toString();
}catch(IOException ex){return null;}	
finally{try{br.close();}catch(Exception ex){}}
}

public static boolean file_put_contents(File file,String data){
BufferedWriter bw=null;
try{
	bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));	
	bw.write(data);
	return true;
}catch(IOException ex){return false;}	
finally{try{bw.close();}catch(Exception ex){}}
}

public static <K extends Comparable<K>>  String http_build_query(Attributes<K> query){
	StringBuilder sb=new StringBuilder();
	int index=0;
	for(K key:query.keys()){
		if(index!=0)
			sb.append("&");
	try {sb.append(key+"="+URLEncoder.encode(query.get(key).toString(), "UTF-8"));} catch (UnsupportedEncodingException e) {throw new RuntimeException("Unable to build query "+e.getMessage(),e);}
	index++;
	}

return sb.toString();
}

public static String openssl_random_pseudo_bytes(int length){
	byte[] b=new byte[20];
	SecureRandom random=null; 
	try {random= SecureRandom.getInstance("SHA1PRNG");} catch (NoSuchAlgorithmException e) {throw new RuntimeException("Unable to build identity file because unable to generate random number "+e.getMessage());}
	random.nextBytes(b); 
	return byteToString(b);
}

public static String mcrypt_create_iv(int size){
	SecureRandom rnd = new SecureRandom();
	byte[] toIv=new byte[size];
	rnd.nextBytes(toIv);
	IvParameterSpec spec=new IvParameterSpec(toIv);
	return byteToString(spec.getIV());
	
}

public static boolean isAsciiPrintable(String str) {
    if (str == null) {
        return false;
    }
    int sz = str.length();
    for (int i = 0; i < sz; i++) {
        if (isAsciiPrintable(str.charAt(i)) == false) {
            return false;
        }
    }
    return true;
}

public static boolean isAsciiPrintable(char ch) {
    return ch >= 32 && ch < 127;
}
public static String dechex(int dec){
	return String.format("%02x", dec);
}


public static String hash_file(String algorithm,byte[] data,boolean raw_output){
	algorithm = algorithm.toLowerCase();
    MessageDigest md=null;
    try{md=MessageDigest.getInstance(algorithm);}catch(NoSuchAlgorithmException nsEx){throw new java.lang.IllegalArgumentException("hash ERROR: Invalid hash algorithm. "+algorithm);}
    byte[] digest=md.digest(data);

    if(raw_output)
    	return byteToString(digest,0,digest.length);
    else
    	return new BigInteger(1,digest).toString(16);

}
public static String createIcon(String mediaData){
	return createIcon(mediaData,100);
}

public static String createIcon(String mediaData,int size){
	String imageData=createImage(mediaData,size);
	return imageData==null?base64_decode("'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'"):imageData;
}

public static String createImage(String mediaData,int size){
	try{
		byte[] imageData=stringToByte(mediaData);
		BufferedImage actualImage=ImageIO.read(new ByteArrayInputStream(imageData));
		int width=actualImage.getWidth();
		int height=actualImage.getHeight();
		int newWidth=0;
		int newHeight=0;
		if(width>height)
			{
			newWidth=size;
			newHeight=(int)(((double)height/(double)width)*size);
			}
		else
			{
			newHeight=size;
			newWidth=(int)(((double)width/(double)height)*size);
			}
	Image scaledImage=actualImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
	BufferedImage newImage=new BufferedImage(newWidth,newHeight,BufferedImage.TYPE_4BYTE_ABGR);
	((Graphics2D)newImage.getGraphics()).drawImage(scaledImage, null,null);
	return byteToString(((DataBufferByte)newImage.getData().getDataBuffer()).getData());
	}catch(Exception ex){return null;}

}

public static String createVideoIcon(String mediaData){
	//TODO
	return "";
}

}