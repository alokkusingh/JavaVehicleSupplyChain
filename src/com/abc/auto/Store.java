package com.abc.auto;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javaEventing.EventManager;
import javaEventing.interfaces.Event;
import javaEventing.interfaces.GenericEventListener;

import com.abc.utils.event.StartBykeProductionEvent;
import com.abc.utils.event.StartCarProductionEvent;
import com.abc.utils.event.StartTruckProductionEvent;
import com.abc.utils.event.StopBykeProductionEvent;
import com.abc.utils.event.StopCarProductionEvent;
import com.abc.utils.event.StopTruckProductionEvent;
import com.alok.utils.AppLog;
import com.alok.utils.AppReport;
import com.alok.utils.Config;

public class Store {
	private static Store instance = null;
	
	Object lockVehicleStock = new Object();
	Object lockCarOrderQueue = new Object();
	Object lockBykeOrderQueue = new Object();
	Object lockTruckOrderQueue = new Object();
	
	HashMap<String, Integer> hmapVehicleStock = new HashMap<String, Integer>();
	HashMap<String, Integer> hmapMinStock = new HashMap<String, Integer>();
	HashMap<String, Integer> hmapMaxStock = new HashMap<String, Integer>();
	HashMap<String, Boolean> hmapInProcess = new HashMap<String, Boolean>();
	
	Queue<String> qCarOrderQueue = new ConcurrentLinkedQueue<String>();
	Queue<String> qBykeOrderQueue = new ConcurrentLinkedQueue<String>();
	Queue<String> qTruckOrderQueue = new ConcurrentLinkedQueue<String>();
	
	HashMap<String, RetailOutlet> hmapROContainer = new HashMap<String, RetailOutlet>();
	
	Integer intCarProdPerHour;
	Integer intBykeProdPerHour;
	Integer intTruckProdPerHour;
	
	final Factory carFactory;
	final Factory bykeFactory;
	final Factory truckFactory;
	
	public Store() {
		intCarProdPerHour = new Integer(Config.GetInstance().GetAppConfig("FACTORY", "CarProdPerHour"));
		intBykeProdPerHour = new Integer(Config.GetInstance().GetAppConfig("FACTORY", "BykeProdPerHour"));
		intTruckProdPerHour = new Integer(Config.GetInstance().GetAppConfig("FACTORY", "TruckProdPerHour"));
		
		hmapVehicleStock.put("Car", 0);
		hmapVehicleStock.put("Byke", 0);
		hmapVehicleStock.put("Truck", 0);
		
		hmapMinStock.put("Car", 6);
		hmapMinStock.put("Byke", 10);
		hmapMinStock.put("Truck", 1);
		
		hmapMaxStock.put("Car", 20);
		hmapMaxStock.put("Byke", 30);
		hmapMaxStock.put("Truck", 3);
		
		hmapInProcess.put("Car", false);
		hmapInProcess.put("Byke", false);
		hmapInProcess.put("Truck", false);
		
		carFactory = new Factory("FAC1", new Vehicle("Car"), intCarProdPerHour);
		bykeFactory = new Factory("FAC2", new Vehicle("Byke"), intBykeProdPerHour);
		truckFactory = new Factory("FAC3", new Vehicle("Truck"), intTruckProdPerHour);
		
		Class<? extends Event> startCarProdEvent = (new StartCarProductionEvent()).getClass();
    	EventManager.registerEventListener(new GenericEventListener(){      
    		public void eventTriggered(Object sender, Event event) {
    			AppLog.PrintTrace(2, "StartCarProductionEvent Event Triggered");
    			carFactory.StartProduction();
    		}
    	}, startCarProdEvent);
    	
		Class<? extends Event> startBykeProdEvent = (new StartBykeProductionEvent()).getClass();
    	EventManager.registerEventListener(new GenericEventListener(){      
    		public void eventTriggered(Object sender, Event event) {
    			AppLog.PrintTrace(2, "StartBykeProductionEvent Event Triggered");
    			bykeFactory.StartProduction();
    		}
    	}, startBykeProdEvent);
    	
		Class<? extends Event> startTruckProdEvent = (new StartTruckProductionEvent()).getClass();
    	EventManager.registerEventListener(new GenericEventListener(){      
    		public void eventTriggered(Object sender, Event event) {
    			AppLog.PrintTrace(2, "StartTruckProductionEvent Event Triggered");
    			truckFactory.StartProduction();
    		}
    	}, startTruckProdEvent);
    	
		Class<? extends Event> stopCarProdEvent = (new StopCarProductionEvent()).getClass();
    	EventManager.registerEventListener(new GenericEventListener(){      
    		public void eventTriggered(Object sender, Event event) { 
    			AppLog.PrintTrace(2, "StopCarProductionEvent Event Triggered");
    			carFactory.StopProduction();
    		}
    	}, stopCarProdEvent);
    	
		Class<? extends Event> stopBykeProdEvent = (new StopBykeProductionEvent()).getClass();
    	EventManager.registerEventListener(new GenericEventListener(){      
    		public void eventTriggered(Object sender, Event event) {
    			AppLog.PrintTrace(2, "StopBykeProductionEvent Event Triggered");
    			bykeFactory.StopProduction();
    		}
    	}, stopBykeProdEvent);
    	
		Class<? extends Event> stopTruckProdEvent = (new StopTruckProductionEvent()).getClass();
    	EventManager.registerEventListener(new GenericEventListener(){      
    		public void eventTriggered(Object sender, Event event) { 
    			AppLog.PrintTrace(2, "StopTruckProductionEvent Event Triggered");
    			truckFactory.StopProduction();
    		}
    	}, stopTruckProdEvent);
    	
    	MinMaxStockVerification("Car");
		MinMaxStockVerification("Byke");
		MinMaxStockVerification("Truck");
	}
	
	public static Store GetInstance() {
	    if(instance == null) {
	       instance = new Store();
	    } 	    
	    return instance;
	}
	
    public void finalize() {
		AppLog.PrintTrace(1, "*****Store: finalize called ******");
	}
    
	public void GenerateReport() {
		AppReport.WriteReport("--------------------------------- Store report ---------------------------------");
    	AppReport.WriteReport("Store in stock:                                   "  + hmapVehicleStock.toString());
    	carFactory.GenerateReport();
    	bykeFactory.GenerateReport();
    	truckFactory.GenerateReport();
    }
	
	public void OrderProduction(String type) {
		AppLog.PrintTrace(3, "Going to trigger START" + type + "Production Event");
		if (type == "Car") {
		    EventManager.triggerEvent(this, new StartCarProductionEvent());
		} else if(type == "Truck") {
		    EventManager.triggerEvent(this, new StartTruckProductionEvent());
		} else {
			EventManager.triggerEvent(this, new StartBykeProductionEvent());
		}
	}

	public void StopProduction(String type) {
		AppLog.PrintTrace(3, "Going to trigger STOP " + type + "Production Event");
		if (type == "Car") {
		    EventManager.triggerEvent(this, new StopCarProductionEvent());
		} else if(type == "Truck") {
		    EventManager.triggerEvent(this, new StopTruckProductionEvent());
		} else {
			EventManager.triggerEvent(this, new StopBykeProductionEvent());
		}
	}
	
	public void AddToStock(String type, Integer qty) {
		AppLog.PrintTrace(3, "AddToStock: " + type + ", " + qty);
		synchronized(lockVehicleStock) {
		    hmapVehicleStock.put(type, hmapVehicleStock.get(type) + qty);
		    AppLog.PrintTrace(2, "\t\t\tIn store: " + hmapVehicleStock.toString());
		}
		SupplyOrder(type);
	}
	
	public void RemoveFromStock(String type, Integer qty) {
		synchronized(lockVehicleStock) {
		    hmapVehicleStock.put(type, hmapVehicleStock.get(type) - qty);
		    AppLog.PrintTrace(2, "\t\t\tIn store: " + hmapVehicleStock.toString());
		}
	}

	public Integer CheckStock(String type) {
		synchronized(lockVehicleStock) {
		    return hmapVehicleStock.get(type);
		}
	}
	
    private Integer GetMinStock(String type) {
        return 	hmapMinStock.get(type);
    }
 
    private Integer GetMaxStock(String type) {
        return 	hmapMaxStock.get(type);
    }
    
    public void MinMaxStockVerification (String type) {
    	AppLog.PrintTrace(3, "MinMaxStockVerification called: " + type + ", " + GetMinStock(type) + ", " + GetMaxStock(type) + ", " + CheckStock(type));
    	if (CheckStock(type) <= GetMinStock(type)) {
    		AppLog.PrintTrace(3, "MinMaxStockVerification start processing: " + type);
    		if (hmapInProcess.get(type) == false) {
    			AppLog.PrintTrace(3, "MinMaxStockVerification started processing: " + type);
    			hmapInProcess.put(type, true);
    		    OrderProduction(type);
    		} else {
    			AppLog.PrintTrace(3, "MinMaxStockVerification already in process: " + type);
    		}
    	} else if (CheckStock(type) >= GetMaxStock(type)) {
    		AppLog.PrintTrace(3, "MinMaxStockVerification stop processing: " + type);
    		
    		hmapInProcess.put(type, false);
    		StopProduction(type);		
    	}
    }
    
    public void RegisterRO(String id, RetailOutlet ro) {
    	hmapROContainer.put(id, ro);
    }
    
    private RetailOutlet GetROObject(String id) {
    	return hmapROContainer.get(id);
    }
    
    private Integer CalculateMaxWaitingPeriod(String type) {
    	//returns max waiting period in second
    	if (type == "Car") {
				return qCarOrderQueue.size() * 3600 / this.intCarProdPerHour;
		} else if(type == "Truck") {
				return qTruckOrderQueue.size() * 3600 / this.intTruckProdPerHour;
		} else {
				return qBykeOrderQueue.size() * 3600 / this.intBykeProdPerHour;
		}
    }
    
    public Integer ReceiveOrder(String type, String roID) {
    	AddOrderToQueue(type, roID);
    	AppLog.PrintTrace(3, "Max waiting for (" + roID + "," + type + "): " + CalculateMaxWaitingPeriod(type) + " seconds");
    	SupplyOrder(type);
    	return CalculateMaxWaitingPeriod(type);
    }
 
    private synchronized void SupplyOrder(String type) {
    	//this will be called once the product added to the store or received a request
    	if (CheckStock(type) != 0) {
    		String roID = GetOrderFromQueue(type);
    	   	if (roID != null) { 
	    		AppLog.PrintTrace(2, "Supplied order for RO: " + roID + " and type: " + type);
	    		//Call a particular dealer to process for the given vehicle type
	    		//call RO.DeliverOrder(type);
	    		RemoveFromStock(type, 1);
	    		GetROObject(roID).DeliverOrder(type);
	    		MinMaxStockVerification(type);
    	   	}
    	}
    }
    
    private void AddOrderToQueue(String type, String roID) {
    	AppLog.PrintTrace(2, "Store:Putting order(" + roID + ") in " + type + " queue");
		if (type == "Car") {
			synchronized(lockCarOrderQueue) {
				qCarOrderQueue.add(roID);
			}
		} else if(type == "Truck") {
			synchronized(lockTruckOrderQueue) {
				qTruckOrderQueue.add(roID);
			}
		} else {
			synchronized(lockBykeOrderQueue) {
				qBykeOrderQueue.add(roID);
			}
		}
    }
    
    private String GetOrderFromQueue(String type) {
		AppLog.PrintTrace(2, "Rertriving order from " + type + " queue");
		AppLog.PrintTrace(2, "\t\tCar Order queue(" + qCarOrderQueue.size() + "): " + qCarOrderQueue.toString());
		AppLog.PrintTrace(2, "\t\tTruck Order queue(" + qTruckOrderQueue.size() + "): " + qTruckOrderQueue.toString());
		AppLog.PrintTrace(2, "\t\tByke Order queue(" + qBykeOrderQueue.size() + "): " + qBykeOrderQueue.toString());
		
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
}
