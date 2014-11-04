package com.covidien.client.mock.handler;

import java.awt.Desktop;
import java.net.URI;
import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLNotification;
import com.covidien.laptopagent.xml.XMLParamComponent;

public class OpenDocumentResponseHandler extends ResponseHandler {

	public OpenDocumentResponseHandler(SessionManager sm, Date date, String xml) {
		super(sm, date, xml);

	}

	@Override
	public void run() {

		if (response.getType().equals("postnotification")) {
			XMLNotification notification = response.getXmlNotification().get(0);
			if (notification != null) {
				XMLParamComponent comp = notification.getBody()
						.getXMLParamSoftwareList().get(0);
				if (comp != null && comp.getLocalPath() != null) {
					String path = comp.getLocalPath();
					String pe = path.toLowerCase();
					try {
						if (pe.endsWith("pdf")) {
							Runtime.getRuntime().exec(
									"\"C:\\Program Files\\Covidien\\Device Management Agent\\PDFReader.exe\" \""
											+ path + "\"");
						} else if (pe.endsWith("txt")) {
							Runtime.getRuntime().exec(
									"notepad \"" + path + "\"");
						} else if (pe.endsWith("html") || pe.endsWith("htm")) {
							if (Desktop.isDesktopSupported()) {
								Desktop.getDesktop().browse(new URI(path));
							} else {
								throw new Exception("can not open browser.");
							}
						} else {
							System.out.println("Cannot open the document: "
									+ path);
						}
					} catch (Exception e) {
						System.out.println("Cannot open the document: " + path);
						e.printStackTrace();
					}
				}
			}
		} else {
			System.err.println(xml);
		}
		NotificationManager.getInstance().runCmd(sessionManager);
	}
}
