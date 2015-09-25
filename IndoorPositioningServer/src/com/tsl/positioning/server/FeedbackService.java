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


@Path("/feedbackService")
public class FeedbackService {

	@POST
	@Produces("application/json")
	public Response calculatePosition(
			@DefaultValue("-") @FormParam("predefinedMsg") String predefinedMsg,
			@DefaultValue("-") @FormParam("userId") String userId,
			@DefaultValue("-") @FormParam("customMsg") String customMsg) {

		System.out.println("Feedback!");
		System.out.println(predefinedMsg);
		System.out.println(customMsg);
		System.out.println(userId);

		String jsonResponse = null;

		try {
	  		
	  		String driver = "com.mysql.jdbc.Driver";
	  		Class.forName(driver);
        	
            String dbURL = "jdbc:mysql://localhost/positioning?user=panos&password=panos";
            java.sql.Connection con = DriverManager.getConnection(dbURL);
            
            if (con != null) {
            	
                System.out.println("Connected to database");
                Statement stmt = con.createStatement();
                
                stmt.execute("insert into feedback(userId , predefinedMsg, customMsg) values ('" + userId + "', '" + predefinedMsg + "', '" + customMsg + "')");
                /*ResultSet results = stmt.executeQuery("select * from passengers where nfcTag='" + nfcTag + "'");
                ResultSetMetaData rsmd = results.getMetaData();
                
                while(results.next())
                {
                    System.out.println(results.getString(1) + "\t\t" + results.getString(2) + "\t\t");
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

		// Send response to Device
		return Response.status(200).entity(jsonResponse).build();
	}
	
}
