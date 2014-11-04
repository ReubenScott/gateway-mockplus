package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.MockLauncher;
import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLParamComponent;

public class AcknowledgeResponseHandler extends ResponseHandler {
	private String oid;
	private XMLParamComponent component;

	public AcknowledgeResponseHandler(SessionManager sm, Date date, String oid,
			XMLParamComponent component, String xml) {
		super(sm, date, xml);
		this.oid = oid;
		this.component = component;
	}

	@Override
	public void run() {
		if (response.getType().equals("ok")) {
			System.out.println("Acknowledge OK");
			System.out.println("Installing software....");
			if (MockLauncher.prompt) {
				sessionManager.acknowledgeInstalled(oid, component,
						NotificationManager.getInstance().getUpgradeStatus());
			} else {
				sessionManager.acknowledgeInstalled(oid, component, true);
			}
		} else {
			System.err.println("Acknowledge Failed");
			System.err.println(xml);
			NotificationManager.getInstance().runCmd(sessionManager);
		}		
	}
}
