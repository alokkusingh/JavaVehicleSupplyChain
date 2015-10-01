package com.abc.auto;

import java.text.SimpleDateFormat;

import com.alok.utils.AppLog;

public class Customer {
	
	String id;
	String region;
	Vehicle vehicle;
	RetailOutlet ro;
	
	public Customer(String id, String region, Vehicle vehicle, RetailOutlet ro) {
		this.id = id;
		this.region = region;
		this.vehicle = vehicle;
		this.ro = ro;
		ro.RegisterCustomer(GetID(), this);
	}

	public String GetID() {
		return id;
	}
	
	public String GetRegion() {
		return region;
	}
	
	public String GetOrderVehicleType() {
		return vehicle.GetVehicleType();
	}
	
	public void finalize() {
		AppLog.PrintTrace(1, "*****Customer(" +GetID()+"): finalize called ******");
	}
	
	public void RequestOrder() {
		AppLog.PrintTrace(1, "\t\t\t[Requested(" + GetID() + "," + GetOrderVehicleType() + ")]");
		Integer waiting = ro.RequestOrder(GetOrderVehicleType(), GetID());
		if (waiting != 0) {
			SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss a");
			AppLog.PrintTrace(1, "\t\t\t\t\t\tRequest(" + GetID() + "," + GetOrderVehicleType() + ") will be served at: " 
					+ ft.format(System.currentTimeMillis() + waiting * 1000));
		}
	}
	
	public void OrderReceived(String type) {
		AppLog.PrintTrace(1, "\t\t\t[Customer(" + GetID() + ") Order served, Type: " + type + "]");
		
	}
}
