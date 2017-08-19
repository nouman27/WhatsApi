package com.nks.whatsapp.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;



import com.nks.whatsapp.protocol.events.WhatsApiEventsManager;

public class Registration {
private WhatsApiEventsManager eventManager;
private String phoneNumber;
//private String identity;
private boolean debug;
private PrintStream debugOut=System.out;
private static Map<Integer,Map<String,String>> countries;
private static Map<String,Map<String,String>> networks;

public Registration(String number){
	this(number,false);
}

public Registration(String number,boolean debug){
	this(number,debug,null);
}
public Registration(String number,boolean debug,String customPath){
	this.debug=debug;
	this.phoneNumber=number; 
	this.eventManager=new WhatsApiEventsManager();
	if(dissectPhone()==null)
		throw new RuntimeException("The provided phone number is not valid.");
	}

public static Map<Integer,String> getCountriesList(){
	initCountries();
	Map<Integer,String> countiriesList=new HashMap<Integer,String>();
	for (Integer countryCode:countries.keySet())
		countiriesList.put(countryCode, countries.get(countryCode).get("country"));
return countiriesList;
}

public static List<String> getNetworkList(String countryCode){
	initNetworks();
	List<String> networkList=new ArrayList<String>();
	for (Map<String,String> networkInfo:networks.values())
		if(networkInfo.get("g")!=null && networkInfo.get("g").equals(countryCode))
			networkList.add(networkInfo.get("h"));
return networkList;
}



private static String getIdentity(String identity_file){
	File file=new File(identity_file);
	if(file.canRead()){
		try {
		String data= URLDecoder.decode(Util.file_get_contents(file.toURI()), Util.standardCharset.name());
		int len=data.length();
		if(len==20 || len==16) return data;
		return null;
		} catch (UnsupportedEncodingException e) {throw new RuntimeException("Unable to build identity file "+e.getMessage());}
	}
	else
		return null;
	/*String bytes=Util.openssl_random_pseudo_bytes(20).toLowerCase();
	return bytes;*/
	}

private static boolean deleteIdentity(String identity_file){
	File file=new File(identity_file);
	if(file.exists())
		return file.delete();
	return true;
}


private static void initNetworks(){
	if(networks!=null)
		return;
	BufferedReader br=null;
	try{
		br=new BufferedReader(new InputStreamReader(Registration.class.getResourceAsStream("networkinfo.csv")));
		networks=new HashMap<String,Map<String,String>>();
		String networkLine=null;
		int unknownIndex=0;
		while((networkLine=br.readLine())!=null)
			{
			String[] networkArray=networkLine.split(",");
			Map<String,String> network=new HashMap<String,String>();
			network.put("a", networkArray[0]);
			network.put("b", networkArray[1]);
			network.put("c", networkArray[2]);
			network.put("d", networkArray[3]);
			network.put("e", networkArray[4]);
			network.put("f", networkArray[5]);
			network.put("g", networkArray[6]);
			if(networkArray.length<8)
			{
				network.put("h", "Unknown"+unknownIndex);
				unknownIndex++;
			}
			else
			network.put("h", networkArray[7]);
			networks.put(network.get("e")+"<>"+network.get("h"),network);
			}
	}catch(Exception ex){networks=null;throw new RuntimeException("Unable to load networks info ",ex);}
	finally{try{br.close();}catch(Exception ex){}}

}

private static void initCountries(){
	if(countries!=null)
		return;
BufferedReader br=null;
try{
	br=new BufferedReader(new InputStreamReader(Registration.class.getResourceAsStream("countries.csv")));
	countries=new HashMap<Integer,Map<String,String>>();
	String countryLine=null;
	while((countryLine=br.readLine())!=null)
		{
		String[] countryArray=countryLine.split(",");
		Map<String,String> country=new HashMap<String,String>();
		country.put("country", countryArray[0].replaceAll("\"", ""));
		country.put("cc", countryArray[1].replaceAll("\"", ""));
		country.put("mcc", countryArray[2].replaceAll("\"", ""));
		country.put("ISO3166", countryArray[3].replaceAll("\"", ""));
		country.put("ISO639", countryArray[4].replaceAll("\"", ""));
		country.put("mnc", countryArray[5].replaceAll("\"", ""));
		countries.put(Integer.parseInt(country.get("cc").trim()),country);
		}
}catch(Exception ex){countries=null;throw new RuntimeException("Unable to load countries list",ex);}
finally{try{br.close();}catch(Exception ex){}}
}

public Map<String,String> dissectPhone(){
	return dissectPhone(this.phoneNumber,this.eventManager);
}

public static Map<String,String> dissectPhone(String phoneNumber){
	return dissectPhone(phoneNumber,null);
}


private static Map<String,String> dissectPhone(String phoneNumber,WhatsApiEventsManager eventManager)
{
	initCountries();
	for(Integer cc:countries.keySet())
		if(phoneNumber.indexOf(Integer.toString(cc))==0)
		{
			Map<String,String> country=countries.get(cc);
			Map<String,String > phone = new HashMap<String,String>();
              phone.put("country", country.get("country"));
              phone.put("cc", country.get("cc"));
              phone.put("phone",phoneNumber.substring(Integer.toString(cc).length()));
              phone.put("mcc", country.get("mcc").charAt(0)=='1'?"1":country.get(cc));
              phone.put("ISO3166", country.get("ISO3166"));
              phone.put("ISO639", country.get("ISO639"));
              phone.put("mnc", country.get("mnc"));
              if(eventManager!=null)
            	  eventManager.fire("onDissectPhone",phoneNumber,phone.get("country"),phone.get("cc"),phone.get("phone"),phone.get("mcc"),phone.get("ISO3166"),phone.get("ISO639"),phone.get("mnc"));
              return phone;
		}
    if(eventManager!=null)
    	eventManager.fire("onDissectPhoneFailed",phoneNumber);
    return null;
}

private JsonObject getResponse(String host, Attributes<String> query)
{
	
	try{
	URL url= new URL(host+'?'+Util.http_build_query(query));
	URLConnection connection=url.openConnection();
    connection.setRequestProperty("User-Agent", Constants.WHATSAPP_USER_AGENT);
    connection.setRequestProperty("Content-type","text/json");
    JsonObject json=Json.createReader(connection.getInputStream()).readObject();
    try{connection.getInputStream().close();}catch(Exception ex){}
    return json;
	}catch(Exception ex){throw new RuntimeException("Unable to read json response from "+host,ex);}
    /*
    // Open connection.
    $ch = curl_init();
    // Configure the connection.
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HEADER, 0);
    curl_setopt($ch, CURLOPT_USERAGENT, Constants::WHATSAPP_USER_AGENT);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Accept: text/json']);
    // This makes CURL accept any peer!
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    // Get the response.
    $response = curl_exec($ch);
    // Close the connection.
    curl_close($ch);
    return json_decode($response);*/
}

public boolean debugPrint(Object message)
{
if(debug && debugOut!=null)
	{
	if(message!=null)
		debugOut.println(message);
	else
		debugOut.println("<null>");
	return true;
	}
return false;
}

public String[] codeRequest(String method,String carrier){
String identity=Util.openssl_random_pseudo_bytes(20).toLowerCase();
	JsonObject response=generateCodeRequestResponse(identity,carrier,method,Util.PLATFORM_NOKIA);
	this.debugPrint(response);
	
	String status=response.getString("status");
if (status.equals("ok")){
	eventManager.fire("onCodeRegister",phoneNumber,response.getString("login"),response.getString("pw"),response.getString("type"),response.getString("expiration"),response.getString("kind"),response.getString("price"),response.getString("cost"),response.getString("currency"),response.getString("price_expiration"));
	try {return new String[]{URLEncoder.encode(identity,Util.standardCharset.name()),response.getString("pw")};} catch (UnsupportedEncodingException e) {throw new RuntimeException("Unable to encode identity",e);}
	}
else if (!status.equals("sent")){
		if(response.containsKey("reason") && response.getString("reason").equals("too_recent")){
			eventManager.fire("onCodeRequestFailedTooRecent",phoneNumber,method,response.getString("reason"),response.getInt("retry_after"));
          throw new RuntimeException("Code already sent. Retry after "+Math.round(response.getInt("retry_after")/60)+" minutes.");
     	}
		else if (response.containsKey("reason") && response.getString("reason").equals("too_many_guesses")) {
		eventManager.fire("onCodeRequestFailedTooRecent",phoneNumber,method,response.getString("reason"),response.getInt("retry_after"));
         throw new RuntimeException("Too many guesses. Retry after "+Math.round(response.getInt("retry_after")/60)+" minutes.");
		}	
		else {
        eventManager.fire("onCodeRequestFailed",phoneNumber,method,response.getString("reason"),response.containsKey("param")?response.get("param"):null);
       throw new RuntimeException("There was a problem trying to request the code.");
		}    
	}
else {
    eventManager.fire("onCodeRequest",phoneNumber,method,response.get("length"));
   return new String[]{this.phoneNumber,null};
	}
}

public JsonObject codeRequest(String method,String carrier,int platform,String identity_file)
{
if(identity_file==null){
		identity_file=fileName(this.phoneNumber);
}
if(!deleteIdentity(identity_file))
	throw new RuntimeException("Unable to delete old identity file :"+identity_file);

String identity=Util.openssl_random_pseudo_bytes(20).toLowerCase();

JsonObject response=generateCodeRequestResponse(identity,carrier,method,platform);
this.debugPrint(response);

String status=response.getString("status");
if (status.equals("ok")) 
    {
	eventManager.fire("onCodeRegister",phoneNumber,response.getString("login"),response.getString("pw"),response.getString("type"),response.getString("expiration"),response.getString("kind"),response.getString("price"),response.getString("cost"),response.getString("currency"),response.getString("price_expiration"));
	try {
		if(!Util.file_put_contents(new File(identity_file),URLEncoder.encode(identity,Util.standardCharset.name())))
			throw new RuntimeException("Unable to write identity file to "+identity_file);
		} catch (UnsupportedEncodingException e) {throw new RuntimeException("Unable to build identity file "+e.getMessage());}
    }
else if (!status.equals("sent")){
if(response.containsKey("reason") && response.getString("reason").equals("too_recent")){
    eventManager.fire("onCodeRequestFailedTooRecent",phoneNumber,method,response.getString("reason"),response.getInt("retry_after"));
          throw new RuntimeException("Code already sent. Retry after "+Math.round(response.getInt("retry_after")/60)+" minutes.");
     	}
else if (response.containsKey("reason") && response.getString("reason").equals("too_many_guesses")) {
	
    eventManager.fire("onCodeRequestFailedTooRecent",phoneNumber,method,response.getString("reason"),response.getInt("retry_after"));
          throw new RuntimeException("Too many guesses. Retry after "+Math.round(response.getInt("retry_after")/60)+" minutes.");
    }	
else {
        eventManager.fire("onCodeRequestFailed",phoneNumber,method,response.getString("reason"),response.containsKey("param")?response.get("param"):null);
       throw new RuntimeException("There was a problem trying to request the code.");
    }    
}else {
          eventManager.fire("onCodeRequest",phoneNumber,method,response.get("length"));
      	try {
    		if(!Util.file_put_contents(new File(identity_file),URLEncoder.encode(identity,Util.standardCharset.name())))
    			throw new RuntimeException("Unable to write identity file to "+identity_file);
    		} catch (UnsupportedEncodingException e) {throw new RuntimeException("Unable to build identity file "+e.getMessage());}
	}
return response;
}


private JsonObject generateCodeRequestResponse(String identity,String carrier,String method,int platform){

	Map<String,String> phone=this.dissectPhone();	
	if(phone==null)
		throw new RuntimeException("The provided phone number is not valid.");
	String countryCode=phone.get("ISO3166");if(countryCode==null)countryCode="US";
	String langCode=phone.get("ISO639");if(langCode==null)langCode="en";
	String mnc=null;
	if (carrier != null) {
	    mnc = detectMnc(countryCode.toLowerCase(), carrier);
	} else {
	    mnc = phone.get("mnc");
	}

	String token=Util.generateRequestToken(phone.get("country"), phone.get("phone"), platform);
	Attributes<String> query=new Attributes<String>();
	query.put("cc", phone.get("cc"));
	query.put("in", phone.get("phone"));
	query.put("lg", langCode);
	query.put("lc", countryCode);
	query.put("id", identity);
	query.put("token", token);
	query.put("mistyped", "6");
	//query.put("network_radio_type", "1");
	//query.put("simnum", "1");
	//query.put("s", "");
	//query.put("copiedrc", "1");
	//query.put("hasinrc", "1");
	//query.put("rcmatch", "1");
	query.put("pid", Integer.toString(100+new Random().nextInt(9900)));
	query.put("rchash", Util.hash("sha-256", Util.openssl_random_pseudo_bytes(20)));
	query.put("anhash",Util.hash("md5", Util.openssl_random_pseudo_bytes(20)));
	query.put("extexist","1");
	query.put("extstate","1");
	//query.put("mcc",phone.get("mcc"));
	query.put("mnc",mnc);
	//query.put("sim_mcc",phone.get("mcc"));
	query.put("sim_mnc",mnc);
	query.put("method", method);
	this.debugPrint(query);

	return getResponse("https://"+Constants.WHATSAPP_REQUEST_HOST, query);
}

public String codeRegisterForIdentity(String code,String identity){
	JsonObject response=getCodeRegisterResponse(identity,code);
	this.debugPrint(response);
	String status=response.getString("status");
	if(!status.equals("ok")){
		eventManager.fire("onCodeRegisterFailed",phoneNumber,status,response.get("reason"),response.containsKey("retry_after")?response.get("retry_after"):null);
		if(response.get("reason").equals("old_version")){
			update();
		}
	    throw new RuntimeException("An error occurred registering the registration code from WhatsApp. Reason: "+response.get("reason"));
	}else
	{	
		return response.getString("pw");
	}		
	
}

public JsonObject codeRegister(String code,String identity_file)
{
if(identity_file==null){
		identity_file=fileName(this.phoneNumber);
}
String identity=getIdentity(identity_file);
if(identity==null)
	throw new RuntimeException("Identity file couldn't be found at "+identity_file+" either the file doesn't exists or corrupted, use CodeRequest first to request a code and generate identity");

JsonObject response=getCodeRegisterResponse(identity,code);
this.debugPrint(response);

String status=response.getString("status");
if(!status.equals("ok")){
	eventManager.fire("onCodeRegisterFailed",phoneNumber,status,response.get("reason"),response.containsKey("retry_after")?response.get("retry_after"):null);
	if(response.get("reason").equals("old_version")){
		update();
	}
    throw new RuntimeException("An error occurred registering the registration code from WhatsApp. Reason: "+response.get("reason"));
}else
{	
	
}		
return response;
}

private JsonObject getCodeRegisterResponse(String identity,String code){
	Map<String,String> phone=this.dissectPhone();	
	if(phone==null)
			throw new RuntimeException("The provided phone number is not valid.");

	code=code.replaceAll("-", "");
	String countryCode=phone.get("ISO3166");if(countryCode==null)countryCode="US";
	String langCode=phone.get("ISO639");if(langCode==null)langCode="en";


	//String token=Util.generateRequestToken(phone.get("country"), phone.get("phone"), platform);
	Attributes<String> query=new Attributes<String>();
	query.put("cc", phone.get("cc"));
	query.put("in", phone.get("phone"));
	query.put("lg", langCode);
	query.put("lc", countryCode);
	query.put("id", identity);
	query.put("mistyped", "6");
	//query.put("network_radio_type", "1");
	//query.put("simnum", "1");
	query.put("s", "");
	query.put("copiedrc", "1");
	query.put("hasinrc", "1");
	query.put("rcmatch", "1");
	query.put("pid", Integer.toString(100+new Random().nextInt(9900)));
	query.put("rchash", Util.hash("sha-256", Util.openssl_random_pseudo_bytes(20)));
	query.put("anhash",Util.hash("md5", Util.openssl_random_pseudo_bytes(20)));
	query.put("extexist","1");
	query.put("extstate","1");
	query.put("code", code);
	this.debugPrint(query);
	return getResponse("https://"+Constants.WHATSAPP_REGISTER_HOST, query);

}


public static String fileName(String phoneNumber){
	return String.format("%s%s%sid.%s.dat",new File("").getAbsolutePath(),File.separator,Constants.DATA_FOLDER+File.separator,phoneNumber);	
}

public void update()
{
	String data=null;
	try {data=Util.file_get_contents(new URI(Constants.WHATSAPP_VER_CHECKER));} catch (URISyntaxException e) {data=null;}
    if(data!=null){
	JsonObject wadata = Json.createReader(new StringReader(data)).readObject();
    String waver = wadata.getString("e");
    if (!Constants.WHATSAPP_VER .equals(waver)) {
        Constants.WHATSAPP_VER=waver;
        Constants.WHATSAPP_USER_AGENT="WhatsApp/"+waver+" S40Version/14.26 Device/Nokia302";
    }
   }
}

private String detectMnc(String lc, String carrierName)
{
	initNetworks();
	String mnc = null;
    Map<String,String> network=networks.get(lc+"<>"+carrierName);
		if(network!=null)
			mnc=network.get("c");
	return mnc==null?"000":mnc;
}
public JsonObject checkCredentials(){
	return checkCredentials();
}

public JsonObject checkCredentials(String identity_file)
{
if(identity_file==null){
		identity_file=fileName(this.phoneNumber);
}
String identity=getIdentity(identity_file);
if(identity==null)
	throw new RuntimeException("Identity file couldn't be found at "+identity_file+" either the file doesn't exists or corrupted, use CodeRequest first to request a code and generate identity");

	
	Map<String,String> phone=this.dissectPhone();	
	if(phone==null)
		throw new RuntimeException("The provided phone number is not valid.");
String countryCode=phone.get("ISO3166");if(countryCode==null)countryCode="US";
String langCode=phone.get("ISO639");if(langCode==null)langCode="en";

/*query.put("cc", phone.get("cc"));
query.put("in", phone.get("phone"));
query.put("lg", langCode);
query.put("lc", countryCode);
query.put("id", identity);
query.put("mistyped", "6");
//query.put("network_radio_type", "1");
//query.put("simnum", "1");
query.put("s", "");
query.put("copiedrc", "1");
query.put("hasinrc", "1");
query.put("rcmatch", "1");
query.put("pid", Integer.toString(100+new Random().nextInt(9900)));
query.put("rchash", Util.hash("sha-256", Util.openssl_random_pseudo_bytes(20)));
query.put("anhash",Util.hash("md5", Util.openssl_random_pseudo_bytes(20)));
query.put("extexist","1");
query.put("extstate","1");
query.put("code", code);*/



Attributes<String> query=new Attributes<String>();
query.put("cc", phone.get("cc"));
query.put("in", phone.get("phone"));
query.put("lg", langCode);
query.put("lc", countryCode);
query.put("id", identity);
query.put("mistyped", "6");
query.put("network_radio_type", "1");
query.put("simnum", "1");
query.put("s", "");
query.put("copiedrc", "1");
query.put("hasinrc", "1");
query.put("rcmatch", "1");
query.put("pid", Integer.toString(100+new Random().nextInt(9900)));
query.put("extexist","1");
query.put("extstate","1");

this.debugPrint(query);
JsonObject response=this.getResponse("https://"+Constants.WHATSAPP_CHECK_HOST, query);
this.debugPrint(response);
String status=response.getString("status");
if(!status.equals("ok")){
	this.eventManager.fire("onCredentialsBad", this.phoneNumber,status,response.get("reason"));
	throw new RuntimeException("There was a problem trying to request the code.");
}
else{
	this.eventManager.fire("onCredentialsGood", this.phoneNumber,response.get("login"),response.get("pw"),response.get("type"),response.get("expiration"),response.get("kind"),response.get("price"),response.get("cost"),response.get("currency"),response.get("price_expiration"));
}
	


    return response;
}

public static void main(String[] args){
	Registration reg=new Registration("923214467134",true);
	//System.out.println(Util.bin2hex(reg.identity));
	//reg.checkCredentials();
	reg.codeRequest("sms", "Warid Telecom");
	//reg.codeRequest("voice", "UFONE/PAKTel");
	//reg.codeRegister("281702",null);
	
}

}



