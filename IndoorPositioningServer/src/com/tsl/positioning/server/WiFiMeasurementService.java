package com.tsl.positioning.server;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;


@Path("/measurementService")
public class WiFiMeasurementService {
	
	@GET
    @Produces("application/json")
    public Response measurementStorage(@Context HttpServletRequest request,
    		@DefaultValue("-") @QueryParam("WiFiMeasurements") String wifiInfos,
    		@DefaultValue("-") @QueryParam("BTMeasurements") String btInfos,
    		@DefaultValue("-") @QueryParam("MagneticCalibratedMeasurements") String magneticCalibratedInfos,
    		@DefaultValue("-") @QueryParam("MagneticUncalibratedMeasurements") String magneticUncalibratedInfos) {
		
		if (wifiInfos.equals("-") || wifiInfos.equals("[]")){
			wifiInfos = null;
		}
		
		if (btInfos.equals("-") || btInfos.equals("[]")){
			btInfos = null;
		}
		
		if (magneticCalibratedInfos.equals("-") || magneticCalibratedInfos.equals("[]")){
			magneticCalibratedInfos = null;
		}
		
		if (magneticUncalibratedInfos.equals("-") || magneticUncalibratedInfos.equals("[]")){
			magneticUncalibratedInfos = null;
		}
		
		Gson gson = new Gson();
		
		WifiStats[] wifiMeasurements = null;
		BTStats[] btMeasurements = null;
		MagneticCalibrated[] magneticCalibratedMeasurements = null;
		MagneticUncalibrated[] magneticUncalibratedMeasurements = null;
		
		try {
			wifiMeasurements = gson.fromJson(wifiInfos, WifiStats[].class);
		} catch (Exception e){
			System.out.println("Error while transforming the Wifi Measurements.");
		}
		
		try {
			btMeasurements = gson.fromJson(btInfos, BTStats[].class);
		} catch (Exception e){
			System.out.println("Error while transforming the BT Measurements.");
		}
		
		try {
			magneticCalibratedMeasurements = gson.fromJson(magneticCalibratedInfos, MagneticCalibrated[].class);
		} catch (Exception e){
			System.out.println("Error while transforming the Magnetic Calibrated Measurements.");
		}
		
		try {
			magneticUncalibratedMeasurements = gson.fromJson(magneticUncalibratedInfos, MagneticUncalibrated[].class);
		} catch (Exception e){
			System.out.println("Error while transforming the Magnetic Uncalibrated Measurements.");
		}
		
	  	try {
	  		
	  		String driver = "com.mysql.jdbc.Driver";
	  		Class.forName(driver);
        	
            String dbURL = "jdbc:mysql://localhost/positioning?user=panos&password=panos";
            Connection con = DriverManager.getConnection(dbURL);
            
            if (con != null) {
            	
                System.out.println("Connected to database");
                Statement stmt = con.createStatement();
                
                if (wifiMeasurements != null){
	                for (WifiStats wifi : wifiMeasurements){
	                	stmt.execute("insert into wifi_measurement(area , bssid, ssid, rssi, time) values ('" + wifi.getArea() + "', '" + wifi.getBssid() + "', '" + wifi.getSsid() + "', '" + wifi.getRssi() + "', '" + wifi.getTime() + "')");
	                }
                }
                
                if (btMeasurements != null){
	                for (BTStats bt : btMeasurements){
	                	stmt.execute("insert into bt_measurement(area , bssid, ssid, rssi, time) values ('" + bt.getArea() + "', '" + bt.getBssid() + "', '" + bt.getSsid() + "', '" + bt.getRssi() + "', '" + bt.getTime() + "')");
	                }
                }
                
                if (magneticCalibratedMeasurements != null){
	                for (MagneticCalibrated magneticCalibrated : magneticCalibratedMeasurements){
	                	stmt.execute("insert into magnetic_calibrated_measurement(area , x_calibrated, y_calibrated, z_calibrated, time) values ('" + magneticCalibrated.getArea() + "', '" + magneticCalibrated.getxValue() + "', '" + magneticCalibrated.getyValue() + "', '" + magneticCalibrated.getzValue() + "', '" + magneticCalibrated.getTime() + "')");
	                	System.out.println("MAGNETIC FIELD X: " + magneticCalibrated.getxValue());
	                	System.out.println("MAGNETIC FIELD Y: " + magneticCalibrated.getyValue());
	                	System.out.println("MAGNETIC FIELD Z: " + magneticCalibrated.getzValue());
	                }
                }
                
                if (magneticUncalibratedMeasurements != null){
	                for (MagneticUncalibrated magneticUncalibrated : magneticUncalibratedMeasurements){
	                	stmt.execute("insert into magnetic_uncalibrated_measurement(area , x_uncalibrated, y_uncalibrated, z_uncalibrated, x_bias, y_bias, z_bias, time) values ('" + magneticUncalibrated.getArea() + "', '" + magneticUncalibrated.getxValueUncalib()+ "', '" + magneticUncalibrated.getyValueUncalib() + "', '" + magneticUncalibrated.getzValueUncalib() + "', '" + magneticUncalibrated.getxBias() + "', '" + magneticUncalibrated.getyBias() + "', '" + magneticUncalibrated.getzBias() + "', '" + magneticUncalibrated.getTime() + "')");
	                }
                }
                
                // Remove comment below to print statistics in Server console
                
                /*System.out.println(" ");
                System.out.println("############### WiFi MEASUREMENTS ###############");
                System.out.println(" ");
                
                ResultSet results = stmt.executeQuery("select * from wifi_measurement");
                ResultSetMetaData rsmd = results.getMetaData();
                int numberCols = rsmd.getColumnCount();
                for (int i=1; i<=numberCols; i++)
                {
                    //print Column Names
                    System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
                }

                System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------------------------");

                while(results.next())
                {
                    System.out.println(results.getString(1) + "\t\t" + results.getString(2) + "\t\t" + results.getString(3) + "\t\t" + results.getString(4) + "\t\t" + results.getString(5) + "\t\t" + results.getString(6));
                }
                
                System.out.println(" ");
                System.out.println("############### BT MEASUREMENTS ###############");
                System.out.println(" ");
                
                results = stmt.executeQuery("select * from bt_measurement");
                rsmd = results.getMetaData();
                numberCols = rsmd.getColumnCount();
                for (int i=1; i<=numberCols; i++)
                {
                    //print Column Names
                    System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
                }

                System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------------------------");

                while(results.next())
                {
                    System.out.println(results.getString(1) + "\t\t" + results.getString(2) + "\t\t" + results.getString(3) + "\t\t" + results.getString(4) + "\t\t" + results.getString(5) + "\t\t" + results.getString(6));
                }*/
                
                
                stmt.close();
                
                con.close();        

            }
           
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	  	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
		Date date = new Date();
		String currentTime = dateFormat.format(date);
		
		System.out.println("############### LAST MEASUREMENTS RETRIEVIED AT: " + currentTime + "###############");
		
	  	return Response.status(200).entity("{}").build();
    }
	

}
