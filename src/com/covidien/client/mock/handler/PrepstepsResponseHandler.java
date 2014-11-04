package com.covidien.client.mock.handler;

import java.util.Date;

import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class PrepstepsResponseHandler extends ResponseHandler {

    public PrepstepsResponseHandler(SessionManager sm, Date date, String xml) {
        super(sm, date, xml);
    }

    @Override
    public void run() {
        handle();
    }
}
