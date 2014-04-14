package snow.prog.fhbgds.entity;

import snow.prog.fhbgds.Snow;

public class Powerup extends FlakeBase {
	
	public float red;
	public float green;
	public float blue;

	public PowerupType type;
	
	public Powerup(){
		int i = rand.nextInt(225);
		if (i >=  25) this.type = PowerupType.NUKE;
		if (i < 25) this.type = PowerupType.SLOWTIME;
		if(i >= 100) this.type = PowerupType.LIVES;
		if(i >= 150) this.type = PowerupType.SHRINK;
		if(i >= 200) this.type = PowerupType.RANDOM;
		this.xPos = this.rand.nextInt(Snow.game.currentWidth - this.size);
		this.size = 10;
		if(this.type == PowerupType.NUKE){
			this.red = 0.8f;
			this.green = 0.3f;
			this.blue = 0.2f;
		}
		if(this.type == PowerupType.SLOWTIME){
			this.red = 0.3f;
			this.green = 0.8f;
			this.blue = 0.2f;
		}
		if(this.type == PowerupType.LIVES){
			this.red = 0.2f;
			this.green = 0.3f;
			this.blue = 0.8f;
		}
		if(this.type == PowerupType.SHRINK){
			this.red = 0.8f;
			this.green = 0.8f;
			this.blue = 0.2f;
		}
	}
	
	@Override
	public void onUpdate() {
		if(!this.onGround) this.yPos += rand.nextInt(2);
		if((this.yPos > Snow.game.currentHeight)){
			Snow.game.thePowerup = null;
		}
		if(this.type == PowerupType.RANDOM){
			this.red = (this.rand.nextFloat() * 2);
			if(this.red > 1) this.red = this.red/2;
			this.green = (this.rand.nextFloat() * 2);
			if(this.green > 1) this.green = this.green/2;
			this.blue = (this.rand.nextFloat() * 2);
			if(this.blue > 1) this.blue = this.blue/2;
		}
	}

	public PowerupType getType(){
		return this.type;
	}
}
