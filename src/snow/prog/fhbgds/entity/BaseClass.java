package snow.prog.fhbgds.entity;

import java.util.Random;

public abstract class BaseClass {

	public Random rand = new Random();
	public float xPos;
	public float yPos;
	public int size;
	
	public abstract void onUpdate();
	
	public abstract BaseClass setPos(int x, int y); 
}
