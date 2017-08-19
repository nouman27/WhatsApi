package com.nks.whatsapp.marketing.app;

import java.util.EventListener;

public interface ListPanelListener extends EventListener{
public void panelUnitAdded(PanelUnit panelUnit);
public void panelDetailPressed(PanelUnit panelUnit);
public void panelImagePressed(PanelUnit panelUnit);

}
