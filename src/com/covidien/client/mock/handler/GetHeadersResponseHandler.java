package com.covidien.client.mock.handler;

import java.io.ByteArrayInputStream;
import java.util.Date;

import com.covidien.client.mock.MockLauncher;
import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLGetHeaderMessage;
import com.covidien.laptopagent.xml.XMLHeaderMessageProcesser;
import com.covidien.laptopagent.xml.XMLHeaderResponse;
import com.covidien.laptopagent.xml.XMLNotification;

public class GetHeadersResponseHandler extends ResponseHandler {

	public GetHeadersResponseHandler(SessionManager sm, Date date, String xml) {
		super(sm, date, xml);
	}

	@Override
	public void run() {
		XMLHeaderMessageProcesser processor = new XMLHeaderMessageProcesser();
		ByteArrayInputStream byteInput = new ByteArrayInputStream(
				xml.getBytes());
		XMLGetHeaderMessage message = processor.parseMessage(byteInput);
		XMLHeaderResponse response = message.getHeaderResponse();
		NotificationManager.getInstance().reset();
		try {
			if (!response.getType().equals("headers")) {
				System.err.println("getheaders failed");
				System.err.println(xml);
			} else {
				if (response.getXmlNotification().size() > 0) {
					if (MockLauncher.prompt) {
						NotificationManager.getInstance().initDownLatch(
								response.getXmlNotification().size());
					}

					System.out.println("there are "
							+ response.getXmlNotification().size()
							+ " notifications!");

					for (XMLNotification n : response.getXmlNotification()) {
						sessionManager.getNotification(n.getOid());
					}

					if (MockLauncher.prompt) {
						NotificationManager.getInstance().await();
					}
				} else {
					System.err.println("No notifications!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (MockLauncher.prompt) {
			NotificationManager.getInstance().runCmd(sessionManager);
		}
	}

}
