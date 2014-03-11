package snow.prog.fhbgds;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;

import de.matthiasmann.twl.utils.PNGDecoder;

public class PNGLoader {
	
	private String[] ICON_PATHS = new String[] {"Snow_Avoider_lib/16x16.png", "Snow_Avoider_lib/64x64.png"};
	
	public void setIcons(){
        ByteBuffer[] icon_array = new ByteBuffer[ICON_PATHS.length];
        try {
            for (int i = 0; i < ICON_PATHS.length; i++){
                icon_array[i] = ByteBuffer.allocateDirect(1);
                String path = ICON_PATHS[i];
                icon_array[i] = loadIcon(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Display.setIcon(icon_array);
    }
 
	private static ByteBuffer loadIcon(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
       	try {
           	PNGDecoder decoder = new PNGDecoder(inputStream);
           	ByteBuffer bytebuf = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
           	decoder.decode(bytebuf, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
          	bytebuf.flip();
           	return bytebuf;
       	} finally {
       		inputStream.close();
       	}
    }
}
