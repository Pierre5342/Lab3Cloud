package client;

public class GetData {
	
	
	public static main (String args []) {
		
		while (isEmpty(OutboxQueue)) {
			pause(1);
		}
		
	}
	
	
	
	public static void pause(int tps_en_min) throws InterruptedException {
		  Thread.sleep(1000*tps_en_min);
	  }

}
