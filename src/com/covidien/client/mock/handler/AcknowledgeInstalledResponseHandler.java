package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.MockLauncher;
import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLParamComponent;

public class AcknowledgeInstalledResponseHandler extends ResponseHandler {
	private XMLParamComponent component;

	public AcknowledgeInstalledResponseHandler(SessionManager sm, Date date,
			XMLParamComponent component,String xml) {
		super(sm, date,xml);
		this.component = component;
	}

	@Override
	public void run() {
		if (response.getType().equals("ok")) {
			System.out.println("Acknowledge installed OK:"
					+ component.getComponentName());
		} else {
			System.err.println("Acknowledge installed Failed");
			System.err.println(xml);
		}
		if(MockLauncher.prompt){
			NotificationManager.getInstance().runCmd(sessionManager);
		}else{
			sessionManager.createDescripency();		
		}
	}

}
