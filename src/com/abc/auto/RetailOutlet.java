package com.abc.auto;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.alok.utils.AppLog;
import com.alok.utils.AppReport;

public class RetailOutlet {
	
	String id;
	String region;
	Integer intCarOrderReceived;
	Integer intCarOrderServed;
	Integer intBykeOrderReceived;
	Integer intBykeOrderServed;
	Integer intTruckOrderReceived;
	Integer intTruckOrderServed;
	
	HashMap<String, Customer> hmapCustomerContainer = new HashMap<String, Customer>();
	
	Object lockCarOrderQueue = new Object();
	Object lockBykeOrderQueue = new Object();
	Object lockTruckOrderQueue = new Object();
	
	Queue<String> qCarOrderQueue = new ConcurrentLinkedQueue<String>();
	Queue<String> qBykeOrderQueue = new ConcurrentLinkedQueue<String>();
	Queue<String> qTruckOrderQueue = new ConcurrentLinkedQueue<String>();
	
	RetailOutlet (String id, String region) {
		intCarOrderReceived = 0;
		intCarOrderServed = 0;
		intBykeOrderReceived = 0;
		intBykeOrderServed = 0;
		intTruckOrderReceived = 0;
		intTruckOrderServed = 0;
		this.id = id;
		this.region = region;
		Store.GetInstance().RegisterRO(id, this);
	}

	private String GetID() {
		return id;
	}
	
	public String GetRegion() {
		return region;
	}
	
	public void GenerateReport() {
		AppReport.WriteReport("--------------------------- RO(" + GetID() + ") report ---------------------------");
    	AppReport.WriteReport("RO (" + GetID() + ") total received Order till now: " + "[Car: " + intCarOrderReceived + 
    			", Byke: " + intBykeOrderReceived + ",Truck: " + intTruckOrderReceived + "]=" + (intCarOrderReceived + intBykeOrderReceived + intTruckOrderReceived));
    	AppReport.WriteReport("RO (" + GetID() + ") total served Order till now:   " + "[Car: " + intCarOrderServed + 
    			", Byke: " + intBykeOrderServed + ",Truck: " + intTruckOrderServed + "]=" + (intCarOrderServed + intBykeOrderServed + intTruckOrderServed));
    	AppReport.WriteReport("RO (" + GetID() + ") Car order queue:               " + qCarOrderQueue.toString());
    	AppReport.WriteReport("RO (" + GetID() + ") Byke order queue:              " + qBykeOrderQueue.toString());
    	AppReport.WriteReport("RO (" + GetID() + ") Truck order queue:             " + qTruckOrderQueue.toString());
    }
	
    public void finalize() {
		AppLog.PrintTrace(1, "*****RetailOutlet(" +GetID()+"): finalize called ******");
	}
    
    public void RegisterCustomer(String id, Customer customer) {
    	hmapCustomerContainer.put(id, customer);
    }
    
    private void UnRegisterCustomer(String id) {
    	hmapCustomerContainer.remove(id);
    }
    
    private Customer GetCustomerObject(String id) {
    	return hmapCustomerContainer.get(id);
    }
    
    private void AddOrderToQueue(String type, String custID) {
    	AppLog.PrintTrace(3, "RO(" + GetID() + "):Putting order in " + type + " queue");
		if (type == "Car") {
			synchronized(lockCarOrderQueue) {
				qCarOrderQueue.add(custID);
			}
		} else if(type == "Truck") {
			synchronized(lockTruckOrderQueue) {
				qTruckOrderQueue.add(custID);
			}
		} else {
			synchronized(lockBykeOrderQueue) {
				qBykeOrderQueue.add(custID);
			}
		}
    }
    
    private String GetOrderFromQueue(String type) {
		AppLog.PrintTrace(2, "RO(" + GetID() + "):Rertriving order from " + type + " queue");
		AppLog.PrintTrace(2, "\t\tRO(" + GetID() + "):Car Order queue(" + qCarOrderQueue.size() + "): " + qCarOrderQueue.toString());
		AppLog.PrintTrace(2, "\t\tRO(" + GetID() + "):Truck Order queue(" + qTruckOrderQueue.size() + "): " + qTruckOrderQueue.toString());
		AppLog.PrintTrace(2, "\t\tRO(" + GetID() + "):Byke Order queue(" + qBykeOrderQueue.size() + "): " + qBykeOrderQueue.toString());
		
		if (type == "Car") {			
			synchronized(lockCarOrderQueue) {
				return qCarOrderQueue.poll();
			}
		} else if(type == "Truck") {
			synchronized(lockTruckOrderQueue) {
				return qTruckOrderQueue.poll();
			}
		} else {
			synchronized(lockBykeOrderQueue) {
				return qBykeOrderQueue.poll();
			}
		}
	}
    
	public Integer RequestOrder(String type, String custID) {
		if (type == "Car") {
			intCarOrderReceived++;
		} else if (type == "Byke") {
			intBykeOrderReceived++;
		} else {
			intTruckOrderReceived++;
		}
		
		AddOrderToQueue(type, custID);
		return Store.GetInstance().ReceiveOrder(type, GetID());
	}
	
	public void DeliverOrder(String type) {
		String custID = GetOrderFromQueue(type);
		if (custID != null) { 
    		GetCustomerObject(custID).OrderReceived(type);
    		UnRegisterCustomer(custID);
    		
    		if (type == "Car") {
    			intCarOrderServed++;
    		} else if (type == "Byke") {
    			intBykeOrderServed++;
    		} else {
    			intTruckOrderServed++;
    		}
	   	}
	}
}
