package snow.prog.fhbgds.entity;

import java.util.Random;

import snow.prog.fhbgds.Snow;

public abstract class FlakeBase {

public Random rand = new Random();
	
	public float xPos;
	public float yPos;
	public int size;
	public float timeTillMelt;
	public boolean onGround = false;
	
	public void onUpdate(){
if(!this.onGround) this.yPos += rand.nextInt(2);
		
		if((this.yPos + this.size >= Snow.currentHeight)){
			this.yPos = Snow.currentHeight - this.size;
			this.onGround = true;
		}
		this.timeTillMelt--;
	}
	
}
