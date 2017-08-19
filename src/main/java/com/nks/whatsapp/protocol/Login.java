package com.nks.whatsapp.protocol;

import java.io.IOException;

import javax.security.auth.login.LoginException;

public class Login {
	private KeyStream inputKey;
    private KeyStream outputKey;
    private WhatsPort parent;

  public Login(WhatsPort parent){
	  this.parent=parent;
  }
   
  
  public boolean doLogin() throws LoginException,IOException
  {
      if (this.parent.isLoggedIn()) {
          return true;
      }
      this.parent.writer.resetKey();
      this.parent.reader.resetKey();
      String resource = Constants.PLATFORM+'-'+Constants.WHATSAPP_VER;
      String data = this.parent.writer.startStream(Constants.WHATSAPP_SERVER, resource);
      ProtocolNode feat = createFeaturesNode();
      ProtocolNode auth = this.createAuthNode();
      this.parent.sendData(data);
      this.parent.sendNode(feat);
      this.parent.sendNode(auth);
      this.parent.pollMessage();
      this.parent.pollMessage();
      this.parent.pollMessage();
      if (this.parent.getChallengeData() != null) {
          ProtocolNode data1 = this.createAuthResponseNode();
          this.parent.sendNode(data1);
          this.parent.reader.setKey(this.inputKey);
          this.parent.writer.setKey(this.outputKey);
          while (!this.parent.pollMessage()) {
          };
      }
      if (this.parent.getLoginStatus().equals(Constants.DISCONNECTED_STATUS)) {
          throw new LoginException();
      }
      this.parent.logFile("info", "{number} successfully logged in , [number => "+this.parent.phoneNumber);
      this.parent.sendAvailableForChat();
      this.parent.sendGetPrivacyBlockedList();
      this.parent.sendGetClientConfig();
      this.parent.setMessageId(Util.bin2hex(Util.mcrypt_create_iv(64)).substring(0, 22)); // 11 char hex
      /*if (extension_loaded('curve25519') || extension_loaded('protobuf')) {
          if (file_exists($this->parent->dataFolder.'axolotl-'.$this->phoneNumber.'.db')) {
              $pre_keys = $this->parent->getAxolotlStore()->loadPreKeys();
              if (empty($pre_keys)) {
                  $this->parent->sendSetPreKeys();
                  $this->parent->logFile('info', 'Sending prekeys to WA server');
              }
          }
      }*/
      return true;
  }
  
  private static ProtocolNode createFeaturesNode()
  {
  	  return new ProtocolNode("stream:features", null, null, null);
  }
  
  private String createAuthBlob()
  {
	  if (parent.getChallengeData()!=null) {
          String key = Util.wa_pbkdf2("sha1", Util.base64_decode(parent.password), parent.getChallengeData(), 16, 20, true);
          this.inputKey = new KeyStream(new String(new char[]{key.charAt(2)}),new String(new char[]{key.charAt(3)}));
          this.outputKey = new KeyStream(new String(new char[]{key.charAt(0)}),new String(new char[]{key.charAt(1)}));
          this.parent.reader.setKey(this.inputKey);
          String array = "\0\0\0\0"+this.parent.phoneNumber+this.parent.getChallengeData()+Util.time();
          this.parent.setChallengeData(null);
          return this.outputKey.encodeMessage(array, 0, array.length(), 0);
      }
  return null;
  }
  
  private ProtocolNode createAuthNode(){
      
	  String data = this.createAuthBlob();
	  Attributes<String> attributes = new Attributes<String>();
      	attributes.put("user", this.parent.phoneNumber);
      	attributes.put("mechanism", "WAUTH-2");
      return new ProtocolNode("auth", attributes, null, data);
      
  }

  
  private ProtocolNode createAuthResponseNode()
  {
      return new ProtocolNode("response", null, null, this.authenticate());
  }


  private String authenticate()
  {
	  String[] keys = KeyStream.generateKeys(Util.base64_decode(this.parent.password), this.parent.getChallengeData());
	  this.inputKey = new KeyStream(keys[2], keys[3]);
      this.outputKey = new KeyStream(keys[0], keys[1]);
      String array = "\0\0\0\0"+this.parent.phoneNumber+this.parent.getChallengeData()+""+Util.time()+"000"+Util.hex2bin("00")+"000"+Util.hex2bin("00")+Constants.OS_VERSION+Util.hex2bin("00")+Constants.MANUFACTURER+Util.hex2bin("00")+Constants.DEVICE+Util.hex2bin("00")+Constants.BUILD_VERSION;
      String response = this.outputKey.encodeMessage(array, 0, 4, array.length() - 4);
      this.parent.setOutputKey(this.outputKey);
      
      return response;
  }
}
