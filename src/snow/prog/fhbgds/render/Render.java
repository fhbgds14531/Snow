package snow.prog.fhbgds.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;

import snow.prog.fhbgds.Snow;
import snow.prog.fhbgds.entity.Flake;
import snow.prog.fhbgds.entity.Powerup;

public class Render {
	
	public static void drawRectangle(float startX, float startY, float sizeX, float sizeY){
		glPushMatrix();
			glBegin(GL_TRIANGLES);
				glVertex2f(startX, startY);
				glVertex2f(startX, startY + sizeY);
				glVertex2f(startX + sizeX, startY);
			
				glVertex2f(startX + sizeX, startY);
				glVertex2f(startX + sizeX, startY + sizeY);
				glVertex2f(startX, startY + sizeY);
			glEnd();
		glPopMatrix();
	}
	
	public static void drawTexturedRectangle(float startX, float startY, float sizeX, float sizeY, Texture texture){
		GL11.glEnable(GL11.GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		glPushMatrix();
			glBegin(GL_TRIANGLES);
				glTexCoord2f(0, 0);
				glVertex2f(startX, startY);
				glTexCoord2f(0, 1);
				glVertex2f(startX, startY + sizeY);
				glTexCoord2f(1, 0);
				glVertex2f(startX + sizeX, startY);
				
				glTexCoord2f(1, 0);
				glVertex2f(startX + sizeX, startY);
				glTexCoord2f(1, 1);
				glVertex2f(startX + sizeX, startY + sizeY);
				glTexCoord2f(0, 1);
				glVertex2f(startX, startY + sizeY);
			glEnd();
		glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public static void drawPaused(int x, int y, int currentHeight){
		GL11.glColor3f(1, 1, 1);
		drawTexturedRectangle(x, y, currentHeight/5, currentHeight/5, Snow.game.playButton);
		drawTexturedRectangle(260, 400, 390, 50, Snow.game.button1);
		drawString(Snow.game.buttonFont, 265, 393, "Exit to menu");
	}
	
	public static void drawFlakes(){
		Iterator<Entry<Float[], Flake>> it = Snow.flakes.entrySet().iterator();
		GL11.glColor3f(Snow.red, Snow.green, Snow.blue);
		while(it.hasNext()){
			Entry<Float[], Flake> entry = it.next();
			Render.drawRectangle(entry.getValue().xPos, entry.getValue().yPos, entry.getValue().size, entry.getValue().size);
		}
	}
	
	public static void clearDisplay(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public static void drawPowerup(Powerup powerup) {
			GL11.glColor3f(powerup.red, powerup.green, powerup.blue);
			Render.drawTexturedRectangle(powerup.xPos, powerup.yPos, powerup.size, powerup.size, Snow.game.player);
	}

	public static void drawMainMenu() {
		clearDisplay();
		GL11.glColor3f(0.1f, 0.1f, 0.1f);
		drawRectangle(0, 0, Snow.game.currentWidth, Snow.game.currentHeight);
		GL11.glColor3f(1, 1, 1);
		drawTexturedRectangle(337, 300, 149, 70, Snow.game.button);
		drawTexturedRectangle(343, 400, 133, 60, Snow.game.button);
		if(Snow.game.isMuted){
			GL11.glColor3f(0.4f, 0.4f, 0.4f);
		}else{
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
		}
			Render.drawRectangle(Snow.game.currentWidth - 35, 15, 10, 10);
		glPushMatrix();
			glBegin(GL_TRIANGLES);
				glVertex2f(Snow.game.currentWidth - 35, 20);
				glVertex2f(Snow.game.currentWidth - 15, 10);
				glVertex2f(Snow.game.currentWidth - 15, 30);
			glEnd();
		glPopMatrix();
		drawString(Snow.game.titleFont, 90, 75, "Snow Avoider!");
		drawString(Snow.game.buttonFont, 343, 300, "Play");
		drawString(Snow.game.buttonFont, 350, 395, "Exit");
	}
	
	public static void drawString(UnicodeFont font, int x, int y, String text) {
		GL11.glEnable(GL11.GL_BLEND);
		font.drawString(x, y, text);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawGame() {
		clearDisplay();
		if(Snow.game.hasWon) {
			GL11.glColor3f(1, 1, 1);
			drawTexturedRectangle(0, 0, Snow.game.currentWidth, Snow.game.currentHeight, Snow.game.win);
			return;
		}
		GL11.glColor3f(0.1f, 0.1f, 0.1f);
		drawRectangle(0, 0, Snow.game.currentWidth, Snow.game.currentHeight);
		if(Snow.game.shouldRenderPaused){
			drawPaused((int) (Snow.game.currentWidth/2.35), (int) (Snow.game.currentHeight/2.5), Snow.game.currentHeight);
			GL11.glColor3f(1, 1, 1);
			if(Snow.game.isMuted){
				GL11.glColor3f(0.4f, 0.4f, 0.4f);
			}else{
				GL11.glColor3f(1.0f, 1.0f, 1.0f);
			}
			drawRectangle(Snow.game.currentWidth - 35, 15, 10, 10);
			glPushMatrix();
				glBegin(GL_TRIANGLES);
					glVertex2f(Snow.game.currentWidth - 15, 20);
					glVertex2f(Snow.game.currentWidth - 35, 10);
					glVertex2f(Snow.game.currentWidth - 35, 30);
				glEnd();
			glPopMatrix();
		} else {
			drawString(Snow.game.font, 2, 0, "Level: " + Snow.levelNum);
			drawString(Snow.game.font, 2, 20, "Deaths: " + Snow.levelDeaths[Snow.levelNum]);
			drawString(Snow.game.font, 2, 40, "Lives: " + Snow.lives);
			Render.drawTexturedRectangle(Snow.game.thePlayer.xPos, Snow.game.thePlayer.yPos, Snow.game.thePlayer.size, Snow.game.thePlayer.size, Snow.game.player);
			if(Snow.game.thePowerup != null){
				drawPowerup(Snow.game.thePowerup);
			}
			drawFlakes();
		}
	}
	
	public static void drawGameOver() {
		GL11.glColor3f(0, 0, 0);
		drawRectangle(0, 0, Snow.game.currentWidth, Snow.game.currentHeight);
		drawString(Snow.game.titleFont, 135, 100, "Game Over");
		GL11.glColor3f(1, 1, 1);
		drawTexturedRectangle(260, 400, 390, 50, Snow.game.button1);
		drawString(Snow.game.buttonFont, 265, 393, "Exit to menu");
	}
}
