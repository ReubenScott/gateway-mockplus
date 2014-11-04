package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLMessageResponse;

public class CreateSessionResponseHandler extends ResponseHandler {

	public CreateSessionResponseHandler(SessionManager sm, Date date,String xml) {
		super(sm, date,xml);
	}

	@Override
	public void run() {
		XMLMessageResponse response = message.getHeaderResponse();

		try {
			if (response.getType().equals("ok")) {
				String sessionID = response.getParams().getSessionID();
				System.out.println("[OK]");
				sessionManager.setSessionId(sessionID);
				NotificationManager.getInstance().runCmd(sessionManager);				
			} else {
				System.err.println("[failed]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
