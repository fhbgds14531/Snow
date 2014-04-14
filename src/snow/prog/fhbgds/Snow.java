package snow.prog.fhbgds;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.awt.Font;
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
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;

import snow.prog.fhbgds.audio.ALStuff;
import snow.prog.fhbgds.entity.BaseClass;
import snow.prog.fhbgds.entity.Flake;
import snow.prog.fhbgds.entity.Player;
import snow.prog.fhbgds.entity.Powerup;
import snow.prog.fhbgds.render.FontHandling;
import snow.prog.fhbgds.render.PNGLoader;
import snow.prog.fhbgds.render.Render;
import snow.prog.fhbgds.render.TextureManager;

public class Snow {
	
	public static ALStuff al = new ALStuff();
	public static final String musicFileName = "Main.wav";
//=======================================================
	
	private static Float version = 0.5f; //MARKER Version
	
//=======================================================
	public static Random rand = new Random();

	public boolean isMuted = false;
	
	public static boolean isWinner = false;
	
	public Cursor emptyCursor;
	public Cursor nativeCursor;
	
	static File save;
	static File lDeaths;
	static IO io = new IO();

	public static float red = 1.0f;
	public static float green = 1.0f;
	public static float blue = 1.0f;

	public static int levelNum = 0;
	public static int maxLevel = 1023;
	public static Integer[] levelDeaths = new Integer[maxLevel + 1];
	public static int deaths = 0;
	public static int totalDeaths = 0;
	public static int baseLives = 20;
	public static int lives = 10;
	public static int frequency = 27;
	public static int baseFreq = 27;

	public volatile boolean isCloseRequested = false;
	public volatile boolean isPaused = false;
	public volatile boolean shouldRenderPaused;
	public static Snow game;
	public static HashMap<Float[], Flake> flakes = new HashMap<Float[], Flake>();

	public BaseClass thePlayer;

	public int currentHeight;
	public int currentWidth;

	DisplayMode defaultMode = new DisplayMode(800, 600);
	DisplayMode fullscreen = Display.getDesktopDisplayMode();

	public static String title = "Snow Avoider! Version: " + version;
	private int fps;
	private long lastFPS;
	private Timer timer;
	private static boolean doFPS;
	private static long timeOfLastFrame;
	private static boolean loadedLDeaths;
	public static boolean needsUpdate;
	public Powerup thePowerup;
	private boolean runSlow;
	private static int slowCount = 0;
	public int smallCount = 0;
	public int ticksSinceDeath = 0;

	public static boolean doDevStuff;
	public boolean doShrink = false;
	public static Integer levelLives;
	
	public String currentlyRunning = "MainMenu";
	
	public Texture player;
	public Texture button;
	public Texture win;
	public Texture playButton;
	public Texture button1;
	
	public boolean hasWon;

	public UnicodeFont font;
	public UnicodeFont titleFont;
	public UnicodeFont buttonFont;
	public boolean isGameOver;
	
	public Snow() { //MARKER Constructor
		try {
			al.initAL();
			Display.setDisplayMode(defaultMode);
			Display.setTitle("Loading...");
			Display.create();
			Mouse.create();
			emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
			nativeCursor = Mouse.getNativeCursor();
			currentWidth = Display.getDisplayMode().getWidth();
			currentHeight = Display.getDisplayMode().getHeight();
			getReadyFor2DDrawing();
			game = this;
			thePlayer = new Player();
			font = FontHandling.getUnicodeFont(new Font("", Font.PLAIN, 50), 15.0f);
			titleFont = FontHandling.getUnicodeFont(new Font("", Font.PLAIN, 50), 100.0f);
			buttonFont = FontHandling.getUnicodeFont(new Font("", Font.PLAIN, 50), 50.0f);
			new TextureManager();
			runMainMenu("");
		} catch (Exception e) {
			e.printStackTrace();
			Display.destroy();
			al.killALData();
			Mouse.destroy();
			AL.destroy();
			System.exit(1);
		}
	}

	private void runMainMenu(String running) {
		if(running == null || running.isEmpty()) {
			if(!this.isMuted) {
				al.playSound("main", 1f, 1f, true);
			}
		}
		Display.setTitle(title);
		while(!Display.isCloseRequested() && !this.isCloseRequested) {
			while(Keyboard.next()) {
				if(Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_F11) {
						try{
							Util.setDisplayMode(800, 600, !Display.isFullscreen());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
			while(Mouse.next()) {
				if(Mouse.getEventButtonState()) {
					if(Mouse.getEventButton() == 0) {
						if(Mouse.getX() >= 337 && Mouse.getY() <= 300 && Mouse.getX() <= 487 && Mouse.getY() >= 230) {
							this.currentlyRunning = "Game";
							this.runGame();
						}
					}
				}
				if(Mouse.getEventButtonState()) {
					if(Mouse.getEventButton() == 0) {
						if(Mouse.getX() >= 343 && Mouse.getY() <= 200 && Mouse.getX() <= 476 && Mouse.getY() >= 140) {
							this.exit(0);
						}
					}
				} //343, 400, 133, 60,
				if(Mouse.getEventButtonState()) {
					if(Mouse.getEventButton() == 0) {
						if(Mouse.getX() <= currentWidth - 15 && Mouse.getY() <= 590 && Mouse.getX() >= currentWidth - 35 && Mouse.getY() >= 570){
							this.isMuted = !this.isMuted;
							if(!this.isMuted){
								for(int i = 0; i < al.getSources().length; i++){
									AL10.alSourcef(al.getSources()[i].get(0), AL10.AL_GAIN, 1f);
								}
								AL10.alSourcePlay(al.getSources()[0].get(0));
							}else{
								for(int i = 1; i < al.getSources().length; i++){
									AL10.alSourceStop(al.getSources()[i].get(0));
								}
								AL10.alSourcePause(al.getSources()[0].get(0));
							}
						}
					}
				}
			}
			updateDebugInfo();
			doRenderTick();
			Display.update();
			Display.sync(240);
			if(Display.isCloseRequested()){
				this.exit(0);
			}
		}
	}

	private void runGame() {
		timer = new Timer(240.0f);
		if(levelLives == null) levelLives = 0;
		if(!loadedLDeaths){
			int i = 0;
			while (i <= maxLevel){
				levelDeaths[i] = 0;
				i++;
			}
		}
		int colorCount = 0;
		timeSinceLastFrame();
		lastFPS = getSystemTime();
		lives = baseLives + (int) Math.round(levelNum * 0.2); 
		levelLives = lives;
		this.updateTitle();
		while(Keyboard.next()) {} //Flush queued keypresses
		isPaused = true;
		shouldRenderPaused = true;
		if(levelNum > 99){
			this.timer.timerSpeed += (0.01f * (levelNum - 99));
			if(this.timer.timerSpeed > 2f) this.timer.timerSpeed = 2f;
		}
		System.out.println("Initializing timer with " + this.timer.ticksPerSecond + " TPS at speed "+ this.timer.timerSpeed);
		
		while(!Display.isCloseRequested() && !isCloseRequested && !this.currentlyRunning.contentEquals("MainMenu")){ //MARKER Game loop
			if(this.shouldRenderPaused) this.isPaused = true;
			if(this.isPaused && !this.isMuted){
				AL10.alSourcef(al.getSources()[0].get(0), AL10.AL_GAIN, 0.75f);
			}else if(!this.isMuted && !this.isPaused){
				AL10.alSourcef(al.getSources()[0].get(0), AL10.AL_GAIN, 1.00f);
			}
			for (int i = 0; i < this.timer.elapsedFullTicks; i++){
				if(!this.isGameOver) {
					this.doTick();
					this.ticksSinceDeath++;
				}
	        }
			
			doRenderTick();
			Display.update();
			Display.sync((int)this.timer.ticksPerSecond);
			
			updateDebugInfo();
			updateFrequency();
			checkKeyboardAndMouse();
			this.timer.updateTimer();
			if(!isPaused){
				if(levelNum == maxLevel && colorCount == 10 && !doFPS){
					changeColors();
					colorCount = 0;
				}
			}
			if(!isPaused){
				if(Keyboard.isKeyDown(Keyboard.KEY_UP)) thePlayer.yPos-= 1f;
				if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) thePlayer.yPos+=1f;
				if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) thePlayer.xPos-=1f;
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) thePlayer.xPos+=1f;
				if(runSlow && slowCount <= 255){
					slowCount++;
					if(slowCount >= 511){
						slowCount = 0;
						runSlow = false;
					}
				}
			}
			if(Display.isCloseRequested()){
				this.exit(0);
			}
		}
	}

	private void exit(int i) {
		save();
		al.killALData();
		AL.destroy();
		isCloseRequested = true;
		Display.destroy();
		Mouse.destroy();
		System.exit(i);
	}

	private void updateFrequency() {
		frequency = (int) ((baseFreq - (levelNum * 0.3f)));
		if(frequency <= 3) frequency = 3;
	}
	
	public void createFlake(){
		Flake flake = new Flake();
		flakes.put(new Float[] {flake.xPos, flake.yPos}, flake);
		if(rand.nextInt(4096) < levelNum + 1){
			if(thePowerup == null && !this.runSlow){
				thePowerup = new Powerup();
			}
		}
	}

	public void doTick(){
		if(levelNum != 1234567890 && !isPaused){
			if(!this.runSlow){
				updateFlakes();
				if(thePowerup != null) thePowerup.onUpdate();
			}
			this.thePlayer.onUpdate();
		}
	}
	
	public int timeSinceLastFrame() {
	    long currentTime = getSystemTime();
	    int diff = (int) (currentTime - timeOfLastFrame);
	    timeOfLastFrame = currentTime;
	    return diff;
	}

	private void getReadyFor2DDrawing() {
		glMatrixMode(GL_PROJECTION);
		glEnable(GL_TEXTURE);
		glEnable(GL_TEXTURE_2D);
		glLoadIdentity();
		GL11.glViewport(0, 0, currentWidth, currentHeight);
		glOrtho(0, 800, 600, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);	
	}

	public void updateFlakes(){
		Float[][] removalList = new Float[2][128];
		int count = 0;
		Iterator<Entry<Float[], Flake>> it = flakes.entrySet().iterator();
		while(it.hasNext()){
			Entry<Float[], Flake> entry = it.next();
			entry.getValue().onUpdate();
			if(entry.getValue().timeTillMelt <= 0 && entry.getValue().rand.nextFloat() < 0.5f){
				if(entry.getValue().size >= 1){
					entry.getValue().size--;
					entry.getValue().timeTillMelt = 150;
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

	public void doRenderTick(){ //MARKER Render method
		if(!this.isGameOver) {
			switch(this.currentlyRunning) {
			case "MainMenu":
				Render.drawMainMenu();
				break;
			case "Game":
				Render.drawGame();
				break;
			default:
				Render.drawMainMenu();
				break;
			}
		} else {
			Render.drawGameOver();
		}
		
	}

	public static long getSystemTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
 
	public void updateDebugInfo() {
		if (getSystemTime() - lastFPS > 1000) {
			if(doFPS){
				String currTitle = Display.getTitle();
				String newTitle = "";

				if(currTitle.contains(" -FPS: ")){
					int index = currTitle.indexOf(" FPS");
					newTitle = currTitle.substring(0, index);
				}else{
					newTitle = currTitle;
				}
				if(!newTitle.contentEquals("")){
					Display.setTitle(newTitle + " FPS: " + fps + " Difficulty: " + (baseFreq - frequency) + " Level Deaths: " + deaths);
				}
			}
			fps = 0;
			lastFPS += 1000;
			}
			fps++;

	}
	
	public void changeColors(){
		red = (rand.nextFloat() * 2);
		if(red > 1) red = red/2;
		if(red < 0.3f) red = 0.3f;
		green = (rand.nextFloat() * 2);
		if(green > 1) green = green/2;
		if(red < 0.3f) red = 0.3f;
		blue = (rand.nextFloat() * 2);
		if(blue > 1) blue = blue/2;
		if(red < 0.3f) red = 0.3f;
		GL11.glColor3f(red, green, blue);
	}

	public void advanceLevel() {
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
			if(levelNum > 99){
				this.timer.timerSpeed += 0.01f;
				if(this.timer.timerSpeed > 2.0f) this.timer.timerSpeed = 2.0f;
			}
			isPaused = false;
			if(!this.isMuted) al.playSound("upLevel", 1, 1, false);
			this.updateTitle();
			smallCount = 0;
		}else{
			flakes = new HashMap<Float[], Flake>();
			thePlayer = new Player().setPos(-1000, -1000);
			thePowerup = null;
			levelNum = 1234567890;
			Display.setTitle("Snow Avoider!");
			this.hasWon = true;
			isWinner = true;
			save();
		}
	}
	
	public void decrementLevel(){
		this.isPaused= true;
		this.ticksSinceDeath = 0;
		levelLives = lives;
		lives = (int) Math.round(baseLives + 0.25*levelNum);
		levelNum--;
		if(levelNum > 99) this.timer.timerSpeed -= 0.01f;
		thePowerup = null;
		if(levelNum > -1) deaths = levelDeaths[levelNum];
		thePlayer.yPos = 0;
		save();
		flakes = new HashMap<Float[], Flake>();
		this.updateTitle();
		changeColors();
		if(!this.isMuted) al.playSound("downLevel", 1, 1, false);
		this.isPaused = false;
	}

	public void handleDeath(){
		isPaused = true;
		this.ticksSinceDeath = 0;
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
			if(levelNum <= 0){
				GL11.glColor3f(1, 0, 0);
				Display.setTitle("-GAME OVER- " + Display.getTitle());
				return;
			}else{

			}
			lives = (int) Math.round(baseLives + 0.25*levelNum);
		}
		if(levelNum > -1) levelDeaths[levelNum]++;
		this.updateTitle();
		if(!this.isMuted) al.playSound("death", 1, 0.25f, false);
		for(int i = 0; i < 20; i++){
			try{Thread.sleep(20);}catch(Exception e){}
			Display.update();
		}
		flakes = new HashMap<Float[], Flake>();
		thePowerup = null;
		this.thePlayer = new Player();
		GL11.glColor3f(red, green, blue);
		save();
		this.ticksSinceDeath = 0;
		isPaused = false;
	}

	public static void main(String[] args) throws Exception { //MARKER main method
		new Updater(version);
		if(!needsUpdate){
			File updater = new File("Snow_Avoider_Installer.jar");
			if(updater.exists()) updater.delete();
			save = new File("0x730x61.sa");
			lDeaths = new File("0x6c0x64.sa");
			if(!save.exists() || !lDeaths.exists()){
				if(!save.exists()) save.createNewFile();
				if(!lDeaths.exists()) lDeaths.createNewFile();
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
							Snow.levelNum = Integer.valueOf(entry.getValue());
							if(Snow.levelNum == 1234567890) {
								Snow.levelNum = 0;
							}
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
						if(key.contains("isWinner")){
							isWinner = Boolean.valueOf(entry.getValue());
						}
					}
				}
				try{
					levelDeaths = io.loadLevelDeaths("0x6c0x64.sa");
					if(levelDeaths.length == Snow.maxLevel) {
						loadedLDeaths = true;
					}else {
						levelDeaths = new Integer[maxLevel + 1];
					}
				}catch(Exception e1){
					loadedLDeaths = false;
					e1.printStackTrace();
				}
			}
			File dev = new File("IAMFHBGDS");
			if(dev.exists()){
				doDevStuff = true;
				System.out.println("RUNNING AS DEVELOPER");
			}
			new PNGLoader().setIcons();
			new Snow();
		}else if(needsUpdate){
			Updater.downloadFile(new File("Snow_Avoider_Installer.jar"), new URL("https://github.com/fhbgds14531/SnowAvoiderDL/raw/master/Snow_Avoider_Installer.jar"));
			try{Runtime.getRuntime().exec("javaw.exe -jar Snow_Avoider_Installer.jar"); System.exit(0);}catch(Exception e){e.printStackTrace();}
		}
	}

	public void save(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("deaths", String.valueOf(deaths));
		map.put("levelNum", String.valueOf(levelNum));
		map.put("totalDeaths", String.valueOf(totalDeaths));
		map.put("red", String.valueOf(red));
		map.put("green", String.valueOf(green));
		map.put("blue", String.valueOf(blue));
		map.put("levelLives", String.valueOf(levelLives));
		map.put("timerSpeed", String.valueOf(this.timer.timerSpeed));
		map.put("iswinner", String.valueOf(isWinner));
		try {
			io.save(map, save.getPath());
			io.save(levelDeaths, lDeaths.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runSlow() {
		this.runSlow = true;
	}

	public void updateTitle() {
		Display.setTitle(Snow.title + " Level: " + Snow.levelNum + " Total Deaths: " + Snow.totalDeaths + " (" + Snow.lives + " lives remaining)");
	}
	
	public void gameOver() {
		this.isGameOver = true;
		this.isPaused = true;
		this.shouldRenderPaused = true;
		try {
			Mouse.setNativeCursor(nativeCursor);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	private void checkKeyboardAndMouse() {
		while(Mouse.next()){
			if(Mouse.getEventButtonState()){
				if(Mouse.getEventButton() == 0){
					int x = Mouse.getX();
					int y = Mouse.getY();
					int pauseXLoc = (int) (currentWidth/2.35);
					int pauseYLoc = (int) (currentHeight/2.5);
					if(x >= pauseXLoc && (y >= pauseYLoc) && (x <= pauseXLoc + currentHeight/5) && (y <= pauseYLoc + currentHeight/5)){
						try {
							Mouse.setNativeCursor(emptyCursor);
						} catch (Exception e) {
							e.printStackTrace();
						}
						isPaused = false;
						shouldRenderPaused = false;
						GL11.glColor3f(red, green, blue);
						this.updateTitle();
					}
					if(x >= 15 && (currentHeight - y >= 10) && (x <= 35) && (currentHeight - y <= 30)){
						if(this.shouldRenderPaused){
							this.isMuted = !this.isMuted;
							if(!this.isMuted){
								for(int i = 0; i < al.getSources().length; i++){
									AL10.alSourcef(al.getSources()[i].get(0), AL10.AL_GAIN, 1f);
								}
								AL10.alSourcePlay(al.getSources()[0].get(0));
							}else{
								for(int i = 1; i < al.getSources().length; i++){
									AL10.alSourceStop(al.getSources()[i].get(0));
								}
								AL10.alSourcePause(al.getSources()[0].get(0));
							}
						}
					}
					y = currentHeight - y;
					if(x >= 265 && y >= 400 && x <= 550 && y <= 450) {
						save();
						for(int i = 1; i < al.getSources().length; i++){
							AL10.alSourceStop(al.getSources()[i].get(0));
						}
						Display.setTitle("Loading...");
						for(float i = 0.75f; i > 0; i -= 0.01f) {
							AL10.alSourcef(al.getSources()[0].get(0), AL10.AL_GAIN, i);
							Display.update();
							Display.sync(240);
							try {Thread.sleep(15);}catch(Exception e) {e.printStackTrace();}
						}
						AL10.alSourceStop(al.getSources()[0].get(0));
						AL10.alSourcef(al.getSources()[0].get(0), AL10.AL_GAIN, 1f);
						AL10.alSourcePlay(al.getSources()[0].get(0));
						if(this.isGameOver) {
							levelNum = 0;
							for(int i = 0; i < levelDeaths.length; i++) {
								levelDeaths[i] = 0;
							}
							this.isGameOver = false;
						}
						this.currentlyRunning = "MainMenu";
						this.runMainMenu("Game");
					}
				}
			}
		}
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_F11) {
					try{
						Util.setDisplayMode(Display.getDisplayMode().getWidth(), Display.getDisplayMode().getWidth(), !Display.isFullscreen());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_F3) {
						doFPS = !doFPS;
						this.updateTitle();
						if(isPaused){
							Display.setTitle("[PAUSED] " + Display.getTitle());
						}
					}
				}
				if(Keyboard.getEventKeyState()){
					if(Keyboard.getEventKey() == Keyboard.KEY_SPACE){
						changeColors();
					}
				}
				if((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) && Keyboard.isKeyDown(Keyboard.KEY_R)){
					if(!this.hasWon) {
						flakes = new HashMap<Float[], Flake>();
						thePlayer = new Player();
						lives = baseLives;
						isPaused = true;
						shouldRenderPaused = true;
						deaths = 0;
						red = 1.0f;
						green = 1.0f;
						blue = 1.0f;
						GL11.glColor3f(red, green, blue);
						this.updateTitle();
					}
				}
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
						if(Snow.game.currentlyRunning.contentEquals("Game")) {
							if(isPaused){
								try {
									Mouse.setNativeCursor(emptyCursor);
								} catch (Exception e) {
									e.printStackTrace();
								}
								GL11.glColor3f(red, green, blue);
								Display.setTitle(Display.getTitle().substring(Display.getTitle().indexOf("Snow")));
							}else{
								try {
									Mouse.setNativeCursor(nativeCursor);
								} catch (Exception e) {
									e.printStackTrace();
								}
								Display.setTitle("[PAUSED] " + Display.getTitle());
							}
							isPaused = !isPaused;
							shouldRenderPaused = !shouldRenderPaused;
						}
					}
				}
//==========================================================================================
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_L && doDevStuff) {
						if(!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))){
							advanceLevel();
							if(shouldRenderPaused){
								isPaused = true;
							}
						}
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_L) && (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) && doDevStuff) {
					for(int i = 0; i < 10; i++){
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
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_LBRACKET && doDevStuff) {
						this.gameOver();
					}
				}
			}
		}
	}
}
