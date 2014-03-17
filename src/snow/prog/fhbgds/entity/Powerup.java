package snow.prog.fhbgds.entity;

import snow.prog.fhbgds.Snow;

public class Powerup extends FlakeBase {
	
	public static final int TYPE_NUKE = 0;
	public static final int TYPE_SLOWTIME = 1;
	public static final int TYPE_LIVES = 2;
	
	public static Powerup thePowerup;
	
	public float red;
	public float green;
	public float blue;

	public int type;
	
	public Powerup(){
		thePowerup = this;
		int i = rand.nextInt(200);
		if (i >=  25) this.type = Powerup.TYPE_NUKE;
		if (i < 25) this.type = Powerup.TYPE_SLOWTIME;
		if(i >= 100) this.type = Powerup.TYPE_LIVES;
		this.xPos = this.rand.nextInt(Snow.currentWidth - this.size);
		this.size = 10;
		if(this.type == Powerup.TYPE_NUKE){
			this.red = 0.8f;
			this.green = 0.3f;
			this.blue = 0.2f;
		}
		if(this.type == Powerup.TYPE_SLOWTIME){
			this.red = 0.3f;
			this.green = 0.8f;
			this.blue = 0.2f;
		}
		if(this.type == Powerup.TYPE_LIVES){
			this.red = 0.2f;
			this.green = 0.3f;
			this.blue = 0.8f;
		}
	}
	
	@Override
	public void onUpdate() {
		if(!this.onGround) this.yPos += rand.nextInt(2);
		if((this.yPos > Snow.currentHeight)){
			Snow.thePowerup = null;
			thePowerup = null;
		}
	}

	public int getType(){
		return this.type;
	}
}
