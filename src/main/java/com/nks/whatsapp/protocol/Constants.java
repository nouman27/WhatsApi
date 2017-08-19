package com.nks.whatsapp.protocol;

public class Constants {

	/**
     * Constant declarations.
     */
    public static final String CONNECTED_STATUS = "connected";                                                    // Describes the connection status with the WhatsApp server.
    public static final String  DISCONNECTED_STATUS = "disconnected";                                              // Describes the connection status with the WhatsApp server.
    public static final String  MEDIA_FOLDER = "media";                                                            // The relative folder to store received media files
    public static final String  PICTURES_FOLDER = "pictures";                                                      // The relative folder to store picture files
    public static final String  DATA_FOLDER = "wadata";                                                             // The relative folder to store cache files.
    public static final int  PORT = 443;                                                                        // The port of the WhatsApp server.
    public static final int  TIMEOUT_SEC = 20;                                                                   // The timeout for the connection with the WhatsApp servers.
    public static final int  TIMEOUT_USEC = 0;
    public static final String  WHATSAPP_CHECK_HOST = "v.whatsapp.net/v2/exist";                                   // The check credentials host.
    public static final String  WHATSAPP_GROUP_SERVER = "g.us";                                                    // The Group server hostname
    public static final String  WHATSAPP_REGISTER_HOST = "v.whatsapp.net/v2/register";                             // The register code host.
    public static final String  WHATSAPP_REQUEST_HOST = "v.whatsapp.net/v2/code";                                  // The request code host.
    public static final String  WHATSAPP_SERVER = "s.whatsapp.net";                                                // The hostname used to login/send messages.
    public static final String  DEVICE = "armani";                                                                 // The device name.
    public static String  WHATSAPP_VER = "2.16.148";                                                         // The WhatsApp version.
    public static final String  OS_VERSION = "4.3";
    public static final String  MANUFACTURER = "Xiaomi";
    public static final String  BUILD_VERSION = "JLS36C";
    public static final String  PLATFORM = "Android";                                                              // The device name.
    public static String  WHATSAPP_USER_AGENT = "WhatsApp/2.16.148 Android/4.3 Device/Xiaomi-HM_1SW";        // User agent used in request/registration code.
    public static final String  WHATSAPP_VER_CHECKER = "https://coderus.openrepos.net/whitesoft/whatsapp_scratch"; // Check WhatsApp version
}
	

