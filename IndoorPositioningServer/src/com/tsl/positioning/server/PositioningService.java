package com.tsl.positioning.server;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import matlabcontrol.MatlabInvocationException;

import com.google.gson.Gson;

@Path("/positioningService")
public class PositioningService {

	@GET
	@Produces("application/json")
	public Response calculatePosition(
			@DefaultValue("-") @QueryParam("APs_SSID") String APsSSID,
			@DefaultValue("-") @QueryParam("APs_RSS") String APsRSS) {

		Gson gson = new Gson();

		if (!APsSSID.equals("-") && !APsRSS.equals("-")) {

			String[] inputAPsSSID = APsSSID.split(",");
			String[] inputAPsRSS = APsRSS.split(",");

			if (inputAPsSSID.length == inputAPsRSS.length) {

				APInfo[] aPsInfo = new APInfo[inputAPsSSID.length];

				for (int i = 0; i < inputAPsSSID.length; i++) {

					APInfo aPInfo = new APInfo();
					aPInfo.setAPSSID(inputAPsSSID[i]);
					aPInfo.setAPRSS(inputAPsRSS[i]);

					aPsInfo[i] = aPInfo;

				}

				// MATLAB Proccessing here...
				try {
					AppServletContextListener.proxy.eval("disp('hello world')");
					Object[] arguments = new Object[2];
					arguments[0] = new Double(5);
					arguments[1] = new Double(1.5);

					Object[] myfunOut = AppServletContextListener.proxy.returningFeval("myfun",
							2, arguments);
					System.out.println(((double[]) myfunOut[0])[0]);
					System.out.println(((double[]) myfunOut[1])[0]);

					UserResponse userResponse = new UserResponse();
					userResponse.setXCoordinate(String
							.valueOf(((double[]) myfunOut[0])[0]));
					userResponse.setYCoordinate(String
							.valueOf(((double[]) myfunOut[1])[0]));

					String jsonResponse = gson.toJson(userResponse);

					return Response.status(200).entity(jsonResponse).build();

				} catch (MatlabInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					UserResponse userResponse = new UserResponse();
					userResponse.setXCoordinate("-");
					userResponse.setYCoordinate("-");

					String jsonResponse = gson.toJson(userResponse);

					return Response.status(200).entity(jsonResponse).build();
				}

			} else {

				UserResponse userResponse = new UserResponse();
				userResponse.setXCoordinate("-");
				userResponse.setYCoordinate("-");

				String jsonResponse = gson.toJson(userResponse);

				return Response.status(200).entity(jsonResponse).build();

			}

		} else {

			UserResponse userResponse = new UserResponse();
			userResponse.setXCoordinate("-");
			userResponse.setYCoordinate("-");

			String jsonResponse = gson.toJson(userResponse);

			return Response.status(200).entity(jsonResponse).build();

		}
	}
}