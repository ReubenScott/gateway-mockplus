package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class CreateDescripencyResponseHandler extends ResponseHandler {

	public CreateDescripencyResponseHandler(SessionManager sm, Date date,
			String xml) {
		super(sm, date, xml);
	}

	@Override
	public void run() {
		System.out.println("create discrepency OK.");
		sessionManager.statDevice();
	}
}
