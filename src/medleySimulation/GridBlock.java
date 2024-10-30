//M. M. Kuttel 2024 mkuttel@gmail.com
// GridBlock class to represent a block in the grid.
// only one thread at a time "owns" a GridBlock - this must be enforced

package medleySimulation;
import java.util.concurrent.atomic.AtomicInteger;

public class GridBlock { 
	
	private AtomicInteger isOccupied; 
	
	private final boolean isStart;  //is this a starting block?
	private int [] coords; // the coordinate of the block.
	
	GridBlock(boolean startBlock) throws InterruptedException {
		isStart=startBlock;
		isOccupied= new AtomicInteger(-1);
	}
	
	GridBlock(int x, int y, boolean startBlock) throws InterruptedException {
		this(startBlock);
		coords = new int [] {x,y};
	}
	
	public   int getX() {return coords[0];}  
	
	public   int getY() {return coords[1];}
	
	
	
	//Get a block
	public  boolean get(int threadID) throws InterruptedException {
		synchronized(this){
		if ((isOccupied.get())==threadID) return true; //thread Already in this block
		if ((isOccupied.get())>=0) return false; //space is occupied
		isOccupied= new AtomicInteger(threadID);  //set ID to thread that had block
		return true;
	}
	}
	

	
	//release a block
	public  void release() {
		synchronized(this)
		{
			isOccupied.set(-1);
			this.notifyAll();
		}
	}
	

	//is a bloc already occupied?
	public  boolean occupied() {
		synchronized(this){
		if((isOccupied.get())==-1) return false;
		return true;}
	}
	
	
	//is a start block
	public  boolean isStart() {
		return isStart;	
	}

}