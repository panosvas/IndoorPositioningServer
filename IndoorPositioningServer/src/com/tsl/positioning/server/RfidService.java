package com.tsl.positioning.server;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/rfidService")
public class RfidService {

	@POST
	@Produces("application/json")
	public Response calculatePosition(
			@DefaultValue("-") @FormParam("room") String room,
			@DefaultValue("-") @FormParam("nfcTag") String nfcTag) {

		System.out.println("RFID!");
		System.out.println(room);
		System.out.println(nfcTag);

		String jsonResponse = null;

		try {
	  		
	  		String driver = "com.mysql.jdbc.Driver";
	  		Class.forName(driver);
        	
            String dbURL = "jdbc:mysql://localhost/positioning?user=panos&password=panos";
            java.sql.Connection con = DriverManager.getConnection(dbURL);
            
            if (con != null) {
            	
                System.out.println("Connected to database");
                //Statement stmt = con.createStatement();
                
                /*ResultSet results = stmt.executeQuery("select * from passengers where nfcTag='" + nfcTag + "'");
                ResultSetMetaData rsmd = results.getMetaData();
                
                while(results.next())
                {
                    System.out.println(results.getString(1) + "\t\t" + results.getString(2) + "\t\t");
                }*/

                // create the java mysql update preparedstatement
                String query = "update passengers set room = ? where nfcTag = ?";
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setString(1, room);
                preparedStmt.setString(2, nfcTag);
           
                // execute the java preparedstatement
                preparedStmt.executeUpdate();
                
                //stmt.close();
                
                con.close();        

            }
           
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Send response to Device
		return Response.status(200).entity(jsonResponse).build();
	}
	
}
