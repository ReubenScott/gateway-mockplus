package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class LogOffResponseHandler extends ResponseHandler {

	public LogOffResponseHandler(SessionManager sm, Date date, String xml) {
		super(sm, date, xml);
	}

	@Override
	public void run() {
		sessionManager.setLogin(false);
		NotificationManager.getInstance().runCmd(sessionManager);
	}

}
