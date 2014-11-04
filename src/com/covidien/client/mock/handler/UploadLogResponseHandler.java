package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class UploadLogResponseHandler extends ResponseHandler {

	public UploadLogResponseHandler(SessionManager sm, Date date, String xml) {
		super(sm, date, xml);

	}

	@Override
	public void run() {
		if (response.getType().equals("ok")) {
			System.out.println("Upload Log OK");
		} else {
			System.err.println("Upload Log Failed");
			System.err.println(xml);
		}
		NotificationManager.getInstance().runCmd(sessionManager);
	}
}
