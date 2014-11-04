package com.covidien.client.mock;

import java.util.Date;

import org.apache.log4j.Logger;

import com.covidien.laptopagent.xml.XMLMessage;
import com.covidien.laptopagent.xml.XMLMessageResponse;

public abstract class ResponseHandler implements Runnable {
	protected Logger logger = Logger.getLogger(ResponseHandler.class);
	protected SessionManager sessionManager;
	protected XMLMessage message;
	protected Date date;
	protected XMLMessageResponse response;
	protected String xml;
	private String request;

	public ResponseHandler(SessionManager sm, Date date, String xml) {
		sessionManager = sm;
		this.date = date;
		request = xml;
	}

	public void setMessage(XMLMessage msg, String xml) {
		message = msg;
		this.xml = xml;
		logger.debug("\n\tRequest: " + request + "\n\tResponse: " + xml + "\n");
		if (msg != null) {
			response = msg.getHeaderResponse();
		}
	}

	protected boolean handle() {
		boolean rtn = false;
		try {
			if (response != null) {
				if (response.getType().equalsIgnoreCase("ok")) {
					System.out.println("Response is OK");
					rtn = true;
				} else {
					System.err.println(response.getParams().getReason());
					System.err.println(xml);
				}
			} else {
				System.err.println(xml);
			}
		} catch (Exception e) {
			System.err.println(xml);
			e.printStackTrace();
		}
		NotificationManager.getInstance().runCmd(sessionManager);
		return rtn;
	}
}
