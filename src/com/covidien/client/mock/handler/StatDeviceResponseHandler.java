package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.MockLauncher;
import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class StatDeviceResponseHandler extends ResponseHandler {
    public StatDeviceResponseHandler(SessionManager sm, Date date, String xml) {
        super(sm, date, xml);
    }

    @Override
    public void run() {
        try {
            if (!response.getType().equals("bad")) {
                System.out.println("the device exists!");
                System.out.println(xml);
                if (MockLauncher.currentDeviceType.equals("pb980") || 
                    MockLauncher.currentDeviceType.equals("scd") ||
                    MockLauncher.currentDeviceType.equals("forcetriad")) {
                    // sessionManager.getSync();
                    sessionManager.createDeviceInfo();
                    //NotificationManager.getInstance().runCmd(sessionManager);
                } else {
                    sessionManager.createDeviceInfo();
                }
            } else {
                System.out.println("the device does not exist!");
                sessionManager.createDevice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
