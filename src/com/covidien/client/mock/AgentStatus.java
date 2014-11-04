package com.covidien.client.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class AgentStatus {
	private static DocumentBuilder builder;
	static {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	private Document doc;

	public AgentStatus(String xml) throws SAXException, IOException {
		ByteArrayInputStream byteInput = new ByteArrayInputStream(
				xml.getBytes());
		doc = builder.parse(byteInput);
	}

	public String valueOf(String path) {
		return valueOf(path, "");
	}

	public String valueOf(String path, String defaults) {
		try {
			XPath xpath = new DOMXPath(path);
			return xpath.stringValueOf(doc);
		} catch (JaxenException e) {
			e.printStackTrace();
			return defaults;
		}
	}

	public String getAgentStatus() {
		return valueOf("message/response/params/agent_status");
	}

	public String getData() {
		return valueOf("message/response/params/data_ready");
	}

	public String getDateTime() {
		return valueOf("message/response/params/datetime");
	}

	public String getJobStatus() {
		return valueOf("message/response/params/job_status");
	}

	public String getLastUpdate() {
		return valueOf("message/response/params/last_update");
	}

	public String getServerStatus() {
		return valueOf("message/response/params/server_status");
	}

	public String getUploadTime() {
		return valueOf("message/response/params/upload_time");
	}

	public String getdownloadTime() {
		return valueOf("message/response/params/download_time");
	}

	public boolean isDownloaded() {
		String t = valueOf("message/response/params/download_time");
		float time = -1;
		try {
			time = Float.parseFloat(t);
		} catch (Exception e) {

		}
		return time == 0;
	}

	@Override
	public String toString() {
		return doc.toString();
	}
}
