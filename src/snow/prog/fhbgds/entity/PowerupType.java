package snow.prog.fhbgds.entity;

import java.util.Random;

public enum PowerupType {
	NUKE,
	SLOWTIME,
	LIVES,
	SHRINK,
	RANDOM,
	NO_POWERUP;
	
	public static int getPowerupInt(PowerupType e) {
		switch(e) {
		case NUKE:
			return 0;
		case SLOWTIME:
			return 1;
		case LIVES:
			return 2;
		case SHRINK:
			return 3;
		case RANDOM:
			return 4;
		case NO_POWERUP:
			return -1;
		default:
			return -1;
		}
	}
	
	public static PowerupType getRandomPowerupType(Random rand) {
		switch (rand.nextInt(4)) {
		case 0:
			return NUKE;
		case 1:
			return SLOWTIME;
		case 2:
			return LIVES;
		case 3:
			return SHRINK;
		case 4:
			return RANDOM;
		default:
			return NO_POWERUP;
		}
	}
}
