package com.tsl.positioning.server;

import java.util.Properties;

import javax.jms.*;
import javax.naming.*;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import matlabcontrol.MatlabInvocationException;

import com.google.gson.Gson;

@Path("/positioningService")
public class PositioningService {

	@POST
	@Produces("application/json")
	public Response calculatePosition(
			@DefaultValue("-") @FormParam("WiFiMeasurements") String wifiMeasurements,
			@DefaultValue("-") @FormParam("BeaconMeasurements") String beaconMeasurements,
			@DefaultValue("-") @FormParam("MagneticMeasurements") String magneticMeasurements) {

		Gson gson = new Gson();
		System.out.println("POSITIONING!");
		System.out.println(wifiMeasurements);
		System.out.println(beaconMeasurements);
		System.out.println(magneticMeasurements);

		if (!wifiMeasurements.equals("-")) {

			DeviceWifiMeasurements[] deviceWifiMeasurements = gson.fromJson(
					wifiMeasurements, DeviceWifiMeasurements[].class);

		} else {
			DeviceWifiMeasurements[] deviceWifiMeasurements = null;
		}

		if (!beaconMeasurements.equals("-")) {

			DeviceBeaconMeasurements[] deviceBeaconMeasurements = gson
					.fromJson(beaconMeasurements,
							DeviceBeaconMeasurements[].class);

		} else {
			DeviceBeaconMeasurements[] deviceBeaconMeasurements = null;
		}

		if (!magneticMeasurements.equals("-")) {

			DeviceMagneticMeasurements[] deviceMagneticMeasurements = gson
					.fromJson(magneticMeasurements,
							DeviceMagneticMeasurements[].class);

		} else {
			DeviceMagneticMeasurements[] deviceMagneticMeasurements = null;
		}

		String jsonResponse = null;

		// MATLAB Proccessing here...
		try {
			Object[] arguments = new Object[2];
			arguments[0] = new Double(5);
			arguments[1] = new Double(1.5);

			Object[] myfunOut = AppServletContextListener.proxy.returningFeval(
					"myfun", 2, arguments);
			System.out.println(((double[]) myfunOut[0])[0]);
			System.out.println(((double[]) myfunOut[1])[0]);

			UserResponse userResponse = new UserResponse();
			userResponse.setXCoordinate(String
					.valueOf(((double[]) myfunOut[0])[0]));
			userResponse.setYCoordinate(String
					.valueOf(((double[]) myfunOut[1])[0]));

			jsonResponse = gson.toJson(userResponse);

		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			UserResponse userResponse = new UserResponse();
			userResponse.setXCoordinate("-");
			userResponse.setYCoordinate("-");

			jsonResponse = gson.toJson(userResponse);

			// return Response.status(200).entity(jsonResponse).build();
		}

		// Activemq here...

		String queueName = "myQueue";
		String user = "system";
		String password = "manager";
		String url = "tcp://hostname:61616";

		// JNDI properties
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL, url);

		// specify queue propertyname as queue.jndiname
		props.setProperty("queue.slQueue", queueName);

		try {
			Context ctx = new InitialContext(props);
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			Connection connection = connectionFactory.createConnection(user,
					password);
			connection.start();

			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination destination = (Destination) ctx.lookup("slQueue");

			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Create a messages
			String text = "Hello world!";
			TextMessage message = session.createTextMessage(text);

			// Tell the producer to send the message
			System.out.println("Sent message: " + message.hashCode() + " : "
					+ Thread.currentThread().getName());
			producer.send(message);

			// Clean up
			session.close();
			connection.close();

		} catch (Exception e) {

		}

		// Send response to Device
		return Response.status(200).entity(jsonResponse).build();
	}
}