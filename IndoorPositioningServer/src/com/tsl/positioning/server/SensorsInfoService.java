package com.tsl.positioning.server;
 

import java.sql.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


@Path("/sensorService")
public class SensorsInfoService {
	
	@GET
    @Produces("application/json")
    public Response monitorSensors(@Context HttpServletRequest request,
    		@DefaultValue("-") @QueryParam("MAC_Address") String macAddress,
            @DefaultValue("-") @QueryParam("Ambient_light") String ambientLight,
            @DefaultValue("-") @QueryParam("Accelometer") String accelometer,
            @DefaultValue("-") @QueryParam("Magnetometer") String magnetometer,
            @DefaultValue("-") @QueryParam("Gyroscope") String gyroscope,
            @DefaultValue("-") @QueryParam("Compass") String compass,
            @DefaultValue("-") @QueryParam("Barometer") String barometer
            ) {
  	  
		//String ip = request.getRemoteAddr().toString();
		
  	  	SensorInfo sensorInfo = new SensorInfo();
  	  
	  	sensorInfo.setAccelometer(accelometer);
	  	sensorInfo.setAmbientLight(ambientLight);
	  	sensorInfo.setBarometer(barometer);
	  	sensorInfo.setCompass(compass);
	  	sensorInfo.setGyroscope(gyroscope);
	  	sensorInfo.setMagnetometer(magnetometer);
	  	
	  	// store to database the sensors info with the IP as primary key.
	  	
	  	try {
	  		
	  		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	  		Class.forName(driver);
        	
            String dbURL = "jdbc:derby:codejava/webdb1;create=true";
            Connection con = DriverManager.getConnection(dbURL);
            
            if (con != null) {
            	
                System.out.println("Connected to database");
                Statement stmt = con.createStatement();
                
                DatabaseMetaData dbmd = con.getMetaData();
                ResultSet rs = dbmd.getTables(null, "APP", "SENSORMONITOR", null);
                if(!rs.next())
                {
                	stmt.executeUpdate("CREATE TABLE SENSORMONITOR (ID VARCHAR(40), Accelometer VARCHAR(20), AmbientLight VARCHAR(20), Barometer VARCHAR(20), Compass VARCHAR(20), Gyroscope VARCHAR(20), Magnetometer VARCHAR(20))");
                }
                
                
                ResultSet results = stmt.executeQuery("select * from SensorMonitor where id = '" + macAddress + "'");
                
                if (results.next()){
                	// update the entry
                	System.out.println("update");
                	stmt.executeUpdate("UPDATE SensorMonitor SET Accelometer = '" + accelometer + "', AmbientLight = '" + ambientLight + "', Barometer = '" + barometer + "', Compass = '" + compass + "', Gyroscope = '" + gyroscope + "', Magnetometer = '" + magnetometer + "' WHERE id = '" + macAddress + "'");
                } else{
                	// create a new entry
                	System.out.println("create");
                	stmt.execute("insert into SensorMonitor values ('" + macAddress + "', '" + accelometer + "', '" + ambientLight + "', '" + barometer + "', '" + compass + "', '" + gyroscope + "', '" + magnetometer + "')");
                }
                
                results.close();
                
                results = stmt.executeQuery("select * from SensorMonitor");
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
                    String id = results.getString(1);
                    accelometer = results.getString(2);
                    ambientLight = results.getString(3);
                    barometer = results.getString(4);
                    compass = results.getString(5);
                    gyroscope = results.getString(6);
                    magnetometer = results.getString(7);
                    System.out.println(id + "\t   " + accelometer + " \t\t\t     " + ambientLight + "  \t\t    " + barometer + "\t\t          " + compass + "\t\t   " + gyroscope + "\t\t\t    " + magnetometer);
                }
                
                
                stmt.close();
                
                con.close();        

            }
           
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	  	return Response.status(200).entity("{}").build();
    }
	
}