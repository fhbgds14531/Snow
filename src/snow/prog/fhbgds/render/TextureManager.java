package snow.prog.fhbgds.render;

import java.io.InputStream;

import org.newdawn.slick.opengl.TextureLoader;

import snow.prog.fhbgds.Snow;

public class TextureManager {

	public TextureManager() throws Exception {
		InputStream in = TextureManager.class.getResourceAsStream("textures/player.png");
		Snow.game.player = TextureLoader.getTexture("PNG", in);
		in = TextureManager.class.getResourceAsStream("textures/button.png");
		Snow.game.button = TextureLoader.getTexture("PNG", in);
		in = TextureManager.class.getResourceAsStream("textures/playButton.png");
		Snow.game.playButton = TextureLoader.getTexture("PNG", in);
		in = TextureManager.class.getResourceAsStream("textures/win.png");
		Snow.game.win = TextureLoader.getTexture("PNG", in);
		in = TextureManager.class.getResourceAsStream("textures/button1.png");
		Snow.game.button1 = TextureLoader.getTexture("PNG", in);
	}
	
}
