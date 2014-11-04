package com.covidien.client.mock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;

import com.covidien.laptopagent.xml.XMLMessage;
import com.covidien.laptopagent.xml.XMLMessageProcesser;

public class ClientMock {

    /**
     * Framer.
     */
    private static final String FRAMER = "Framer";

    /**
     * Decoder.
     */
    private static final String DECODER = "Decoder";
    /**
     * Encoder.
     */
    private static final String ENCODER = "Encoder";
    /**
     * Handler.
     */
    private static final String HANDLER = "Handler";
    /**
     * Buffer size.
     */
    private static final int MAX_BUFFER = 1024000;
    private static final byte[] XML_END_TOKEN = new byte[] {
            '<', '/', 'm', 'e', 's', 's', 'a', 'g', 'e', '>' };
    private static ChannelBuffer xmlEndBuffer = ChannelBuffers.wrappedBuffer(XML_END_TOKEN);
    private ClientBootstrap bootstrap;
    private SessionManager sm;
    public static String curSerialNumber = "";

    public ClientMock() {
        sm = new SessionManager();
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory());
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline()
                throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast(FRAMER, new DelimiterBasedFrameDecoder(MAX_BUFFER, false, xmlEndBuffer));
                pipeline.addLast(DECODER, new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(ENCODER, new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(HANDLER, new ClientMockHandler(sm));
                return pipeline;
            }
        });
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
    }

    public void start() {
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 9999));
        try {
            channelFuture.sync().awaitUninterruptibly();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static XMLMessage getMessage(String filename) {
        String line = null;

        if (filename.startsWith("5_createdeviceInfo")) {
            if (MockLauncher.GUID.equals("61e08b77-df3c-4735-9f3b-0b42efb7bdcf")) {
                filename = "5_createdeviceInfo_scdu.xml";
            } else if (MockLauncher.GUID.equals("DACD7FA1-9D67-4057-9952-C55F8EA6227B")) {
                filename = "5_createdeviceInfo_fcd.xml";
            } else if (MockLauncher.GUID.equals("3B682913-6D1E-4355-9E48-208EB7061A3D")) {
                filename = "5_createdeviceInfo_em.xml";
            } else if (MockLauncher.GUID.equals("BD5CE934-26AE-484A-8395-EB0FD29F6838")) {
                filename = "5_createdeviceInfo_ctp.xml";
            }
        } else if (filename.startsWith("17_clientupdate")) {
            if (MockLauncher.GUID.equals("61e08b77-df3c-4735-9f3b-0b42efb7bdcf")) {
                filename = "17_clientupdate_scdu.xml";
                ClientMock.curSerialNumber = "SCD 700";
            } else if (MockLauncher.GUID.equals("DACD7FA1-9D67-4057-9952-C55F8EA6227B")) {
                filename = "17_clientupdate_fcd.xml";
                ClientMock.curSerialNumber = "ForceTriad";
            } else if (MockLauncher.GUID.equals("3B682913-6D1E-4355-9E48-208EB7061A3D")) {
                filename = "17_clientupdate_em.xml";
                ClientMock.curSerialNumber = "Valleylab LS10";
            }
        }

        File f = new File("./messages/" + filename);
        if (f.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                StringBuffer sb = new StringBuffer();
                try {
                    line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = br.readLine();
                    }
                    line = sb.toString();
                } catch (Exception e) {

                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                    }
                }

            } catch (FileNotFoundException e) {
            }
        }

        if (line != null) {
            line = line.replaceAll("@SN@", MockLauncher.currentSN);
            line = line.replaceAll("@GUID@", MockLauncher.GUID);
            XMLMessageProcesser processor = new XMLMessageProcesser();
            ByteArrayInputStream byteInput = new ByteArrayInputStream(line.getBytes());
            XMLMessage message = processor.parseMessage(byteInput);
            return message;
        }
        System.out.println("cannot access file: ./messages/" + filename);
        return null;
    }

    public void stop() {
        sm.close();
    }
}
