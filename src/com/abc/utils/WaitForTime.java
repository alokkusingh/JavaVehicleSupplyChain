package com.abc.utils;

public class WaitForTime {
	public static void start(int periodInSec)
    {
        //System.out.println("Wait......" + periodInSec + " secs");
   
        // pause for a while
        //Thread thisThread = Thread.currentThread();
	    try
	    {
	        Thread.sleep(1000 * periodInSec);
	    }
	    catch (Throwable t)
	    {
	       
	    }
        //System.out.println("Ending......");
    }
}
