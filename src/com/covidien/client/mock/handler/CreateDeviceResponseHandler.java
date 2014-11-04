package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.MockLauncher;
import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class CreateDeviceResponseHandler extends ResponseHandler {

    public CreateDeviceResponseHandler(SessionManager sm, Date date, String xml) {
        super(sm, date, xml);
    }

    @Override
    public void run() {
        try {
            if (response.getType().equals("ok")) {
                System.out.println("create device ok");
                if (MockLauncher.currentDeviceType.equals("pb980")) {
                    // sessionManager.getSync();
                	sessionManager.createDeviceInfo();
                    NotificationManager.getInstance().runCmd(sessionManager);
                } else {
                    sessionManager.createDeviceInfo();
                }
            } else {
                System.err.println("create device failed");
                System.out.println(xml);
                NotificationManager.getInstance().runCmd(sessionManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
