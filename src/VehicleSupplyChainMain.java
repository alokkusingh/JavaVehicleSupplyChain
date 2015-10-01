import java.util.ArrayList;
import java.util.Iterator;
import com.abc.auto.Customer;
import com.abc.auto.RetailOutlet;
import com.abc.auto.Vehicle;
import com.abc.auto.Vendor;
import com.abc.utils.WaitForTime;
import com.alok.utils.Config;


public class VehicleSupplyChainMain {

	/**
	 * @param args
	 */
	public static void main (String args[]) {
		
		Config.CreateInstance("C:\\Alok\\workspace\\VehicleSupplyChain\\conf\\SysConfig.file"); 
		
		Vendor vendor = new Vendor();
		
		ArrayList<Vehicle> arrlstVehicle = new ArrayList<Vehicle>();
		ArrayList<String> arrlstRegion = new ArrayList<String>();
		
		arrlstVehicle.add(new Vehicle("Car"));
		arrlstVehicle.add(new Vehicle("Byke"));
		arrlstVehicle.add(new Vehicle("Truck"));
		
		arrlstRegion.add("NORTH");
		arrlstRegion.add("SIUTH");
		arrlstRegion.add("EAST");
		arrlstRegion.add("WEST");
		
		Iterator<String> itrRegion; 
		Iterator<Vehicle> itrVehicle; 
		Integer custId = 0;
		
		while (true) {
			WaitForTime.start(20);
			itrRegion = arrlstRegion.iterator();
			while(itrRegion.hasNext()) {
				itrVehicle = arrlstVehicle.iterator();
				RetailOutlet ro = vendor.GetROObject(itrRegion.next());
				while(itrVehicle.hasNext()) {
					Customer customer = new Customer(ro.GetRegion()+custId++, ro.GetRegion(), itrVehicle.next(), ro);
					customer.RequestOrder();
					WaitForTime.start(2);
				}
				WaitForTime.start(2);
			}
			vendor.GenerateReport();
		}
	}

}
