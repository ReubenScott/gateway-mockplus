package com.covidien.client.mock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MockLauncher {
    public static boolean prompt = true;
    public static String GUID;
    //public static final java.util.Queue<String> devices = new java.util.ArrayDeque<String>();
    public static String currentSN;
    public static final Map<String, String> deviceTypes = new HashMap<String, String>();
    public static final Map<String,String> guids = new HashMap<String,String>();
    public static String currentDeviceType = "";
    static {
        deviceTypes.put("scd", "61e08b77-df3c-4735-9f3b-0b42efb7bdcf");
        guids.put("61e08b77-df3c-4735-9f3b-0b42efb7bdcf", "SCD 700");
        
        deviceTypes.put("pb980", "7a85f0c9-531e-4754-ad68-04c77ed63657");
        guids.put("7a85f0c9-531e-4754-ad68-04c77ed63657", "PB980_Ventilator");
        
        deviceTypes.put("pb840", "a7a65225-ef2d-48e9-89c8-6975dd7dc054");
        guids.put("a7a65225-ef2d-48e9-89c8-6975dd7dc054", "PB840_Ventilator");
        
        deviceTypes.put("forcetriad", "DACD7FA1-9D67-4057-9952-C55F8EA6227B");
        guids.put("DACD7FA1-9D67-4057-9952-C55F8EA6227B","ForceTriad");
        
        deviceTypes.put("emerald", "3B682913-6D1E-4355-9E48-208EB7061A3D");
        guids.put("3B682913-6D1E-4355-9E48-208EB7061A3D", "Valleylab LS10");
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args)
        throws IOException {
        
        //see NotificationManager.java for command line parsing
        
        System.out.println(Locale.getDefault());
        SimpleDateFormat print = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
        System.out.println(print.format(new Date()));
        final ClientMock cm = new ClientMock();
        cm.start();
        Runtime.getRuntime().removeShutdownHook(new Thread() {
            public void run() {
                cm.stop();
            }
        });
    }
}
