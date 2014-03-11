package snow.prog.fhbgds;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.*;

import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import snow.prog.fhbgds.entity.Flake;
import snow.prog.fhbgds.entity.Powerup;

public class Render {
	
	public static void drawShape1(float startX, float startY, float sizeX, float sizeY){
		glPushMatrix();
			glBegin(GL_TRIANGLES);
				glVertex2f(startX, startY);
				glVertex2f(startX, startY + sizeY);
				glVertex2f(startX + sizeX, startY + sizeY);
			glEnd();
		glPopMatrix();
	}
	
	public static void drawShape2(float startX, float startY, float sizeX, float sizeY){
		glPushMatrix();
			glBegin(GL_TRIANGLES);
				glVertex2f(startX + sizeX, startY + sizeY);
				glVertex2f(startX + sizeX, startY);
				glVertex2f(startX, startY);
			glEnd();
		glPopMatrix();
	}
	
	public static void drawSquare(float startX, float startY, float sizeX, float sizeY){
		drawShape1(startX, startY, sizeX, sizeY);
		drawShape2(startX, startY, sizeX, sizeY);
	}
	
	public static void drawPaused(int pauseXLoc, int pauseYLoc, int currentHeight){
		
		Render.drawSquare(pauseXLoc, pauseYLoc, currentHeight/5, currentHeight/5);
		GL11.glColor3f(0.1f, 0.1f, 0.1f);
		glPushMatrix();
			glBegin(GL_TRIANGLES);
				glVertex2f(pauseXLoc + 25, pauseYLoc + 20);
				glVertex2f(pauseXLoc + 25, pauseYLoc + currentHeight/10 + 40);
				glVertex2f(pauseXLoc + currentHeight/5 - 20, pauseYLoc + currentHeight/20 + 30);
			glEnd();
		glPopMatrix();
	}
	
	public static void drawFlakes(){
		Iterator<Entry<Float[], Flake>> it = Snow.flakes.entrySet().iterator();
		GL11.glColor3f(Snow.red, Snow.green, Snow.blue);
		while(it.hasNext()){
			Entry<Float[], Flake> entry = it.next();
			Render.drawSquare(entry.getValue().xPos, entry.getValue().yPos, entry.getValue().size, entry.getValue().size);
		}
	}
	
	public static void clearDisplay(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public static void drawPowerup(Powerup powerup) {
			GL11.glColor3f(powerup.red, powerup.green, powerup.blue);
			Render.drawSquare(powerup.xPos, powerup.yPos, powerup.size, powerup.size);
	}
}
