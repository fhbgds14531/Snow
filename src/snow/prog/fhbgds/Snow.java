package snow.prog.fhbgds;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import snow.prog.fhbgds.entity.BaseClass;
import snow.prog.fhbgds.entity.Flake;
import snow.prog.fhbgds.entity.Player;
import snow.prog.fhbgds.entity.Powerup;

public class Snow {

	private static Float version = 0.464f;

	public static Random rand = new Random();

	static File save;
	static File lDeaths;
	static File positions;
	
	static IO io = new IO();

	public int flakesInAir = 0;

	public static float red = 1.0f;
	public static float green = 1.0f;
	public static float blue = 1.0f;

	public static int levelNum = 0;
	public static int maxLevel = 255;
	public static Integer[] levelDeaths = new Integer[maxLevel + 1];
	public static int deaths = 0;
	public static int totalDeaths = 0;
	public static int baseLives = 20;
	public static int lives = 20;
	public static int frequency = 27;
	public static int baseFreq = 27;

	public static volatile boolean isCloseRequested = false;
	public static volatile boolean isPaused = false;
	public static volatile boolean shouldRenderPaused;
	public static boolean doAutoHandicap;
	public static Snow theSnow;
	public static HashMap<Float[], Flake> flakes = new HashMap<Float[], Flake>();

	public static BaseClass thePlayer;

	public static int currentHeight;
	public static int currentWidth;

	DisplayMode defaultMode = new DisplayMode(800, 600);
	DisplayMode fullscreen = Display.getDesktopDisplayMode();

	Cursor emptyCursor;
	Cursor nativeCursor;

	public static String title = "Snow Avoider! Version: " + version;
	private int fps;
	private long lastFPS;
	private static boolean doFPS;
	private static float handicap;
	private static long lastFrame;
	private static boolean loadedLDeaths;
	public static boolean needsUpdate;
	public static Powerup thePowerup;
	private static boolean runSlow;
	private static int slowCount = 0;

	public static boolean doDevStuff;

	private static Integer levelLives;

	public Snow() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setTitle("Loading...");
			Display.create();
			Mouse.create();

			nativeCursor = Mouse.getNativeCursor();
			emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);

			currentWidth = Display.getDisplayMode().getWidth();
			currentHeight = Display.getDisplayMode().getHeight();
			getReadyFor2DDrawing();
			thePlayer = new Player();
			theSnow = this;
			runSim();
		} catch (LWJGLException e) {
			e.printStackTrace();
			Display.destroy();
			Mouse.destroy();
			System.exit(1);
		}
	}

	private void runSim() {
		if(!loadedLDeaths){
			int i = 0;
			while (i <= maxLevel){
				levelDeaths[i] = 0;
				i++;
			}
		}
		int count = 0;
		int colorCount = 0;
		timeSinceLastFrame();
		lastFPS = getTime();
		lives = baseLives + (int) Math.round(levelNum * 0.25); 
		Display.setTitle(title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
		isPaused = true;
		shouldRenderPaused = true;
		while(!Display.isCloseRequested() && !isCloseRequested){
			updateDebugInfo();
			handicap = deaths * 0.1f;
			frequency = (int) ((baseFreq - (levelNum * 0.3f)));
			if(frequency <= 3) frequency = 3;
			if(handicap > levelNum * 0.01f){
				handicap -= levelNum * 0.01f;
			}else{
				if(handicap - (levelNum*0.01f) < 0){
					handicap = 0.0f;
				}
			}
			String round = String.valueOf(handicap);
			round = round.substring(0, 3);
			handicap = Float.valueOf(round);
			if(handicap > 3.0f) handicap = 3.0f;
			if(doAutoHandicap) frequency += handicap;
			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_F11) {
						try{
							if(!Display.isFullscreen()){
								Mouse.setNativeCursor(emptyCursor);
							}else{
								Mouse.setNativeCursor(nativeCursor);
							}
							Util.setDisplayMode(800, 600, !Display.isFullscreen());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					if (Keyboard.getEventKeyState()) {
						if (Keyboard.getEventKey() == Keyboard.KEY_F3) {
							doFPS = !doFPS;
							if(levelNum < 101){
								Display.setTitle(title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
								if(isPaused){
									Display.setTitle("[PAUSED] " + title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
								}
							}else{
								Display.setTitle(title + "100");
								if(isPaused){
									Display.setTitle("[PAUSED] " + title + " Level: 100" + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
								}
							}
						}
					}
					if(Keyboard.getEventKeyState()){
						if(Keyboard.getEventKey() == Keyboard.KEY_SPACE){
							changeColors();
						}
					}
					if((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) && Keyboard.isKeyDown(Keyboard.KEY_R)){
						flakes = new HashMap<Float[], Flake>();
						thePlayer = new Player();
						levelNum = 0;
						lives = baseLives;
						isPaused = true;
						shouldRenderPaused = true;
						deaths = 0;
						red = 1.0f;
						green = 1.0f;
						blue = 1.0f;
						GL11.glColor3f(red, green, blue);
						Display.setTitle(title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
					}
					if (Keyboard.getEventKeyState()) {
						if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
							if(isPaused){
								GL11.glColor3f(red, green, blue);
								Display.setTitle(Display.getTitle().substring(Display.getTitle().indexOf("Snow")));
							}else{
								Display.setTitle("[PAUSED] " + Display.getTitle());
							}
							isPaused = !isPaused;
							shouldRenderPaused = !shouldRenderPaused;
						}
					}
					if (Keyboard.getEventKeyState()) {
						if (Keyboard.getEventKey() == Keyboard.KEY_A) {
							doAutoHandicap = !doAutoHandicap; 
						}
					}
//==========================================================================================
					if (Keyboard.getEventKeyState()) {
						if (Keyboard.getEventKey() == Keyboard.KEY_L && doDevStuff) {
							advanceLevel();
							if(shouldRenderPaused){
								isPaused = true;
							}
						}
					}
					if (Keyboard.getEventKeyState()) {
						if (Keyboard.getEventKey() == Keyboard.KEY_K && doDevStuff) {
							handleDeath();
						}
					}
					if (Keyboard.getEventKeyState()) {
						if (Keyboard.getEventKey() == Keyboard.KEY_P && doDevStuff) {
							thePowerup = new Powerup();
						}
					}
//==========================================================================================
				}
			}
			if(Mouse.isButtonDown(0)){
				int pauseXLoc = (int) (currentWidth/2.35);
				int pauseYLoc = (int) (currentHeight/2.5);
				if(Mouse.getX() >= pauseXLoc && (Mouse.getY() >= pauseYLoc) && (Mouse.getX() <= pauseXLoc + currentHeight/5) && (Mouse.getY() <= pauseYLoc + currentHeight/5)){
					isPaused = false;
					shouldRenderPaused = false;
					Display.setTitle(title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
					GL11.glColor3f(red, green, blue);
				}
			}
			if(!isPaused){
				if(levelNum == maxLevel && colorCount == 10 && !doFPS){
					changeColors();
					colorCount = 0;
				}

				thePlayer.onUpdate();

				if(Display.isCloseRequested()){
					save();
					isCloseRequested = true;
					Display.destroy();
					Mouse.destroy();
					System.exit(0);
				}
				
				if((runSlow && count == 5) || !runSlow){
					int newValues = update(count);
					count = newValues;
				}
			}
			if(!isPaused){
				if(Keyboard.isKeyDown(Keyboard.KEY_UP)) thePlayer.yPos--;
				if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) thePlayer.yPos++;
				if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) thePlayer.xPos--;
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) thePlayer.xPos++;
				if(runSlow && slowCount <= 255){
					slowCount++;
					if(slowCount >= 511){
						slowCount = 0;
						runSlow = false;
					}
				}
			}
			doRenderTick();
			Display.update();
			Display.sync(240);
		}
	}

	public static int update(int count){
		if(levelNum != 1234567890){
			if(count >= frequency){
				Flake flake = new Flake();
				flakes.put(new Float[] {flake.xPos, flake.yPos}, flake);
				if(rand.nextInt(4096) < levelNum + 1){
					if(thePowerup == null){
						thePowerup = new Powerup();
					}
				}
				count = 0;
			}
			updateFlakes();
			if(thePowerup != null) thePowerup.onSnowTick();
		}
		count++;
		return count;
	}
	
	public static int timeSinceLastFrame() {
	    long time = getTime();
	    int diff = (int) (time - lastFrame);
	    lastFrame = time;
	    return diff;
	}

	private void getReadyFor2DDrawing() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 600, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);	
	}

	public static void updateFlakes(){
		Float[][] removalList = new Float[2][128];
		int count = 0;
		Iterator<Entry<Float[], Flake>> it = flakes.entrySet().iterator();
		while(it.hasNext()){
			Entry<Float[], Flake> entry = it.next();
			entry.getValue().onSnowTick();
			if(entry.getValue().timeTillMelt <= 0 && entry.getValue().rand.nextFloat() < 0.5f){
				if(entry.getValue().size >= 1){
					entry.getValue().size--;
					entry.getValue().timeTillMelt = 300;
					entry.getValue().onGround = false;
				}else{
					removalList[count] = entry.getKey();
					if(count < removalList.length - 1){
						count++;
					}
				}
			}
		}
		for(int i = 0; i < removalList.length; i++){
			if(removalList[i][0] != null && removalList[i][1] != null){
				flakes.remove(removalList[i]);
			}
		}
	}

	public static void doRenderTick(){
		Render.clearDisplay();
		GL11.glColor3f(0.1f, 0.1f, 0.1f);
		Render.drawSquare(0, 0, currentWidth, currentHeight);
		Render.drawFlakes();
		Render.drawSquare(thePlayer.xPos, thePlayer.yPos, thePlayer.size, thePlayer.size);
		if(thePowerup != null){
			Render.drawPowerup(thePowerup);
		}
		if(shouldRenderPaused){
			GL11.glColor3f(0.4f, 0.4f, 0.4f);
			Render.drawPaused((int) (currentWidth/2.35), (int) (currentHeight/2.5), currentHeight);
		}
	}

	public static long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
 
	public void updateDebugInfo() {
		if (getTime() - lastFPS > 1000) {
			if(doFPS){
				String currTitle = Display.getTitle();
				String newTitle = "";

				if(currTitle.contains(" -FPS: ")){
					int index = currTitle.indexOf(" -");
					newTitle = currTitle.substring(0, index);
				}else{
					newTitle = currTitle;
				}
				if(!newTitle.contentEquals("")){
					if(doAutoHandicap){
						Display.setTitle(newTitle + " -FPS: " + fps + "- D: " + (baseFreq - frequency) + " LD: " + deaths + " Handicap: " + handicap);
					}else{
						Display.setTitle(newTitle + " -FPS: " + fps + "- Difficulty: " + (baseFreq - frequency) + " Level Deaths: " + deaths);
					}
				}
			}
			fps = 0;
			lastFPS += 1000;
			}
			fps++;

	}
	
	public static void changeColors(){
		red = (rand.nextFloat() * 2);
		if(red > 1) red = red/2;
		green = (rand.nextFloat() * 2);
		if(green > 1) green = green/2;
		blue = (rand.nextFloat() * 2);
		if(blue > 1) blue = blue/2;
		GL11.glColor3f(red, green, blue);
	}

	public static void advanceLevel() {
		if(levelNum < maxLevel){
			thePowerup = null;
			isPaused = true;
			runSlow = false;
			slowCount = 0;
			deaths = 0;
			if(levelLives != 0){
				lives = (int) Math.round(baseLives + 0.25*levelNum);
			}else{
				lives = levelLives;
				levelLives = 0;
			}
			flakes = new HashMap<Float[], Flake>();
			thePlayer.yPos = currentHeight - thePlayer.size;
			levelNum++;
			changeColors();
			save();
			isPaused = false;
			Display.setTitle(title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
		}else{
			flakes = new HashMap<Float[], Flake>();
			thePlayer = new Player().setPos(-1000, -1000);
			levelNum = 1234567890;
			Display.setTitle("--YOU WIN-- " + title);
		}
	}
	
	public static void decrementLevel(){
		levelLives = lives;
		lives = (int) Math.round(baseLives + 0.25*levelNum);
		levelNum--;
		thePowerup = null;
		if(levelNum > -1) deaths = levelDeaths[levelNum];
		thePlayer.yPos = 0;
		save();
		flakes = new HashMap<Float[], Flake>();
		Display.setTitle(title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
		changeColors();
	}

	public static void handleDeath(){
		isPaused = true;
		runSlow = false;
		slowCount = 0;
		thePowerup = null;
		Render.drawFlakes();
		Display.update();
		Render.drawFlakes();
		deaths++;
		totalDeaths++;
		lives--;
		if(lives < 0){
			isPaused = true;
			if(levelNum >= 5){
				levelNum -= 5;
			}else{
				levelNum = 0;
			}
			if(levelNum < 0){
				GL11.glColor3f(1, 0, 0);
				Display.setTitle("-GAME OVER- " + title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + levelDeaths[levelNum] + " on this level)");
				return;
			}else{

			}
			lives = (int) Math.round(baseLives + 0.25*levelNum);
		}
		if(levelNum > -1) levelDeaths[levelNum]++;
		Display.setTitle(title + " Level: " + levelNum + " Total Deaths: " + totalDeaths + " (" + lives + " lives remaining)");
		for(int i = 0; i < 20; i++){
			try{Thread.sleep(20);}catch(Exception e){}
			thePlayer.size--;
			Display.update();
		}
		flakes = new HashMap<Float[], Flake>();
		thePowerup = null;
		thePlayer = new Player();
		GL11.glColor3f(red, green, blue);
		save();
		isPaused = false;
	}

	public static void main(String[] args) throws Exception {
		new Updater(version);
		if(!needsUpdate){
			File updater = new File("Snow_Avoider_Installer.jar");
			File posDir = new File("flakes/");
			if(updater.exists()) updater.delete();
			save = new File("0x730x61.sa");
			lDeaths = new File("0x6c0x64.sa");
			positions = new File("flakes/positions.sa");
			if(!save.exists() || !lDeaths.exists() || !positions.exists()){
				if(!save.exists()) save.createNewFile();
				if(!lDeaths.exists()) lDeaths.createNewFile();
				if(!posDir.exists()) posDir.mkdirs();
				if(!positions.exists()) positions.createNewFile();
				loadedLDeaths = false;
			}else{
				HashMap<String, String> map = null;
				try{map = io.load("0x730x61.sa");}catch(Exception e){e.printStackTrace();}
				if(map != null){
					Iterator<Entry<String, String>> it = map.entrySet().iterator();
					while(it.hasNext()){
						Entry<String, String> entry = it.next();
						String key = entry.getKey();
						if(key.contentEquals("levelNum")){
							levelNum = Integer.valueOf(entry.getValue());
						}
						if(key.contentEquals("deaths")){
							deaths = Integer.valueOf(entry.getValue());
						}
						if(key.contentEquals("totalDeaths")){
							totalDeaths = Integer.valueOf(entry.getValue());
						}
						if(key.contentEquals("red")){
							red = Float.valueOf(entry.getValue());
						}
						if(key.contentEquals("green")){
							green = Float.valueOf(entry.getValue());
						}
						if(key.contentEquals("blue")){
							blue = Float.valueOf(entry.getValue());
						}
						if(key.contains("levelLives")){
							levelLives = Integer.valueOf(entry.getValue());
						}
					}
				}
				try{
					levelDeaths = io.loadLevelDeaths("0x6c0x64.sa");
					loadedLDeaths = true;
				}catch(Exception e1){
					loadedLDeaths = false;
					e1.printStackTrace();
				}
			}
			File dev = new File("IAMFHBGDS");
			if(dev.exists()){
				doDevStuff = true;
				System.err.println("RUNNING AS DEVELOPER");
			}
			new PNGLoader().setIcons();
			new Snow();
		}
		if(needsUpdate){
			Updater.downloadFile(new File("Snow_Avoider_Installer.jar"), new URL("https://github.com/fhbgds14531/SnowAvoiderDL/raw/master/Snow_Avoider_Installer.jar"));
			try{Runtime.getRuntime().exec("javaw.exe -jar Snow_Avoider_Installer.jar"); System.exit(0);}catch(Exception e){e.printStackTrace();}
		}
	}

	public static void save(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("deaths", String.valueOf(deaths));
		map.put("levelNum", String.valueOf(levelNum));
		map.put("totalDeaths", String.valueOf(totalDeaths));
		map.put("red", String.valueOf(red));
		map.put("green", String.valueOf(green));
		map.put("blue", String.valueOf(blue));
		map.put("levelLives", String.valueOf(levelLives));
		try {
			io.save(map, save.getPath());
			io.save(levelDeaths, lDeaths.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void runSlow() {
		runSlow = true;
	}
}