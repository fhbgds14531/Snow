package snow.prog.fhbgds.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import snow.prog.fhbgds.MathHelp;
import snow.prog.fhbgds.Snow;

public class Player extends BaseClass{

	public Player(){
		this.size = 20;
		this.xPos = (Snow.currentWidth/2) - this.size/2;
		this.yPos = Snow.currentHeight - this.size;
	}

	@Override
	public void onUpdate() {
		if(this.xPos != -1000 && this.yPos != -1000){
			if(this.xPos < 0) this.xPos = 780;
			if(this.yPos < 0){
				this.yPos = 0;
				Snow.instance.advanceLevel();
			}
			if(this.xPos > 780) this.xPos = 0;
			if(this.yPos > 580){
				Snow.instance.decrementLevel();
			}
		}
		
		Iterator<Entry<Float[], Flake>> it = Snow.flakes.entrySet().iterator();
		boolean flag = false;
		while(it.hasNext()){
			Entry<Float[], Flake> entry = it.next();
			if(entry.getValue().xPos >= this.xPos && entry.getValue().xPos <= this.xPos + this.size){
				if(entry.getValue().yPos >= this.yPos && entry.getValue().yPos <= this.yPos + this.size){
					flag = true;
				}
			}
		}
		if(flag && !Snow.doDevStuff){
			Snow.instance.handleDeath();
		}
		boolean flag1 = false;
		Powerup powerup = Snow.thePowerup;
		if(powerup != null){
			if(MathHelp.isBetween(powerup.xPos, this.xPos, this.xPos + this.size)){
				if(MathHelp.isBetween(powerup.yPos, this.yPos, this.yPos + this.size)){
					flag1 = true;
				}
			}
			if(MathHelp.isBetween(powerup.xPos + powerup.size, this.xPos, this.xPos + this.size)){
				if(MathHelp.isBetween(powerup.yPos + powerup.size, this.yPos, this.yPos + this.size)){
					flag = true;
				}
			}
			if(MathHelp.isBetween(this.xPos, powerup.xPos, powerup.xPos + powerup.size)){
				if(MathHelp.isBetween(this.yPos, powerup.yPos, powerup.yPos + powerup.size)){
					flag1 = true;
				}
			}
			if(MathHelp.isBetween(powerup.xPos, this.xPos, this.xPos + this.size)){
				if(MathHelp.isBetween(powerup.yPos + powerup.size, this.yPos, this.yPos + this.size)){
					flag = true;
				}
			}
			if(flag1){
				float pitch = rand.nextFloat();
				pitch *= rand.nextFloat();
				if(pitch > 1) pitch = 1;
				if(pitch < 0.5) pitch = 0.5f;
				
				pitch = 0.75f;
				
				int type = powerup.getType();
				if(type == Powerup.TYPE_NUKE){
					Snow.flakes = new HashMap<Float[], Flake>();
					Snow.thePowerup = null;
					Snow.al.playSound("powerup", pitch, 0.8f, false);
				}
				if(type == Powerup.TYPE_SLOWTIME){
					Snow.instance.runSlow();
					Snow.thePowerup = null;
					Snow.al.playSound("powerup", pitch, 0.8f, false);
				}
				if(type == Powerup.TYPE_LIVES){
					Snow.lives += 3;
					Snow.instance.updateTitle();
					Snow.thePowerup = null;
					Snow.al.playSound("powerup", pitch, 0.8f, false);
				}
			}
		}
	}

	@Override
	public Player setPos(int x, int y) {
		this.xPos = x;
		this.yPos = y;
		return this;
	}
}
