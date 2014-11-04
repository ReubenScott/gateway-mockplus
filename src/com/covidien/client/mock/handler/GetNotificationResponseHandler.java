package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.MockLauncher;
import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLNotification;
import com.covidien.laptopagent.xml.XMLParamComponent;

public class GetNotificationResponseHandler extends ResponseHandler {

	public GetNotificationResponseHandler(SessionManager sm, Date date,
			String xml) {
		super(sm, date, xml);

	}

	@Override
	public void run() {
		try {
			// System.err.println(xml);
			if (response.getType().equals("notifications")) {
				System.out
						.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

				for (XMLNotification notification : response
						.getXmlNotification()) {
					System.out.println("Notification ID:"
							+ notification.getOid());
					System.out
							.println("----------------------------------Components---------------------------------------------------------------");
					for (XMLParamComponent component : notification.getBody()
							.getXMLParamSoftwareList()) {

						System.out.println("Component "
								+ component.getComponentType() + " - "
								+ component.getComponentName() + " - "
								+ component.getSoftwareRevision() + " - "
								+ component.getLocalPath());
						
						if (MockLauncher.prompt) {
							if (component.getComponentType().equalsIgnoreCase(
									"document")) {
								NotificationManager.getInstance().addDocument(
										notification.getOid(), component);

							}

							if (component.getComponentType().equalsIgnoreCase(
									"software")
									&& component.getLocalPath() != null) {
								NotificationManager.getInstance().addSoftware(
										notification.getOid(), component);
							}
						} else {
							if (component.getComponentType().equalsIgnoreCase(
									"software")
									&& component
											.getSoftwareRevision()
											.equalsIgnoreCase("VIKING_3.2.0.71")) {
								sessionManager.acknowledge(
										notification.getOid(), component);
							}
						}
					}
				}
				System.out
						.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
			} else {
				System.err.println("get Notification Failed");
				System.err.println(xml);
			}
		} catch (Exception e) {
			System.err.println(xml);
			e.printStackTrace();
		} finally {
			if (MockLauncher.prompt) {
				NotificationManager.getInstance().countDown();
			}
		}
		NotificationManager.getInstance().runCmd(sessionManager);
	}

}
