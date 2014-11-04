package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class GetSystemConfigResponseHandler extends ResponseHandler {
    public GetSystemConfigResponseHandler(SessionManager sm, Date date, String xml) {
        super(sm, date, xml);
    }

    @Override
    public void run() {
        try {
            if (!response.getType().equals("bad")) {
                System.out.println("OK");
            } else {
                System.out.println("Get system configuration failed!");
                System.out.println(xml);
            }
            NotificationManager.getInstance().runCmd(sessionManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
