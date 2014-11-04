package com.covidien.client.mock.handler;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import com.covidien.client.mock.AgentStatus;
import com.covidien.client.mock.MockLauncher;
import com.covidien.client.mock.NotificationManager;
import com.covidien.client.mock.ResponseHandler;
import com.covidien.client.mock.SessionManager;

public class GetStatusResponseHandler extends ResponseHandler {
	private CountDownLatch down;

	public GetStatusResponseHandler(SessionManager sm, Date date, String xml,
			CountDownLatch down) {
		super(sm, date, xml);
		this.down = down;
	}

	@Override
	public void run() {
		if (response.getType().equals("param")) {
			try {
				if (!MockLauncher.prompt && down != null) {
					down.countDown();
				}
				AgentStatus as = new AgentStatus(xml);
				StringBuffer sb = new StringBuffer();
				if (down != null) {
					// sb.append(", upload_time:");
					// sb.append(as.getUploadTime());
					sb.append("remaining:");
					sb.append(as.getdownloadTime());
					if (MockLauncher.prompt && as.isDownloaded()) {
						down.countDown();
					}
					System.out.println(sb.toString());
				} else {
					sb.append("========get status result[" + date + "]=======");
					sb.append("\nagent_status:");
					sb.append(as.getAgentStatus());
					sb.append("\ndata_ready:");
					sb.append(as.getData());
					sb.append("\ndatetime:");
					sb.append(as.getDateTime());
					sb.append("\njob_status:");
					sb.append(as.getJobStatus());
					sb.append("\nlast_update:");
					sb.append(as.getLastUpdate());
					sb.append("\nserver_status:");
					sb.append(as.getServerStatus());
					sb.append("\n==============================================================\n");
					System.out.println(sb.toString());
					NotificationManager.getInstance().runCmd(sessionManager);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("get status failed :" + date);
		}
	}
}
