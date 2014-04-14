package snow.prog.fhbgds.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import snow.prog.fhbgds.Snow;

public class Player extends BaseClass{

	public Player(){
		this.size = 20;
		this.xPos = (Snow.game.currentWidth/2) - this.size/2;
		this.yPos = Snow.game.currentHeight - this.size;
	}

	@Override
	public void onUpdate() {
		if(this.xPos != -1000 && this.yPos != -1000){
			if(this.xPos < 0) this.xPos = Snow.game.currentWidth - this.size;
			if(this.yPos < 0){
				this.yPos = 0;
				Snow.game.advanceLevel();
			}
			if(this.xPos > Snow.game.currentWidth - this.size) this.xPos = 0;
			if(this.yPos > Snow.game.currentHeight - this.size && Snow.game.ticksSinceDeath > 1000){
				Snow.game.decrementLevel();
			}else if(this.yPos > Snow.game.currentHeight - this.size && Snow.game.ticksSinceDeath <= 1500){
				this.yPos = 580;
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
			Snow.game.handleDeath();
		}
		boolean flag1 = false;
		Powerup powerup = Snow.game.thePowerup;
		if(powerup != null){
			float powerupX1 = powerup.xPos;
			float powerupX2 = powerup.xPos + powerup.size;
			float powerupY1 = powerup.yPos;
			float powerupY2 = powerup.yPos + powerup.size;
			float playerX1 = this.xPos;
			float playerX2 = this.xPos + this.size;
			float playerY1 = this.yPos;
			float playerY2 = this.yPos + this.size;
		
			if(powerupX1 >= playerX1 && powerupY1 >= playerY1 && powerupX1 <= playerX2 && powerupY1 <= playerY2){
				flag1 = true;
			}
			if(powerupX2 >= playerX1 && powerupY1 >= playerY1 && powerupX2 <= playerX2 && powerupY1 <= playerY2){
				flag = true;
			}
			if(powerupX2 >= playerX1 && powerupY2 >= playerY1 && powerupX2 <= playerX2 && powerupY2 <= playerY2){
				flag1 = true;
			}
			if(powerupX1 >= playerX1 && powerupY2 >= playerY1 && powerupX1 <= playerX2 && powerupY2 <= playerY2){
				flag1 = true;
			}
			if(flag1){
				PowerupType type = powerup.getType();
				this.handlePowerup(type);
			}
		}
	}
	
	private void handlePowerup(PowerupType type){
		float pitch = (rand.nextFloat() * (rand.nextFloat() + 1));
		if(pitch >  0.9f) pitch =  0.9f;
		if(pitch < 0.75f) pitch = 0.75f;
		
		switch (type){
		case NUKE:
			Snow.flakes = new HashMap<Float[], Flake>();
			Snow.game.thePowerup = null;
			break;
		case SLOWTIME:
			Snow.game.runSlow();
			Snow.game.thePowerup = null;
			break;
		case LIVES:
			Snow.lives += 3;
			Snow.game.updateTitle();
			Snow.game.thePowerup = null;
			break;
		case SHRINK:
			if(!Snow.game.doShrink){
				this.size = 15;
				this.xPos += 5;
				this.yPos += 5;
				Snow.game.doShrink = true;
			}
			Snow.game.thePowerup = null;
			Snow.game.smallCount = 2048;
			break;
		case RANDOM:
			this.handlePowerup(PowerupType.getRandomPowerupType(rand));
		case NO_POWERUP:
			Snow.game.thePowerup = null;
			return;
		}
		if(!Snow.game.isMuted) Snow.al.playSound("powerup", pitch, 0.8f, false);
	}

	@Override
	public Player setPos(int x, int y) {
		this.xPos = x;
		this.yPos = y;
		return this;
	}
}
