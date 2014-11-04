package com.covidien.client.mock;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.covidien.laptopagent.xml.XMLMessage;
import com.covidien.laptopagent.xml.XMLMessageProcesser;
import com.covidien.laptopagent.xml.XMLMessageResponse;

public class ClientMockHandler extends SimpleChannelHandler {
	private SessionManager sm;
	private final Map<String, ResponseHandler> handlers;
	private ExecutorService es;

	public ClientMockHandler(SessionManager sm) {
		this.sm = sm;
		this.sm.setNettyIOHandler(this);
		handlers = new ConcurrentHashMap<String, ResponseHandler>();
		es = Executors.newCachedThreadPool();
	}

	public void register(String actionId, ResponseHandler handler) {
		handlers.put(actionId, handler);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelClosed(ctx, e);
		System.out.println("the channel is closed.");
		System.exit(0);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		sm.setChannel(ctx.getChannel());
		sm.createSession();		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		ctx.getChannel().close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		String xml = (String) e.getMessage();
		// System.out.println(xml);
		XMLMessageProcesser processor = new XMLMessageProcesser();
		ByteArrayInputStream byteInput = new ByteArrayInputStream(
				xml.getBytes());
		XMLMessage message = processor.parseMessage(byteInput);
		if (message == null) {
			System.out.println("fail to parse xml to object: " + xml);
			return;
		}
		XMLMessageResponse response = message.getHeaderResponse();
		if (response != null) {
			String actionId = response.getXactionGuid();
			try {
				ResponseHandler rh = handlers.remove(actionId);

				if (rh == null) {
					throw new NullPointerException();
				}
				rh.setMessage(message, xml);
				es.submit(rh);
			} catch (Exception ee) {
				System.err.println("no handler for :" + actionId);
				System.err.println(xml);
			}
		} else {
			System.out.println("fail to parse xml to object: " + xml);
		}
	}
}
