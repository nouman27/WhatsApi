package com.nks.whatsapp.marketing.app;

import java.util.EventListener;


public interface PanelUnitListener extends EventListener{

public void detailPressed(PanelUnit source);	
public void imagePressed(PanelUnit source);
public void backPressed(PanelUnit source);
public void attachmentPressed(PanelUnit source);
public void menuPressed(PanelUnit source);
}
