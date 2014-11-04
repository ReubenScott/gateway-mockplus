package com.covidien.client.mock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import org.jboss.netty.channel.Channel;

import com.covidien.client.mock.handler.AcknowledgeInstalledResponseHandler;
import com.covidien.client.mock.handler.AcknowledgeResponseHandler;
import com.covidien.client.mock.handler.ChangePasswordResponseHandler;
import com.covidien.client.mock.handler.CloseSessionResponseHandler;
import com.covidien.client.mock.handler.CreateDescripencyResponseHandler;
import com.covidien.client.mock.handler.CreateDeviceInfoResponseHandler;
import com.covidien.client.mock.handler.CreateDeviceResponseHandler;
import com.covidien.client.mock.handler.CreateSessionResponseHandler;
import com.covidien.client.mock.handler.DisconnectResponseHandler;
import com.covidien.client.mock.handler.ForgotPasswordResponseHandler;
import com.covidien.client.mock.handler.GetHeadersResponseHandler;
import com.covidien.client.mock.handler.GetMatchedSysCfgResponseHandler;
import com.covidien.client.mock.handler.GetNotificationResponseHandler;
import com.covidien.client.mock.handler.GetStatusResponseHandler;
import com.covidien.client.mock.handler.GetSystemConfigResponseHandler;
import com.covidien.client.mock.handler.LogOffResponseHandler;
import com.covidien.client.mock.handler.LoginResponseHandler;
import com.covidien.client.mock.handler.OpenDocumentResponseHandler;
import com.covidien.client.mock.handler.PrepstepsResponseHandler;
import com.covidien.client.mock.handler.RegisterResponseHandler;
import com.covidien.client.mock.handler.StatDeviceResponseHandler;
import com.covidien.client.mock.handler.UpdateClientResponseHandler;
import com.covidien.client.mock.handler.UploadLogResponseHandler;
import com.covidien.laptopagent.xml.XMLMessage;
import com.covidien.laptopagent.xml.XMLMessageProcesser;
import com.covidien.laptopagent.xml.XMLParamComponent;
import com.covidien.laptopagent.xml.XMLParamLog;

public class SessionManager {
    private String sessionId;
    private Channel channel;
    private ClientMockHandler ioHandler;
    private CountDownLatch down = null;
    private boolean logined = false;
    private Thread thread;
    private String currentUser, password;

    private XMLMessageProcesser messageProcesser = new XMLMessageProcesser();

    public boolean isLogin() {
        return logined;
    }

    public void setLogin(boolean logined) {
        this.logined = logined;
    }

    public void setSessionId(String id) {
        sessionId = id;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setNettyIOHandler(ClientMockHandler ioHandler) {
        this.ioHandler = ioHandler;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void register(String actionId, ResponseHandler handler) {
        ioHandler.register(actionId, handler);
    }

    /**
     * create session
     */
    public void createSession() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><message schema_version=\"3644767c-2632-411a-9416-44f8a7dee08e\"><request type='createsession' xaction_guid='0f16a851-a043-498d-a857-77ee5c59499c'/></message>";
        register("0f16a851-a043-498d-a857-77ee5c59499c", new CreateSessionResponseHandler(this, new Date(), xml));
        System.out.print("create session ..... ");
        channel.write(xml);
    }

    /**
     * login
     */
    public void login(String username, String password, String prepsteps) {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<message schema_version=\"3644767c-2632-411a-9416-44f8a7dee08e\" session_guid='" + sessionId + "'>"
                + "<request type='login' xaction_guid='e1c38f30-50a0-4ccf-a74a-c7ec11b54544'>" + "<params><username>"
                + username + "</username><password>" + password + "</password>" + "<prepstep_enabled>" + prepsteps
                + "</prepstep_enabled></params></request></message>";
        register("e1c38f30-50a0-4ccf-a74a-c7ec11b54544", new LoginResponseHandler(this, new Date(), xml));
        System.out.print("login .... ");
        channel.write(xml);
        currentUser = username;
        this.password = password;
    }

    public void updateClient(String type) {
        XMLMessage msg = ClientMock.getMessage("17_clientupdate.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("17_clientupdate.xml");
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("17_clientupdate.xml", new UpdateClientResponseHandler(this, new Date(), xml));
                System.out.println("check client upgrade info .... ");
                channel.write(xml);
            }
        }
    }
    
    public void getnotification(String oid) {
        XMLMessage msg = ClientMock.getMessage("9_getNotificationRequest.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().getXmlParam().setNotificationOid(oid);
            msg.getHeaderRequest().setXactionGuid("9_getNotificationRequest.xml");
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("9_getNotificationRequest.xml", new GetNotificationResponseHandler(this, new Date(), xml));
                System.out.println("check client upgrade info .... ");
                channel.write(xml);
            }
        }
    }

    /**
     * get status
     */
    public synchronized void getStatus(boolean needThread) {
        if (needThread) {
            if (thread == null) {
                final SessionManager sm = this;
                thread = new Thread() {
                    @Override
                    public void run() {
                        while (true) {// send getstatus per 10 sec.
                            String actionId = "actionId-" + System.currentTimeMillis();

                            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message schema_version=\"3644767c-2632-411a-9416-44f8a7dee08e\" session_guid='"
                                    + sessionId
                                    + "'>"
                                    + "<request type=\"getstatus\" xaction_guid='"
                                    + actionId
                                    + "'/></message>";
                            register(actionId, new GetStatusResponseHandler(sm, new Date(), xml, down));
                            channel.write(xml);
                            try {
                                if (down != null) {
                                    Thread.sleep(5000);
                                }
                            } catch (InterruptedException e) {
                                if (down == null) {
                                    break;
                                }
                            }
                        }
                    }
                };
            }
            thread.start();
        } else {
            String actionId = "actionId-" + System.currentTimeMillis();
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message schema_version=\"3644767c-2632-411a-9416-44f8a7dee08e\" session_guid='"
                    + sessionId + "'>" + "<request type=\"getstatus\" xaction_guid='" + actionId + "'/></message>";
            register(actionId, new GetStatusResponseHandler(this, new Date(), xml, null));
            channel.write(xml);
        }
    }

    /**
     * get system configuration
     */
    public void getSysCfg() {
        XMLMessage msg = ClientMock.getMessage("15_getSystemConfig.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("15_getSystemConfig.xml");

            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("15_getSystemConfig.xml", new GetSystemConfigResponseHandler(this, new Date(), xml));
                System.out.println("getnc [" + MockLauncher.GUID + "] ");
                channel.write(xml);
            }
        } else {
            NotificationManager.getInstance().runCmd(this);
        }
    }

    /**
     * synchronize named configuration to server.
     */
    public void getSync() {
        // String sn = MockLauncher.devices.poll();
        // if (sn == null) {
        // System.out.println("no more device to process!");
        // logoff();
        // return;
        // }
        // ClientMock.curSerialNumber = sn;
        XMLMessage msg = ClientMock.getMessage("20_syncDeviceCfgfg.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("20_syncDeviceCfgfg.xml");

            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("20_syncDeviceCfgfg.xml", new GetMatchedSysCfgResponseHandler(this, new Date(), xml));
                System.out.println("syncDeviceCfg [" + ClientMock.curSerialNumber + "] ...");
                channel.write(xml);
            }
        }
    }

    /**
     * get matched system configuration
     */
    public void getMatchedSysCfg() {
        String sn = MockLauncher.currentSN;
        if (sn == null) {
            System.out.println("no more device to process!");
            logoff();
            return;
        }
        ClientMock.curSerialNumber = sn;
        XMLMessage msg = ClientMock.getMessage("16_getMatchedSysCfg.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("16_getMatchedSysCfg.xml");

            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("16_getMatchedSysCfg.xml", new GetMatchedSysCfgResponseHandler(this, new Date(), xml));
                System.out.println("getMatchedSysCfg [" + sn + "] ...");
                channel.write(xml);
            }
        }
    }

    /**
     * stat device info
     */
    public void statDevice() {
        String sn = MockLauncher.currentSN;
        if (sn == null) {
            System.out.println("no more device to process!");
            logoff();
            return;
        }
        ClientMock.curSerialNumber = sn;
        XMLMessage msg = ClientMock.getMessage("6_statDeviceRequest.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("6_statDeviceRequest.xml");

            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("6_statDeviceRequest.xml", new StatDeviceResponseHandler(this, new Date(), xml));
                System.out.println("stat device [" + sn + "] ...");
                channel.write(xml);
            }
        }
    }

    /**
     * create device
     */
    public void createDevice() {
        XMLMessage msg = ClientMock.getMessage("4_createDeviceRequest.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("4_createDeviceRequest.xml");
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("4_createDeviceRequest.xml", new CreateDeviceResponseHandler(this, new Date(), xml));
                System.out.println("create device .... ");
                channel.write(xml);
            }
        }
    }

    /**
     * create device info
     */
    public void createDeviceInfo() {
        XMLMessage msg = ClientMock.getMessage("5_createdeviceInfo.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("5_createdeviceInfo.xml");
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("5_createdeviceInfo.xml", new CreateDeviceInfoResponseHandler(this, new Date(), xml));
                System.out.println("create device info ....");
                channel.write(xml);
            }
        }
    }

    public void createDescripency() {
        XMLMessage msg = ClientMock.getMessage("5_createdeviceInfo.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("5x_createdeviceInfo.xml");
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("5x_createdeviceInfo.xml", new CreateDescripencyResponseHandler(this, new Date(), xml));
                System.out.println("create discrepency...");
                channel.write(xml);
            }
        }
    }

    /**
     * get headers
     */
    public void getHeaders() {
        if (MockLauncher.prompt) {
            down = new CountDownLatch(1);
            getStatus(true);
            try {
                down.await();
                down = null;
                thread.interrupt();
                thread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        XMLMessage msg = ClientMock.getMessage("7_getHeadersRequest.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            msg.getHeaderRequest().setXactionGuid("7_getHeadersRequest.xml");

            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register("7_getHeadersRequest.xml", new GetHeadersResponseHandler(this, new Date(), xml));
                System.out.println("get headers ....");
                channel.write(xml);
            }
        }
    }

    /**
     * get notification
     * 
     * @param oid
     */
    public void getNotification(String oid) {
        XMLMessage msg = ClientMock.getMessage("9_getNotificationRequest.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "9_getNotificationRequest.xml_" + oid;
            msg.getHeaderRequest().setXactionGuid(actionid);

            msg.getHeaderRequest().getXmlParam().setNotificationOid(oid);
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register(actionid, new GetNotificationResponseHandler(this, new Date(), xml));
                channel.write(xml);
            }
        }

    }

    public void openDocument(String oid, XMLParamComponent component) {
        XMLMessage msg = ClientMock.getMessage("10_openDocument.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "10_openDocument.xml_" + oid;
            msg.getHeaderRequest().setXactionGuid(actionid);

            XMLParamComponent comp = msg.getHeaderRequest().getXmlNotification().getBody().getXMLParamSoftwareList()
                    .get(0);
            // comp.setComponentType("document");
            comp.setLocalPath(component.getLocalPath());
            comp.setMd5(component.getMd5());
            comp.setFilesize(component.getFilesize());
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register(actionid, new OpenDocumentResponseHandler(this, new Date(), xml));
                channel.write(xml);
            }
        }
    }

    /**
     * acknowledge the installation
     * 
     * @param component
     */
    public void acknowledge(String oid, XMLParamComponent component) {
        XMLMessage msg = ClientMock.getMessage("8_acknowledge.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "8_acknowledge.xml_" + oid;
            msg.getHeaderRequest().setXactionGuid(actionid);

            msg.getHeaderRequest().getXmlNotification().setOid(oid);
            XMLParamComponent ackComponent = msg.getHeaderRequest().getXmlNotification().getBody()
                    .getXMLParamSoftwareList().get(0);
            ackComponent.setComponentType("SOFTWARE");
            ackComponent.setComponentName(component.getComponentName());
            ackComponent.setPartNumber(component.getPartNumber());
            ackComponent.setSoftwareRevision(component.getSoftwareRevision());
            // ackComponent.setStatus("installed");
            ackComponent.setStatus("start");
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy h:m:s a", Locale.ENGLISH);
            ackComponent.setTimestamp(sdf.format(new Date()));

            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                System.out.println("acknowledge : " + oid + "...");
                register(actionid, new AcknowledgeResponseHandler(this, new Date(), oid, ackComponent, xml));
                System.out.println("Start upgrading....");
                channel.write(xml);
            }
        }

    }

    public void acknowledgeInstalled(String oid, XMLParamComponent component, boolean status) {
        XMLMessage msg = ClientMock.getMessage("8_acknowledge.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "8_acknowledge_installed.xml_" + oid;
            msg.getHeaderRequest().setXactionGuid(actionid);

            msg.getHeaderRequest().getXmlNotification().setOid(oid);
            component.setStatus(status ? "installed" : "failed");
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy h:m:s a", Locale.ENGLISH);
            component.setTimestamp(sdf.format(new Date()));

            msg.getHeaderRequest().getXmlNotification().getBody().getXMLParamSoftwareList().clear();

            msg.getHeaderRequest().getXmlNotification().getBody().getXMLParamSoftwareList().add(component);

            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                System.out.println("acknowledge install: " + oid + "...");
                register(actionid, new AcknowledgeInstalledResponseHandler(this, new Date(), component, xml));
                channel.write(xml);
            }
        }
    }

    public void uploadLog(String file, String type, String encode) {
        XMLMessage msg = ClientMock.getMessage("11_uploadLog.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "11_uploadLog.xml";
            msg.getHeaderRequest().setXactionGuid(actionid);
            XMLParamLog log = msg.getHeaderRequest().getXmlNotification().getBody().getXMLParamLogList().get(0);

            log.setEncoding(encode);
            log.setName(type);
            log.setUriInc(file);

            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                System.out.println("uploading log: " + file + "...");
                register(actionid, new UploadLogResponseHandler(this, new Date(), xml));
                channel.write(xml);
            }
        }
    }

    public void logoff() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<message schema_version=\"3644767c-2632-411a-9416-44f8a7dee08e\" session_guid='" + sessionId + "'>"
                + "<request type='logoff' xaction_guid='e1c38f30-50a0-4ccf-a74a-c7ec11b50987'>" + "<params><username>"
                + currentUser + "</username></params></request></message>";
        register("e1c38f30-50a0-4ccf-a74a-c7ec11b50987", new LogOffResponseHandler(this, new Date(), xml));
        System.out.println("logoff .... ");
        channel.write(xml);
    }

    public void close() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><message schema_version=\"3644767c-2632-411a-9416-44f8a7dee08e\" session_guid='"
                + sessionId
                + "'><request type='closesession' xaction_guid='9650e555-810d-487b-94bf-da8d2f0e334e'/></message>";
        System.out.println("close session .... ");
        channel.write(xml);
        register("9650e555-810d-487b-94bf-da8d2f0e334e", new CloseSessionResponseHandler(this, new Date(), xml));
    }

    public void forgetPassword(String username) {
        XMLMessage msg = ClientMock.getMessage("14_forgotPassword.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "14_forgotPassword.xml";
            msg.getHeaderRequest().setXactionGuid(actionid);
            msg.getHeaderRequest().getXmlParam().setUsername(username);
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register(actionid, new ForgotPasswordResponseHandler(this, new Date(), xml));
                channel.write(xml);
                System.out.println("forgot password....");
                return;
            }
        }
        System.err.println("error forget password message");
        NotificationManager.getInstance().runCmd(this);
    }

    public void disconnect() {
        XMLMessage msg = ClientMock.getMessage("12_DisconnectRequest.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "12_DisconnectRequest.xml";
            msg.getHeaderRequest().setXactionGuid(actionid);
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register(actionid, new DisconnectResponseHandler(this, new Date(), xml));
                channel.write(xml);
                System.out.println("disconnect RSA from RSS....");
                return;
            }
        }
        System.err.println("error disconnect message");
        NotificationManager.getInstance().runCmd(this);

    }

    public void changePassword(String string) {
        XMLMessage msg = ClientMock.getMessage("13_changepasswordRequest.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "13_changepasswordRequest.xml";
            msg.getHeaderRequest().setXactionGuid(actionid);
            msg.getHeaderRequest().getXmlParam().setUsername(currentUser);
            msg.getHeaderRequest().getXmlParam().setPassword(password);
            msg.getHeaderRequest().getXmlParam().setNewPassword(string);
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register(actionid, new ChangePasswordResponseHandler(this, new Date(), xml));
                channel.write(xml);
                System.out.println("Change password ....");
                return;
            }
        }
        System.err.println("error change message");
        NotificationManager.getInstance().runCmd(this);
    }

    /**
     * prepsteps.
     * 
     * @param devices
     *        device GUIDs.
     */
    public void prepsteps(ArrayList<String> devices) {
        XMLMessage msg = ClientMock.getMessage("prepsteps.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "prepsteps.xml";
            msg.getHeaderRequest().setXactionGuid(actionid);
            ArrayList<String> deviceTypes = new ArrayList<String>();
            for (String d : devices) {
                deviceTypes.add(MockLauncher.guids.get(d));
            }
            msg.getHeaderRequest().getXmlParam().setDeviceTypes(deviceTypes);
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register(actionid, new PrepstepsResponseHandler(this, new Date(), xml));
                channel.write(xml);
                System.out.println("Prepsteps ....");
                return;
            }
        }
        System.err.println("error prepstpes message");
        NotificationManager.getInstance().runCmd(this);
    }

    public void getRegisterURL() {
        XMLMessage msg = ClientMock.getMessage("register.xml");
        if (msg != null) {
            msg.setSessionGuid(sessionId);
            String actionid = "register.xml";
            msg.getHeaderRequest().setXactionGuid(actionid);
            String xml = messageProcesser.marshallToString(msg);
            if (xml != null) {
                register(actionid, new RegisterResponseHandler(this, new Date(), xml));
                channel.write(xml);
                System.out.println("get register page URL....");
                return;
            }
        }
        System.err.println("error register message");
        NotificationManager.getInstance().runCmd(this);

    }
}
