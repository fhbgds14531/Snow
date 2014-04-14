package snow.prog.fhbgds.render;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;

import de.matthiasmann.twl.utils.PNGDecoder;

public class PNGLoader {
	
	String[] paths = new String[] {"textures/64x64.png", "textures/16x16.png"};
	
	public void setIcons(){
        ByteBuffer[] icons = new ByteBuffer[paths.length];
        try {
            for (int i = 0; i < paths.length; i++){
                icons[i] = ByteBuffer.allocateDirect(1);
                String path = paths[i];
                icons[i] = loadIcon(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Display.setIcon(icons);
    }
 
	private static ByteBuffer loadIcon(String path) throws IOException {
        InputStream in = PNGLoader.class.getResourceAsStream(path);
       	try {
           	PNGDecoder decoder = new PNGDecoder(in);
           	ByteBuffer bb = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
           	decoder.decode(bb, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
          	bb.flip();
           	return bb;
       	} finally {
       		in.close();
       	}
    }
}
