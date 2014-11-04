package com.covidien.client.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.covidien.laptopagent.xml.XMLParamComponent;

public class NotificationManager {
    private static final NotificationManager INSTANCE = new NotificationManager();
    private CountDownLatch down = null;
    private Map<String, XMLParamComponent> softwares = new ConcurrentHashMap<String, XMLParamComponent>();
    private Map<String, XMLParamComponent> documents = new ConcurrentHashMap<String, XMLParamComponent>();
    private BufferedReader br;
    private boolean upgradeStatus = true;
    private boolean deviceInfoSended = false;
    private boolean disconnected = false;    
    private String types;

    public void reset() {
        softwares.clear();
        documents.clear();
    }

    public void setDeviceInfoSended(boolean deviceInfoSended) {
        this.deviceInfoSended = deviceInfoSended;
        reset();
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    private NotificationManager() {
        InputStreamReader converter = new InputStreamReader(System.in);
        br = new BufferedReader(converter);
        StringBuffer sb = new StringBuffer();
        for (String type : MockLauncher.deviceTypes.keySet()) {
            sb.append(type);
            sb.append(",");
        }
        types = sb.toString().substring(0, sb.length() - 1);
    }

    public static NotificationManager getInstance() {
        return INSTANCE;
    }

    public void initDownLatch(int count) {
        down = new CountDownLatch(count);
    }

    public void resetDownLatch() {
        down = null;
    }

    public void await() {
        if (down != null) {
            try {
                down.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void countDown() {
        if (down != null) {
            down.countDown();
        }
    }

    public void addSoftware(String od, XMLParamComponent component) {
        softwares.put(od, component);
    }

    public void addDocument(String od, XMLParamComponent component) {
        documents.put(od, component);
    }

    public void runCmd(SessionManager sm) {
        XMLParamComponent component = null;
        if (deviceInfoSended) {
            System.out.println("\n=======================================================================");
            System.out.println("Upgradable Softwares:");
            System.out.println("-----------------------------------------------------------------------");

            for (String oid : softwares.keySet()) {
                component = softwares.get(oid);
                System.out.println(oid + " --> " + component.getComponentName() + " ["
                        + component.getSoftwareRevision() + "] ");

            }
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Documents:");
            System.out.println("-----------------------------------------------------------------------");
            for (String oid : documents.keySet()) {
                component = documents.get(oid);
                System.out.println(oid + " --> " + component.getComponentName() + " ["
                        + component.getSoftwareRevision() + "] ");

            }
            System.out.println("=======================================================================\n");
        }
        String cmd = getCmd(sm.isLogin(), sm.getCurrentUser());
        if (cmd != null) {
            String cmdLine = cmd.trim();
            if (cmdLine.equalsIgnoreCase("quit")) {
                sm.close();
                return;
            } else if ("disconnect".equalsIgnoreCase(cmdLine)) {
                sm.disconnect();
                return;
            } else if ("status".equalsIgnoreCase(cmdLine)) {
                sm.getStatus(false);
                return;
            } else if ("logout".equalsIgnoreCase(cmdLine)) {
                sm.logoff();
                return;
            }else if("register".equalsIgnoreCase(cmdLine)){
                sm.getRegisterURL();
                return;
            }
            String cmds[] = cmdLine.split(" ");
            if (cmds.length < 2) {
                System.err.println("invalid command: " + cmd);
                runCmd(sm);
            } else if ("update".equalsIgnoreCase(cmds[0]) && cmds.length > 1) {
                if (MockLauncher.deviceTypes.containsKey(cmds[1])) {
                    NotificationManager.getInstance().setDeviceInfoSended(false);
                    MockLauncher.GUID = MockLauncher.deviceTypes.get(cmds[1]);
                    sm.updateClient(cmds[1]);
                } else {
                    System.err.println("invalid device type: " + cmds[1]);
                    runCmd(sm);
                }
            } else if ("login".equalsIgnoreCase(cmds[0]) && cmds.length >= 3) {
                String pp = "";
                if (cmds.length == 4) {
                    pp = cmds[3];                    
                }
                sm.login(cmds[1], cmds[2], pp);
            } else if ("forgot".equalsIgnoreCase(cmds[0])) {
                sm.forgetPassword(cmds[1]);
            } else if ("change".equalsIgnoreCase(cmds[0])) {
                sm.changePassword(cmds[1]);
            } else if ("prepsteps".equalsIgnoreCase(cmds[0]) && cmds.length > 1) {
                ArrayList<String> devices = new ArrayList<String>();
                for (int i = 1; i < cmds.length; i++) {
                    if(MockLauncher.deviceTypes.containsKey(cmds[i])){
                        devices.add(MockLauncher.deviceTypes.get(cmds[i]));
                    }
                }
                sm.prepsteps(devices);
            } else if ("getnc".equalsIgnoreCase(cmds[0]) && cmds.length == 2) {
                if (MockLauncher.deviceTypes.containsKey(cmds[1])) {
                    MockLauncher.GUID = MockLauncher.deviceTypes.get(cmds[1]);
                    // MockLauncher.devices.add(cmds[2]);
                    sm.getSysCfg();
                } else {
                    System.err.println("invalid device type: " + cmds[1]);
                    runCmd(sm);
                }
            } else if ("getnotification".equalsIgnoreCase(cmds[0]) && cmds.length == 2) {
                sm.getnotification(cmds[1]);
            } else if ("sync".equalsIgnoreCase(cmds[0]) && cmds.length == 3) {
                if (MockLauncher.deviceTypes.containsKey(cmds[1])) {
                    MockLauncher.GUID = MockLauncher.deviceTypes.get(cmds[1]);
                    MockLauncher.currentSN = cmds[2];
                    sm.getSync();
                } else {
                    System.err.println("invalid device type: " + cmds[1]);
                    runCmd(sm);
                }
            } else if ("getmc".equalsIgnoreCase(cmds[0]) && cmds.length == 3) {
                if (MockLauncher.deviceTypes.containsKey(cmds[1])) {
                    MockLauncher.GUID = MockLauncher.deviceTypes.get(cmds[1]);
                    MockLauncher.currentSN = cmds[2];
                    sm.getMatchedSysCfg();
                } else {
                    System.err.println("invalid device type: " + cmds[1]);
                    runCmd(sm);
                }
            } else if ("connect".equalsIgnoreCase(cmds[0]) && cmds.length == 3) {
                if (MockLauncher.deviceTypes.containsKey(cmds[1])) {
                    NotificationManager.getInstance().setDeviceInfoSended(false);
                    MockLauncher.GUID = MockLauncher.deviceTypes.get(cmds[1]);
                    MockLauncher.currentDeviceType = cmds[1];
                    MockLauncher.currentSN = cmds[2];
                    sm.statDevice();
                } else {
                    System.err.println("invalid device type: " + cmds[1]);
                    runCmd(sm);
                }
            } else if ("install".equalsIgnoreCase(cmds[0]) && softwares.containsKey(cmds[1])) {

                if (cmds.length > 2 && "f".equalsIgnoreCase(cmds[2])) {
                    upgradeStatus = false;
                } else {
                    upgradeStatus = true;
                }
                component = softwares.get(cmds[1]);
                sm.acknowledge(cmds[1], component);
            } else if ("open".equalsIgnoreCase(cmds[0]) && documents.containsKey(cmds[1])) {
                component = documents.get(cmds[1]);
                sm.openDocument(cmds[1], component);
            } else if ("upload".equalsIgnoreCase(cmds[0])) {
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yMdHmmss");

                File f = new File("clientMock-test-" + sdf.format(d) + ".log");
                if (!f.exists()) {
                    try {
                        if (f.createNewFile()) {
                            FileWriter fw = new FileWriter(f);
                            fw.write("test upload log file - " + sdf.format(d));
                            fw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String path = f.getAbsolutePath();
                f = null;
                sm.uploadLog(path, "SysComm", "gzip");
                return;
            } else {
                System.err.println("invalid command: " + cmd);
                runCmd(sm);
            }
        }
    }

    public boolean getUpgradeStatus() {
        return upgradeStatus;
    }

    private String getCmd(boolean isLogin, String user) {

        StringBuffer sb = new StringBuffer();
        sb.append("***********************************************************************\n");
        if (!isLogin) {
            sb.append("status\n\t\tshow the status of Agent.\n");
            sb.append(String.format("login %s\n\t\t%s\n", "<username> <password> [pp]",
                    "login gateway system. pp means prepstep,its value is true or false."));
            sb.append(String.format("forgot %s\n\t\t%s\n", "<username>", "find back the password"));
        } else {
            sb.append(String.format("update %s\n\t\t%s", "<type>", "update client,the type is in:"));
            sb.append(types);
            sb.append("\n");
            sb.append("prepsteps <type1> [<type2>...]\n\t\tprepstep for downloading software of device:");
            sb.append(types);
            sb.append("\n");
            sb.append("disconnect\n\t\tdisconnect the connection between RSA and RSS\n");
            sb.append("change <password>\n\t\tchange current user's password.\n");
            sb.append("connect <type> <sn>\n\t\tconnect device, the type is in:");
            sb.append(types);
            sb.append("\n");
            sb.append("getnc <type>\n\t\tget named configuration, the type is in:");
            sb.append(types);
            sb.append("\n");
            sb.append("sync <type> <sn>\n\t\tsynchronize named configuration to server, the type is in:");
            sb.append(types);
            sb.append("\n");
            sb.append("getmc <type> <sn>\n\t\tget matched named configuration, the type is in:");
            sb.append(types);
            sb.append("\n");
            sb.append("getnotification <oid>\n\t\tsend the getnotification message with oid alone.");
            sb.append("\n");
            if (deviceInfoSended) {
                sb.append(String.format("install %s\n\t\t%s\n", "<oid> [s|f]",
                        "upgrade software success(s) or failed(f)."));
                sb.append(String.format("open %s\n\t\t%s\n", "<oid>", "open a document (pdf format)."));
                sb.append(String.format("upload %s\n\t\t%s\n", "<type> <encode>",
                        "upload log, type - SysComm. encode - gzip"));
            }
            
        }
        if (isLogin) {
            sb.append("logout\n\t\tlogout gateway system.\n");
        }
        sb.append("register\n\t\tget the register page URL.\n");
        sb.append(String.format("quit\n\t\t%s\n", "quit the mock."));
        System.out.println(sb.toString());
        if (isLogin) {
            System.out.print(user + ">");
        } else {
            System.out.print("mock>");
        }
        try {
            return br.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
