package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLMessageResponse;

public class UpdateClientResponseHandler extends ResponseHandler {

	public UpdateClientResponseHandler(SessionManager sm, Date date, String xml) {
		super(sm, date, xml);
	}

	@Override
	public void run() {
	    XMLMessageResponse response = message.getHeaderResponse();
        try {
            if (!response.getType().equals("bad")) {
                System.out.println("get client upgrade info: OK");
                NotificationManager.getInstance().setDeviceInfoSended(true);
                sessionManager.getHeaders();
            } else {
                System.err.println("get client upgrade info: Failed");
                System.err.println(xml);
                NotificationManager.getInstance().setDeviceInfoSended(true);
                NotificationManager.getInstance().runCmd(sessionManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
