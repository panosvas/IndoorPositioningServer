package com.tsl.positioning.server;

public class UserResponse {
	
	String xCoordinate;
	String yCoordinate;
	
	public void setXCoordinate(String xCoordinate){
		this.xCoordinate = xCoordinate;
	}
	
	public String getXCoordinate(){
		return this.xCoordinate;
	}
	
	public void setYCoordinate(String yCoordinate){
		this.yCoordinate = yCoordinate;
	}
	
	public String getYCoordinate(){
		return this.yCoordinate;
	}

}
