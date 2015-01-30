package com.tsl.positioning.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;
 
public class AppServletContextListener implements ServletContextListener{
 
	public static MatlabProxy proxy;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
		//Disconnect the proxy from MATLAB*/
	    this.proxy.disconnect();
	}
 
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("ServletContextListener started");	
		try {
			this.runMatlab();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void runMatlab() throws MatlabConnectionException, MatlabInvocationException
	{

		//Create a proxy, which we will use to control MATLAB
	    MatlabProxyFactory factory = new MatlabProxyFactory();
	    
	    MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
	    .setPort(2110)
	    .build();

	    factory = new MatlabProxyFactory(options);
	    
	    this.proxy = factory.getProxy();
	    
    
	}
	
}