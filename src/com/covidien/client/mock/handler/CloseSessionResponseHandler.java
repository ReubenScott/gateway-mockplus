package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class CloseSessionResponseHandler extends ResponseHandler {

	public CloseSessionResponseHandler(SessionManager sm, Date date, String xml) {
		super(sm, date, xml);
	}

	@Override
	public void run() {
		System.out.println("session closed.");
		System.exit(0);
	}
}
