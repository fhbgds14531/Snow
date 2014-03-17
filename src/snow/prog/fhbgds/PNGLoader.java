package snow.prog.fhbgds;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;

import de.matthiasmann.twl.utils.PNGDecoder;

public class PNGLoader {
	
	String[] paths = new String[] {"Snow_Avoider_lib/64x64.png", "Snow_Avoider_lib/16x16.png"};
	
	public void setIcons(){
        ByteBuffer[] icons = new ByteBuffer[paths.length];
//		ByteBuffer[] icons = new ByteBuffer[2];
        try {
//        	icons[0] = ImageIO.read(new File("Snow_Avoider_lib/64x64.png"));
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
        InputStream inputStream = new FileInputStream(path);
       	try {
           	PNGDecoder decoder = new PNGDecoder(inputStream);
           	ByteBuffer b = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
           	decoder.decode(b, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
          	b.flip();
           	return b;
       	} finally {
       		inputStream.close();
       	}
    }
}
