package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLMessageResponse;

public class LoginResponseHandler extends ResponseHandler {

	public LoginResponseHandler(SessionManager sm, Date date, String xml) {
		super(sm, date, xml);
	}

	@Override
	public void run() {
		XMLMessageResponse response = message.getHeaderResponse();
		try {
			if (response.getType().equals("ok")) {
				System.out.println("[OK]");
				sessionManager.setLogin(true);				
			} else {
				System.err.println("[failed]");
				System.err.println(xml);				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationManager.getInstance().runCmd(sessionManager);
	}
}
