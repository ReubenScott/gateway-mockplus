package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;
import com.covidien.laptopagent.xml.XMLMessageResponse;
/**
 * Register Handler. 
 */
public class RegisterResponseHandler extends ResponseHandler {
    public RegisterResponseHandler(SessionManager sm, Date date, String xml) {
        super(sm, date, xml);
    }

    @Override
    public void run() {
        XMLMessageResponse response = message.getHeaderResponse();
        try {
            if (response.getType().equals("ok")) {
                System.out.println("The register page URL:" + response.getParams().getUrl());
            } else {
                System.err.println(xml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationManager.getInstance().runCmd(sessionManager);
    }

}
