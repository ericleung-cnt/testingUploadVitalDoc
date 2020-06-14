package org.mardep.ssrs.dmi.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mardep.ssrs.fsqcwebService.pojo.FsqcCertResultPojo;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component("SimulateFsqcCertReply")
public class SimulateFsqcCertReplyServlet  implements HttpRequestHandler {
	protected final Logger logger = LoggerFactory.getLogger(SimulateFsqcCertReplyServlet.class);

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		System.out.println("ipAddr: " + ipAddress);
		
		String hdrAuthorization = request.getHeader("Authorization");
		if (hdrAuthorization == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			//logger.info("not have header Authorization");
			return;
		}

		StringBuffer jb = new StringBuffer();
		String line = null;
		FsqcShipResultData result = new FsqcShipResultData();
		ObjectMapper mapper = new ObjectMapper();
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}

			// JSON string to Java Object
			//FsqcShipDetainData ship_data = mapper.readValue(jb.toString(), FsqcShipDetainData.class);
			//submitFsqcShip(ship_data, result);
			System.out.println(jb.toString());
			FsqcCertResultPojo resultData = mapper.readValue(jb.toString(), FsqcCertResultPojo.class);			
			result.setSuccess(false);
			result.setMessage("got");
		} catch (Exception e) {
			/* report an error */
			logger.error(e.getMessage(), e);
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}

		String json_output = mapper.writeValueAsString(result);
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(json_output);

		logger.info(json_output);
	}

}
