package snow.prog.fhbgds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Net {
	
	FileOutputStream out;
	File updater = new File("updater.jar");
	InputStream in;
	
	public Net(){
		try {
			out = new FileOutputStream(updater);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(!updater.exists()){
			try {
				updater.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void downloadUpdater(URL url) throws IOException {
	   URLConnection c = url.openConnection();
	   in = c.getInputStream();
	   
	   int n = -1;
	   byte[] buffer = new byte[4096];
	   
	   while((n = in.read(buffer)) != -1){
		   if(n>0){
			   out.write(buffer, 0, n);
		   }
	   }
	   in.close();
	   out.close();
	}
	
}
