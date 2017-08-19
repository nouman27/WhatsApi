package com.nks.whatsapp.protocol;

public class RC4 {

    private int[] s;
    private int i;
    private int j;
    
    public RC4()
    {
    	s = Util.range(0, 255);
    	i = 0;
        j = 0;
        
    }
    public RC4(String key, int drop)
    {
        s = Util.range(0, 255);
    
        for (i = 0, j = 0; i < 256; i++) {
            int k = Util.ord(key.charAt(i % key.length()));
            j = (j + k + s[i]) & 255;
            swap(i, j);
        }
        i = 0;
        j = 0;
        cipher(Util.intArrayToString(Util.range(0, drop)), 0, drop);
    }
    public String cipher(String data, int offset, int length)
    {
        StringBuffer out = new StringBuffer(data);
        for (int n = length; n > 0; n--) {
           i = (i + 1) & 0xff;
           j = (j + s[i]) & 0xff;
           
           swap(i, j);
            int d = Util.ord((char)data.charAt(offset));
            out.setCharAt(offset,(char)(d ^ s[(s[i] + s[j]) & 0xff]));
            offset++;
        }
        return out.toString();
    }
 
 private void swap(int i, int j)
    {
    int c = s[i];
        s[i] = s[j];
        s[j] = c;
    }
	
/*public static void main(String[] args)
{
RC4 rc4=new RC4();
System.out.println(rc4.cipher(Util.intArrayToString(Util.range(0, 255)), 0, 78));
}*/
}
