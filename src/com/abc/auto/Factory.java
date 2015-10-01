package com.abc.auto;

import com.abc.utils.WaitForTime;
import com.alok.utils.AppLog;
import com.alok.utils.AppReport;

public class Factory {
    String strId;
    Vehicle vehicle;
    Integer intProdCapacity;
    Boolean keepProcessing;
    Integer intNumberOfProduction;
    
    Factory (String id, Vehicle vehicle, int prodCap) {
    	intNumberOfProduction = 0;
    	strId = id;
    	this.vehicle = vehicle;
    	intProdCapacity = prodCap;
    	AppLog.PrintTrace(3, "Factory: " + id + ", " + vehicle.GetVehicleType() + ", " + prodCap);
    }
    
    public void finalize() {
		AppLog.PrintTrace(1, "*****Factory(" +strId+"): finalize called ******");
	}
    
    public void GenerateReport() {
    	AppReport.WriteReport("---------------------------" + vehicle.GetVehicleType() + " Factory report ---------------------------");
    	AppReport.WriteReport(vehicle.GetVehicleType() + " Factory total prodution till now:    " + intNumberOfProduction);
    }
    
    public void StartProduction() {
    	AppLog.PrintTrace(1, vehicle.GetVehicleType() + "Production Started...");
    	keepProcessing = true;
    	while(true) {
    		WaitForTime.start((3600 / intProdCapacity) < 1 ? 1 : (3600 / intProdCapacity));
    		if (!keepProcessing) {
    			break;
    		}
    		intNumberOfProduction++;
    		Store.GetInstance().AddToStock(vehicle.GetVehicleType(), 1);
    		AppLog.PrintTrace(1, "Produced (" + vehicle.GetVehicleType() + "): 1");
    		Store.GetInstance().MinMaxStockVerification(vehicle.GetVehicleType());	
    	}
    	
    }
    
    public void StopProduction () {
    	AppLog.PrintTrace(1, vehicle.GetVehicleType() + "Production Stoped...");
    	keepProcessing = false;
    }
    
}
