package snow.prog.fhbgds.render;

import java.awt.Color;
import java.awt.Font;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontHandling {

	@SuppressWarnings("unchecked")
	public static UnicodeFont getUnicodeFont(Font font, float size) {
		UnicodeFont uFont = new UnicodeFont(font.deriveFont(0, size));
		uFont.addAsciiGlyphs();
		uFont.getEffects().add(new ColorEffect(Color.WHITE));
		try {
		    uFont.loadGlyphs();
		} catch (SlickException e1) {
		    e1.printStackTrace();
		}
		return uFont;
	}
	
}
