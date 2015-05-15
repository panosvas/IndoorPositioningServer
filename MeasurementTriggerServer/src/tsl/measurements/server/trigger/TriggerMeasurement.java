package tsl.measurements.server.trigger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TriggerMeasurement {
	
	public static void main(String[] args) throws IOException{
		
		if (args.length != 7){
			
			System.out.println("Usage:");
			System.out.println("TriggerMeasurement <IP> <PORT> <AREA> <#OF_WIFI_MEASUREMENTS> <#OF_BT_MEASUREMENTS> <#OF_MAGNETIC_MEASUREMENTS> <#OF_BLE_MEASUREMENTS>");
			
			throw new IOException("Six inputs are needed.");
		}
		
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		String area = args[2];
		String wifiMeasurements = args[3];
		String btMeasurements = args[4];
		String magneticMeasurements = args[5];
		String bleMeasurements = args[4];
		
		String messageStr = "measure=" + area + "," + wifiMeasurements + "," + btMeasurements + "," + magneticMeasurements + "," + bleMeasurements;
        int server_port = port;
        DatagramSocket s;
        try {
            s = new DatagramSocket((server_port));
            s.setReuseAddress(true);
            InetAddress local = InetAddress.getByName(ip);
            int msg_length=messageStr.length();
            byte[] message = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
            s.send(p);
            s.close();
        } catch (SocketException e) {
            System.out.println("SocketException");
            e.printStackTrace();
        } catch (UnknownHostException e) {
        	System.out.println("UnknownHostException");
            e.printStackTrace();
        } catch (IOException e) {
        	System.out.println("IOException");
            e.printStackTrace();
        }
		
	}

}
