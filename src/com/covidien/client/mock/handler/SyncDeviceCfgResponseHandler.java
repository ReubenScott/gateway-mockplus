package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class SyncDeviceCfgResponseHandler extends ResponseHandler {
    public SyncDeviceCfgResponseHandler(SessionManager sm, Date date, String xml) {
        super(sm, date, xml);
    }

    @Override
    public void run() {
        try {
            if (!response.getType().equals("bad")) {
                System.out.println("OK");
            } else {
                System.out.println("Synchronize Device configuration failed!");
                System.out.println(xml);
            }
            NotificationManager.getInstance().runCmd(sessionManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
