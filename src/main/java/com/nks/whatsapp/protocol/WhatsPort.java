package com.nks.whatsapp.protocol;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;

import com.nks.whatsapp.protocol.events.WhatsApiEventsManager;
import com.nks.whatsapp.protocol.handlers.Handler;
import com.nks.whatsapp.protocol.handlers.IqHandler;
import com.nks.whatsapp.protocol.handlers.MessageHandler;
import com.nks.whatsapp.protocol.handlers.NotificationHandler;


public class WhatsPort {

private static final int MAX_MEDIA_SIZE=5242880;	
	
public BinTreeNodeReader reader;
public BinTreeNodeWriter writer;

String phoneNumber; 
String password;

private String loginStatus;
private Socket socket;
private KeyStream outputKey;
private boolean replaceKey;
private int iqCounter=0;

private Attributes<String> nodeId;

private String challengeFilename;
private String challengeData;
private String dataFolder;
private String messageId;
private SqliteMessageStore messageStore;
private String name;

private long timeout=0;
private String serverReceivedId;

private boolean debug=false; 
private PrintStream debugOut=System.out;

public boolean log;
public Logger logger;
public Map<String,Integer> retryCounters;

private WhatsApiEventsManager eventManager;
private Map<String,Boolean> v1Only=new HashMap<String,Boolean>();

private Attributes<String> mediaQueue; 



public static void main(String[] args){
	WhatsPort port=new WhatsPort("923214467134","mani",true,true);
	if(!port.connect())
		{
		System.out.println("Unable to connect. exiting...");
		System.exit(0);
		}
	try{port.loginWithPassword("5b/sV0cOHSI+0UwvIrYly1eEe34=");}catch(Exception ex){ex.printStackTrace();System.exit(0);}
	//port.waitForServer(port.sendMessage("923458508206", "hello"));
	FileInputStream fileInput=null;
	try{
	fileInput=new FileInputStream(new File("C:\\Desert.jpg"));
	byte[] data=new byte[fileInput.available()];
	fileInput.read(data);
	port.sendImage(new String[]{"923214467134"}, data);
	}catch(Exception ex){ex.printStackTrace();System.exit(0);}
	finally{try{fileInput.close();}catch(Exception ioEX){}}

}


public WhatsPort(String number,String nickName,boolean debug,boolean log){
	this(number,nickName,debug,log,null);
	}

public WhatsPort(String number,String nickName,boolean debug,boolean log,String dataFolder){
	this.writer=new BinTreeNodeWriter();
	this.reader=new BinTreeNodeReader();
	this.debug=debug;
	this.phoneNumber=number;
	this.name=nickName;
	this.nodeId=new Attributes<String>();
	
	if (dataFolder != null && new File(dataFolder).exists()) {
		this.dataFolder=dataFolder;
		if(!this.dataFolder.trim().endsWith(File.separator))
			this.dataFolder+=File.separator;
	} else {
        this.dataFolder = new File("").getAbsolutePath()+File.separator+Constants.DATA_FOLDER+File.separator;
    }
	
	challengeFilename=String.format("%snextChallenge.%s.dat", dataFolder==null?"":dataFolder+File.separator,this.phoneNumber);
	this.loginStatus=Constants.DISCONNECTED_STATUS;
	this.eventManager=new WhatsApiEventsManager();
	mediaQueue=new Attributes<String>();
	
}


public String getMyNumber(){
	return phoneNumber;
}

public boolean isConnected(){
	return socket!=null;
}

public boolean isLoggedIn(){
	return isConnected() && loginStatus!=null && loginStatus.equals(Constants.CONNECTED_STATUS);
}

public String getLoginStatus()
{
    return loginStatus;
}

public void loginWithPassword(String password) throws LoginException,IOException
{
    this.password = password;
    File f=new File(this.challengeFilename);
    if (f.canRead()) {
        String challengeData = Util.file_get_contents(new File(this.challengeFilename).toURI());
        if (challengeData!=null) 
        	this.challengeData=challengeData;
    }
    challengeData=Util.hex2bin("9b524c67e67634e1fa72b20f714b01001c2690bc");
    new Login(this).doLogin();
}

public void sendData(String data) throws IOException
{
  	if (isConnected()) {
        IOUtil.socketWrite(socket, data, data.length());
    }
}

public boolean debugPrint(Object message)
{
if(debug && debugOut!=null)
	{
	if(message!=null)
		debugOut.print("DEBUG :"+message);
	else
		debugOut.print("<null>");
	return true;
	}
return false;
}

public void sendNode(ProtocolNode node) throws IOException
{
sendNode(node,true);
}

public void sendNode(ProtocolNode node,boolean encrypt) throws IOException
{
timeout=Util.time();	
debugPrint(node.nodeString("tx  ")+"\n");
sendData(writer.write(node,encrypt));
}

public boolean pollMessage() throws IOException
{
    if (!isConnected()) {
        throw new RuntimeException("Connection Closed!");
    }
    String stanza=null;
    		socket.setSoTimeout(Constants.TIMEOUT_SEC*1000+Constants.TIMEOUT_USEC);
     		stanza=readStanza();
    	if (stanza!=null) {
            processInboundData(stanza);
            return true;
        }
    if ((Util.time() - timeout) > 60) {
        sendPing();
    }

    return false;
}

public String readStanza() throws IOException
{
	 StringBuilder buff=new StringBuilder();
	     
	if (isConnected()) {
    	String header = IOUtil.socketRead(socket, 3); //read stanza header
        if (header==null) 
            {eventManager.fire("onClose",phoneNumber,"Socket EOF");return null;}
        else if (header.length()==0)
        	return null;
        else if (header.length() != 3) {
            throw new IOException("Failed to read stanza header");
        }
        int treeLength = (Util.ord(header.charAt(0)) & 0x0F) << 16;
        treeLength |= Util.ord(header.charAt(1)) << 8;
        treeLength |= Util.ord(header.charAt(2)) << 0;
        buff.append(IOUtil.socketRead(socket, treeLength));
        int len = buff.length();
        while (buff.length() < treeLength) {
            int toRead = treeLength - buff.length();
            buff.append(IOUtil.socketRead(socket,toRead));
            if (len == buff.length()) {
                break;
            }
            len = buff.length();
        }
        if (buff.length() != treeLength) {
            throw new IOException("Tree length did not match received length (buff = "+buff.length()+" & treeLength = "+treeLength+")");
        }
        buff.insert(0, header);
    }
    return buff.toString();
}

private void processInboundData(String data) throws IOException
{
    ProtocolNode node = reader.nextTree(data);
    if (node != null) {
        processInboundDataNode(node);
    }
}


private void processInboundDataNode(ProtocolNode node) throws IOException
{
    timeout = Util.time();
    debugPrint(node.nodeString("rx  ")+"\n");
    serverReceivedId =(String)node.getAttribute("id");
    Handler handler=null;
    if (node.getTag().equals("challenge")) {
        processChallenge(node);
    } else if (node.getTag().equals("failure")) {
        loginStatus = Constants.DISCONNECTED_STATUS;
        eventManager.fire("onLoginFailed",phoneNumber,node.getChild(0).getTag());
        if (node.getChild(0).getTag().equals("not-authorized")) {
            logFile("error", "Blocked number or wrong password. Use blockChecker.php");
        }
    } else if (node.getTag().equals("success")) {
        if (node.getAttribute("status").equals("active")) {
            loginStatus = Constants.CONNECTED_STATUS;
            String challengeData = node.getData();
            IOUtil.file_put_contents(challengeFilename, challengeData);
            writer.setKey(outputKey);
            eventManager.fire("onLoginSuccess",phoneNumber,node.getAttribute("kind"),node.getAttribute("status"),node.getAttribute("creation"),node.getAttribute("expiration"));
        } else if (node.getAttribute("status").equals("expired")) {
            eventManager.fire("onAccountExpired",phoneNumber,node.getAttribute("kind"),node.getAttribute("status"),node.getAttribute("creation"),node.getAttribute("expiration"));
        }
    } else if (node.getTag().equals("ack")) {
        if (node.getAttribute("class").equals("message")) {
            eventManager.fire("onMessageReceivedServer",phoneNumber,node.getAttribute("from"),node.getAttribute("id"),node.getAttribute("class"),node.getAttribute("t"));
        }
    } else if (node.getTag().equals("receipt")) {
        if (node.hasChild("list" )) {
            for (ProtocolNode child:node.getChild("list").getChildren()) {
                eventManager.fire("onMessageReceivedClient",phoneNumber,node.getAttribute("from"),child.getAttribute("id"),node.getAttribute("type"),node.getAttribute("t"),node.getAttribute("participant"));
            }
        }
        if (node.hasChild("retry")) {
            sendGetCipherKeysFromUser(Util.extractNumber((String)node.getAttribute("from")), true);
            messageStore.setPending((String)node.getAttribute("id"),(String)node.getAttribute("from"));
        }
        if (node.hasChild("error") && node.getChild("error").getAttribute("type").equals("enc-v1")) {
            v1Only.put(Util.extractNumber((String)node.getAttribute("from")),true);
            messageStore.setPending((String)node.getAttribute("id"),(String)node.getAttribute("from"));
            //TODO handle this in event manager and manualy
           // sendPendingMessages((String)node.getAttribute("from"));
        }
        eventManager.fire("onMessageReceivedClient",phoneNumber,node.getAttribute("from"),node.getAttribute("id"),node.getAttribute("type"),node.getAttribute("t"),node.getAttribute("participant"));
        sendAck(node, "receipt");
    }
    if (node.getTag().equals("message")) {
        handler = new MessageHandler(this, node);
    }
    if (node.getTag().equals("presence") && node.getAttribute("status").equals("dirty") && node.getChildren()!=null) {
        ProtocolNode[] children=node.getChildren();
    	String[] categories = new String[children.length];
            for (int i=0;i<children.length;i++) {
                ProtocolNode child=children[i];
            	if (child.getTag().equals("category")) {
                    categories[i] = (String)child.getAttribute("name");
                }
            }
        sendClearDirty(categories);
    }
    if (node.getTag().equals("presence")
        && !((String)node.getAttribute("from")).substring(0, phoneNumber.length()).equals(phoneNumber)
        && ((String)node.getAttribute("from")).indexOf('-')==-1) {
        //$presence = [];
        if (node.getAttribute("type") == null) {
            eventManager.fire("onPresenceAvailable",phoneNumber,node.getAttribute("from"));
        } else {
            eventManager.fire("onPresenceUnavailable",phoneNumber,node.getAttribute("from"),node.getAttribute("last"));
        }
    }
    if (node.getTag().equals("presence")
        && !((String)node.getAttribute("from")).substring(0, phoneNumber.length()).equals(phoneNumber)
        && ((String)node.getAttribute("from")).indexOf('-')!=-1 
        && node.getAttribute("type") != null) {
        String groupId = parseJID((String)node.getAttribute("from"));
        if (node.getAttribute("add") != null) {
            eventManager.fire("onGroupsParticipantsAdd",phoneNumber,groupId,parseJID((String)node.getAttribute("add")));
        } else if (node.getAttribute("remove") != null) {
            eventManager.fire("onGroupsParticipantsRemove",phoneNumber,groupId,parseJID((String)node.getAttribute("remove")));
        }
    }
    if (node.getTag().equals("chatstate")
    		&& !((String)node.getAttribute("from")).substring(0, phoneNumber.length()).equals(phoneNumber)) { // remove if isn't group
        if (((String)node.getAttribute("from")).indexOf('-') ==-1) {
            if (node.getChild(0).getTag().equals("composing")) {
                eventManager.fire("onMessageComposing",phoneNumber,node.getAttribute("from"),node.getAttribute("id"),"composing",node.getAttribute("t"));
            } else {
                eventManager.fire("onMessagePaused",phoneNumber,node.getAttribute("from"),node.getAttribute("id"),"paused",node.getAttribute("t"));
            }
        } else {
            if (node.getChild(0).getTag().equals("composing")) {
                eventManager.fire("onGroupMessageComposing",phoneNumber,node.getAttribute("from"),node.getAttribute("participant"),node.getAttribute("id"),"composing",node.getAttribute("t"));
            } else {
                eventManager.fire("onGroupMessagePaused",phoneNumber,node.getAttribute("from"),node.getAttribute("participant"),node.getAttribute("id"),"paused",node.getAttribute("t"));
            }
        }
    }
    if (node.getTag().equals("receipt")) {
        eventManager.fire("onGetReceipt",node.getAttribute("from"),node.getAttribute("id"),node.getAttribute("offline"),node.getAttribute("retry"));
    }
    if (node.getTag().equals("iq")) {
        handler = new IqHandler(this, node);
    }
    if (node.getTag().equals("notification")) {
        handler = new NotificationHandler(this, node);
    }
    if (node.getTag().equals("call")) {
        if (node.getChild(0).getTag().equals("offer")) {
            String callId =(String)node.getChild(0).getAttribute("call-id");
            sendReceipt(node, null, null, callId);
            eventManager.fire("onCallReceived",phoneNumber,node.getAttribute("from"),node.getAttribute("id"),node.getAttribute("notify"),node.getAttribute("t"),node.getChild(0).getAttribute("call-id"));
        } else {
            sendAck(node, "call");
        }
    }
    if (node.getTag().equals("ib")) {
        for (ProtocolNode child:node.getChildren()) {
               if(child.getTag().equals("dirty")){sendClearDirty(new String[]{(String)child.getAttribute("type")});}
               else if(child.getTag().equals("account")){eventManager.fire("onPaymentRecieved",phoneNumber,child.getAttribute("kind"),child.getAttribute("status"),child.getAttribute("creation"),child.getAttribute("expiration"));}
               else if(child.getTag().equals("offline"));
               else throw new RuntimeException("ib handler for "+child.getTag()+" not implemented");
        }
    }
    // Disconnect socket on stream error.
    if (node.getTag().equals("stream:error")) {
        eventManager.fire("onStreamError",node.getChild(0).getTag());
        Map<String,String> context=new HashMap<String,String>();
        context.put("error", node.getChild(0).getTag());
        logFile("error", "Stream error {error}",context);
        disconnect();
    }
    if (handler!=null) {
        handler.process();
        handler=null;
    }
}

/*public void sendPendingMessages(String jid){
	if (messageStore != null && isLoggedIn()) {
		Map<String,Object>[] messages = messageStore.getPending(jid);
        for (Map<String,Object> message:messages) {
            sendMessage((String)message.get("to"),(String)message.get("message"));
        }
    }
}*/


private void processChallenge(ProtocolNode node){
	challengeData=node.getData();
}

public void logFile(String tag,String message){
logFile(tag,message,new HashMap<String,String>());
}
private void logFile(String tag,String message,Map<String,String> context){
	if(log && logger!=null)
		logger.log(tag, message,context);
}

private void sendGetCipherKeysFromUser(String number,boolean replaceKey) throws IOException{
	sendGetCipherKeysFromUser(new String[]{number},false);
}
private void sendGetCipherKeysFromUser(String[] numbers,boolean replaceKey) throws IOException{
	
	this.replaceKey = replaceKey;
    String msgId = createIqId();nodeId.put("cipherKeys",msgId) ;
    ProtocolNode[] userNode = new ProtocolNode[numbers.length];
    for (int i=0;i<userNode.length;i++){
    Attributes<String> attributes=new Attributes<String>();
    attributes.put("jid", getJID(numbers[i]));
    userNode[i] = new ProtocolNode("user",attributes, null, null);
    }
    ProtocolNode keyNode = new ProtocolNode("key", null, userNode, null);
    Attributes<String> nodeAttributes=new Attributes<String>();
    nodeAttributes.put("id", msgId);
    nodeAttributes.put("xmlns", "encrypt");
    nodeAttributes.put("type", "get");
    nodeAttributes.put("to", Constants.WHATSAPP_SERVER);
    
    sendNode(new ProtocolNode("iq",nodeAttributes, new ProtocolNode[]{keyNode}, null));
    //TODO implement waitForServer
    //waitForServer(msgId);
}

public void sendAck(ProtocolNode node, String class_) throws IOException{
	sendAck(node,class_,false);
}

public void sendAck(ProtocolNode node, String class_, boolean isGroup) throws IOException{
	String from = (String)node.getAttribute("from");
    String to = (String)node.getAttribute("to");
    String id = (String)node.getAttribute("id");
    String participant = null;
    String type = null;
    if (!isGroup) {
        type = (String)node.getAttribute("type");
        participant = (String)node.getAttribute("participant");
    }
    Attributes<String> attributes=new Attributes<String>();
    if (to!=null) {
    	attributes.put("from", to);
    }
    if (participant!=null) {
        attributes.put("participant", participant);
    }
    if (isGroup) {
        attributes.put("count", retryCounters.get(id).toString());
    }
	attributes.put("to", from);
	attributes.put("class", class_);
	attributes.put("id", id);
    
    if (type != null) {
        attributes.put("type", type);
    	
    }
    sendNode(new ProtocolNode("ack", attributes, null, null));	
}

public void sendClearDirty(String[] categories) throws IOException{
	String msgId = createIqId();
    ProtocolNode[] catnodes = new ProtocolNode[categories.length];
    for (int i=0;i<catnodes.length;i++) {
    	Attributes<String> attributes=new Attributes<String>();
    	attributes.put("type",categories[i]);
        catnodes[i] = new ProtocolNode("clean", attributes, null, null);
    }
    Attributes<String> attributes=new Attributes<String>();
    attributes.put("id", msgId);
    attributes.put("type", "set");
    attributes.put("to", Constants.WHATSAPP_SERVER);
    attributes.put("xmlns", "urn:xmpp:whatsapp:dirty");
    
    sendNode(new ProtocolNode("iq",attributes,catnodes,null));
    }

private static String parseJID(String jid)
{
return jid.split("@")[0];
}

public void sendReceipt(ProtocolNode node, String type ,String participant, String callId ) throws IOException{
	Attributes<String> messageHash =new Attributes<String>();
    if (type.equals("read")) {
        messageHash.put("type", type);
    }
    if (participant != null) {
        messageHash.put("participant", participant);
    }
    messageHash.put("to",(String)node.getAttribute("from"));
    messageHash.put("id", (String)node.getAttribute("id"));
    messageHash.put("participant", (String)node.getAttribute("t"));
    ProtocolNode messageNode=null;
    if (callId != null) {
        Attributes<String> attributes=new Attributes<String>();
        attributes.put("call-id", callId);
    	ProtocolNode offerNode = new ProtocolNode("offer", attributes, null, null);
        messageNode = new ProtocolNode("receipt", messageHash, new ProtocolNode[]{offerNode}, null);
    } else {
        messageNode = new ProtocolNode("receipt", messageHash, null, null);
    }
    sendNode(messageNode);
    eventManager.fire("onSendMessageReceived",phoneNumber,node.getAttribute("id"),node.getAttribute("from"),type);
}

public boolean connect()
{
    if (isConnected()) {
        return true;
    }
    
    Socket socket=new Socket();
    try{
    	socket.connect(new InetSocketAddress("e"+Integer.toString(new Random().nextInt(15))+".whatsapp.net",Constants.PORT));
    	socket.setSoTimeout(Constants.TIMEOUT_SEC*1000+Constants.TIMEOUT_USEC);
    }catch(IOException ioEx){logFile("error", "Failed to connect WA server "+ioEx.getMessage());this.eventManager.fire("onConnectError",this.phoneNumber,ioEx.getMessage());this.socket=null;return false;}
    
    this.socket=socket;
    this.logFile("info", "Connected to WA server");
    this.eventManager.fire("onConnect",this.phoneNumber,socket);
    return true;
    }

public void disconnect(){

    if (socket!=null) {
       try{socket.shutdownInput();
       socket.shutdownOutput();
       socket.close();}catch(IOException ioEx){}
    }
    socket = null;
    loginStatus = Constants.DISCONNECTED_STATUS;
    logFile("info", "Disconnected from WA server");
    eventManager.fire("onDisconnect",phoneNumber,socket);
}

private String createMsgId()
{
    String msg = Util.hex2bin(messageId);
    char[] chars = msg.toCharArray();
    int[] chars_val = new int[chars.length];
    for(int i=0;i<chars_val.length;i++)
    	chars_val[i]=Util.ord(chars[i]);
    int pos = chars_val.length - 1;
    while (true) {
        if (chars_val[pos] < 255) {
            chars_val[pos]++;
            break;
        } else {
            chars_val[pos] = 0;
            pos--;
        }
    }
    for(int i=0;i<chars.length;i++)
    	chars[i]=(char)chars_val[i];
    msg = Util.bin2hex(new String(chars));
    messageId = msg;
    return messageId;
}

private String sendMessageNode(String to, ProtocolNode node,String id) throws IOException{
return sendMessageNode(to,node,id,null);
}

private String sendMessageNode(String to, ProtocolNode node, String id, ProtocolNode plaintextNode ) throws IOException 
{
    String msgId = (id == null) ? createMsgId() : id;
    to = getJID(to);
    String type=null;
    if (node.getTag().equals("body") || node.getTag().equals("enc")) {
        type = "text";
    } else {
        type = "media";
    }

    ProtocolNode messageNode = new ProtocolNode("message",new Attributes<String>(new String[]{"to","type","id","t","notify"},new Object[]{to,type,msgId,Util.time(),this.name}),new ProtocolNode[]{node},"");
    sendNode(messageNode);

    if (node.getTag().equals("enc")) {
        node = plaintextNode;
    }

    logFile("info", String.format("%s message with id %s sent to %s ", type,id,Util.extractNumber(to)));
    eventManager.fire("onSendMessage",phoneNumber,to,msgId,node);
       // $this->waitForServer($msgId);
    return msgId;
}


private String sendBroadcast(String[] targets, ProtocolNode node, String type) throws IOException
{
    
    List<ProtocolNode> toNodes = new ArrayList<ProtocolNode>();
    for (String target:targets) 
         toNodes.add(new ProtocolNode("to", new Attributes<String>(new String[]{"jid"},new Object[]{getJID(target)}), null, null));
    ProtocolNode broadcastNode = new ProtocolNode("broadcast", null, toNodes.toArray(new ProtocolNode[0]), null);
    String msgId = createMsgId();
    ProtocolNode messageNode = new ProtocolNode("message",new Attributes<String>(new String[]{"to","type","id"},new Object[]{Long.toString(Util.time())+"@broadcast",type,msgId}),new ProtocolNode[]{node,broadcastNode},null);
    sendNode(messageNode);
    //TODO implement waitForServer
    //waitForServer(msgId);
    
    //listen for response
    eventManager.fire("onSendMessage",phoneNumber,targets,msgId,node);
    return msgId;
}



public String sendTextMessage(String to,String plaintext) throws IOException{
	return sendTextMessage(to,plaintext,false);
}

public String sendTextMessage(String to,String plaintext,boolean force_plain) throws IOException{
	ProtocolNode msgNode=null;
	/*if (extension_loaded('curve25519') && extension_loaded('protobuf') && !$force_plain) {
        $to_num = ExtractNumber($to);
        if (!(strpos($to, '-') !== false)) {
            if (!$this->axolotlStore->containsSession($to_num, 1)) {
                $this->sendGetCipherKeysFromUser($to_num);
            }

            $sessionCipher = $this->getSessionCipher($to_num);

            if (in_array($to_num, $this->v2Jids) && !isset($this->v1Only[$to_num])) {
                $version = '2';
                $alteredText = padMessage($plaintext);
            } else {
                $version = '1';
                $alteredText = $plaintext;
            }
            $cipherText = $sessionCipher->encrypt($alteredText);

            if ($cipherText instanceof WhisperMessage) {
                $type = 'msg';
            } else {
                $type = 'pkmsg';
            }
            $message = $cipherText->serialize();
            $msgNode = new ProtocolNode('enc',
          [
            'v'     => $version,
            'type'  => $type, 
          ], null, $message);
        } else {
      $msgNode = new ProtocolNode('body', null, null, $plaintext);
        }
    } else {*/
        msgNode = new ProtocolNode("body", null, null, plaintext);
    //}
    ProtocolNode plaintextNode = new ProtocolNode("body", null, null, plaintext);
    String id = sendMessageNode(to, msgNode, null, plaintextNode);

    /*if ($this->messageStore !== null) {
        $this->messageStore->saveMessage($this->phoneNumber, $to, $plaintext, $id, time());
    }*/

    return id;
}

public static String getJID(String number)
{
	if (!number.contains("@")) {
        //check if group message
        if (number.contains("-")) {
            //to group
            number += '@'+Constants.WHATSAPP_GROUP_SERVER;
        } else {
            //to normal user
            number += '@'+Constants.WHATSAPP_SERVER;
        }
    }
    return number;
}

public String createIqId(){
	iqCounter++;
    String id=Util.dechex(iqCounter);
    if (id.length() % 2 == 1) {
        id=String.format("%"+id.length()+1+"s", id).replace(" ", "0");
    }
    return id;
}
public void waitForServer(String id) throws ConnectionException,IOException{
	waitForServer(id,5);
}

public void waitForServer(String id,int timeout) throws ConnectionException,IOException{
	serverReceivedId = null;
    do {
        pollMessage();
    //} while (serverReceivedId != id && Util.time() - time < timeout);
    } while (!id.equals(serverReceivedId));
    
}

public void sendPing() throws IOException{
	String msgId = createIqId();
    ProtocolNode pingNode = new ProtocolNode("ping", null, null, null);
    sendNode(new ProtocolNode("iq",new Attributes<String>(new String[]{"id","xmlns","type","to"},new Object[]{msgId,"w:p","get",Constants.WHATSAPP_SERVER}),new ProtocolNode[]{pingNode},null));

}
public void sendAvailableForChat() throws IOException{
	sendAvailableForChat(null);
}

public void sendAvailableForChat(String nickName) throws IOException{
	    Attributes<String> presence = new Attributes<String>();
        if (nickName!=null) {
           this.name = nickName;
        }
        presence.put("name", this.name);
        presence.put("type", "available");
        
        sendNode(new ProtocolNode("presence", presence, null, ""));
 }

public void sendGetPrivacyBlockedList() throws IOException{
	
	  String msgId =this.createIqId();nodeId.put("privacy",msgId ); 
	  Attributes<String> attributesChild=new Attributes<String>();
	  attributesChild.put("name", "default");
	  ProtocolNode child = new ProtocolNode("list",attributesChild,null,null);
	  ProtocolNode child2 = new ProtocolNode("query",new Attributes<String>(), new ProtocolNode[]{child}, null);
	  Attributes<String> attributesNode=new Attributes<String>();
	  attributesNode.put("id", msgId);
	  attributesNode.put("xmlns", "jabber:iq:privacy");
	  attributesNode.put("type", "get");
	  sendNode(new ProtocolNode("iq",attributesNode,new ProtocolNode[]{child2},null));
}

public void sendGetClientConfig() throws IOException{
	
    String msgId = this.createIqId();
    ProtocolNode child = new ProtocolNode("config", null, null, null);
    Attributes<String> attributesNode=new Attributes<String>();
	  attributesNode.put("id", msgId);
	  attributesNode.put("xmlns", "urn:xmpp:whatsapp:push");
	  attributesNode.put("type", "get");
	  attributesNode.put("to", Constants.WHATSAPP_SERVER);
	  sendNode(new ProtocolNode("iq",attributesNode,new ProtocolNode[]{child},null));
}

/*private String sendRequestFileUpload( String type, byte[] data, String[] to){
	return sendRequestFileUpload(type,data,to,"");
}*/


private String sendRequestFileUpload(String type, byte[] data, String[] to, String caption) throws IOException
{
	if(data.length>MAX_MEDIA_SIZE)
	    	throw new RuntimeException("Maximum media size can be "+WhatsPort.MAX_MEDIA_SIZE+" but provided "+data.length);
		
	String b64hash=Util.base64_encode(Util.hash_file("sha-256",data,true));
	String id = createIqId();
    if(to.length==1)
    	to[0]=getJID(to[0]);
    ProtocolNode mediaNode = new ProtocolNode("media",new Attributes<String>(new String[]{"hash","type","size"},new Object[]{b64hash,type,data.length}),null,null);
    ProtocolNode node = new ProtocolNode("iq",new Attributes<String>(new String[]{"id","to","type","xmlns"},new Object[]{id,Constants.WHATSAPP_SERVER,"set","w:m"}),new ProtocolNode[]{mediaNode},null);
      //add to queue
    String messageId = createMsgId();
    mediaQueue.put(id, new Attributes<String>(new String[]{"messageNode","data","to","type","message_id","caption"},new Object[]{node,Util.byteToString(data),to,type,messageId,caption}));
    sendNode(node);
    //TODO implement waitForServer
    //waitForServer(id);

    // Return message ID. Make pull request for this.
    return messageId;
}


public String sendImage(String[] to, byte[] data) throws IOException{
	return sendImage(to,data,"");
}

public String sendImage(String[] to, byte[] data,String caption) throws IOException
{
   try{
    if(ImageIO.read(new ByteArrayInputStream(data))==null)
    		throw new RuntimeException("provided data is not convertable to image");
	}catch(IOException ioEx){throw new RuntimeException("error converting data to image "+ioEx.getMessage(),ioEx);}
	    return sendRequestFileUpload("image",data, to, caption);
}

public boolean processUploadResponse(ProtocolNode node) throws IOException{
        String id =(String)node.getAttribute("id");
        @SuppressWarnings("unchecked")
		Attributes<String> messageNode = (Attributes<String>)mediaQueue.get(id);
        
        if (messageNode == null) {
            eventManager.fire("onMediaUploadFailed",phoneNumber,id,node,messageNode,"Message node not found in queue");
            return false;
        }
        String url=null;
        int filesize=0;
        String filehash=null;
        String filetype=null;
       String filename=null;
       
        ProtocolNode duplicate = node.getChild("duplicate");
        if (duplicate != null) {
            //file already on whatsapp servers
             url=(String)duplicate.getAttribute("url");
             filesize= Integer.parseInt((String)duplicate.getAttribute("size"));
             filehash= (String)duplicate.getAttribute("filehash");
            filetype= (String)duplicate.getAttribute("type");
            try {filename=new URL(url).getFile();} catch (MalformedURLException e) {eventManager.fire("onMediaUploadFailed",phoneNumber,id,node,messageNode,"image URL is malformed");return false;}
        } /*else {
            //upload new file
            JsonObject json = WhatsMediaUploader.pushFile(node, messageNode, mediaFileInfo, phoneNumber);
            if (json==null) {
                eventManager.fire("onMediaUploadFailed",phoneNumber,id,node,messageNode,"Failed to push file to server");
                return false;
            }
            url = json.getString("url");
            filesize = json.getInt("size");
            filehash = json.getString("filehash");
            filetype = json.getString("type");
            filename = json.getString("name");
        }*/

        Attributes<String> mediaAttribs = new Attributes<String>(new String[]{"type","url","encoding","file","size"},new Object[]{filetype,url,"raw",filename,filesize});
        String caption=(String)messageNode.get("caption");
        if (caption!=null && caption.length()>0) 
            mediaAttribs.put("caption", caption);
        if(messageNode.get("type").equals("audio"))
        	mediaAttribs.put("origin","live");
        
        
        String mediaData=(String)messageNode.get("data");
        String[] to =(String[]) messageNode.get("to");
        
        String icon=null;
        if(filetype.equals("image"))
        	icon=Util.createIcon(mediaData);
        else if(filetype.equals("video"))
        	icon=Util.createVideoIcon(mediaData);
        else
        	icon="";
        //Retrieve Message ID
        String message_id =(String)messageNode.get("message_id");

        ProtocolNode mediaNode = new ProtocolNode("media", mediaAttribs, null, icon);
        if((to.length>1))
        	sendBroadcast(to, mediaNode, "media");
        else 
            sendMessageNode(to[0], mediaNode, message_id);
        
        eventManager.fire("onMediaMessageSent",phoneNumber,to,message_id,filetype,url,filename,filesize,filehash,caption,icon);
        return true;
    }



public void setOutputKey(KeyStream outputKey)
{
    this.outputKey = outputKey;
}

public String getChallengeData() {
	
	return this.challengeData;
}

public void setChallengeData(String challengeData) {
	this.challengeData=challengeData;
}

public void setMessageId(String id)
{
    this.messageId = id;
}
}
