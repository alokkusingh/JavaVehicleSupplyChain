package com.abc.auto;
import java.util.HashMap;
public class Vendor {
	
	HashMap<String, RetailOutlet> hmapRO = new HashMap<String, RetailOutlet>();
	
	public Vendor() { 
		hmapRO.put("NORTH", new RetailOutlet("RO001", "NORTH") );
		hmapRO.put("SOUTH", new RetailOutlet("RO002", "SOUTH"));
		hmapRO.put("EAST", new RetailOutlet("RO003", "EAST"));
		hmapRO.put("WEST", new RetailOutlet("RO004", "WEST"));
	}
	
	public RetailOutlet GetROObject(String Region) 
	{
		return (hmapRO.get(Region));
	}
	
	public void GenerateReport() {
		GetROObject("NORTH").GenerateReport();
		GetROObject("SOUTH").GenerateReport();
		GetROObject("EAST").GenerateReport();
		GetROObject("WEST").GenerateReport();
		Store.GetInstance().GenerateReport();
	}
}