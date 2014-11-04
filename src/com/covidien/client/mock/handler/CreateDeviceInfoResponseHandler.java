package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLMessageResponse;

public class CreateDeviceInfoResponseHandler extends ResponseHandler {

	public CreateDeviceInfoResponseHandler(SessionManager sm, Date date,
			String xml) {
		super(sm, date, xml);
	}

	@Override
	public void run() {
		XMLMessageResponse response = message.getHeaderResponse();
		try {
			if (!response.getType().equals("bad")) {
				System.out.println("create device info: OK");
				NotificationManager.getInstance().setDeviceInfoSended(true);
				sessionManager.getHeaders();
			} else {
				System.err.println("create device info: Failed");
				System.err.println(xml);
				NotificationManager.getInstance().setDeviceInfoSended(false);
				NotificationManager.getInstance().runCmd(sessionManager);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
