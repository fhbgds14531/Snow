package snow.prog.fhbgds.entity;

import org.lwjgl.opengl.Display;

public class Flake extends FlakeBase {
	
	public Flake(){
		this.size = rand.nextInt(3);
		this.size += rand.nextInt(3);
		if(this.size < 2) this.size = 2;
		this.yPos = -this.size;
		this.xPos = rand.nextInt(Display.getWidth());
		this.timeTillMelt = 3020;
	}
	
	public boolean onGround;
	
	@Override
	public void onUpdate(){
		super.onUpdate();
	}
}
