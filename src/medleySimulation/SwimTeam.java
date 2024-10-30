//M. M. Kuttel 2024 mkuttel@gmail.com
//Class to represent a swim team - which has four swimmers
package medleySimulation;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger; // me
import medleySimulation.Swimmer.SwimStroke;

public class SwimTeam extends Thread {
	
	public static StadiumGrid stadium; //shared 
	private Swimmer [] swimmers;
	private int teamNo; //team number 
	public AtomicInteger swimOrder; // me - manage order of swimmers
	public CountDownLatch orange;// me
	public CountDownLatch pink; // me
	public CountDownLatch red;
	
	public static final int sizeOfTeam=4;
	
	SwimTeam( int ID, FinishCounter finish,PeopleLocation [] locArr ) {
		this.teamNo=ID;
		swimOrder = new AtomicInteger(1); // me - set to 1 for first stroke
		orange = new CountDownLatch(1); // me
		pink = new CountDownLatch(1);
		red = new CountDownLatch(1);
		swimmers= new Swimmer[sizeOfTeam];
	    SwimStroke[] strokes = SwimStroke.values();  // Get all enum constants
		stadium.returnStartingBlock(ID);

		for(int i=teamNo*sizeOfTeam,s=0;i<((teamNo+1)*sizeOfTeam); i++,s++) { //initialise swimmers in team
			locArr[i]= new PeopleLocation(i,strokes[s].getColour());
	      	int speed=(int)(Math.random() * (3)+30); //range of speeds 
			swimmers[s] = new Swimmer(i,teamNo,locArr[i],finish,speed,strokes[s], this); //hardcoded speed for now
		}
	}
	
	
	public void run() {
		try {	
			for(int s=0;s<sizeOfTeam; s++) { //start swimmer threads
				swimmers[s].start();
				
			}
			for(int s=0;s<sizeOfTeam; s++) swimmers[s].join();			//don't really need to do this;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}