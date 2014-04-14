package snow.prog.fhbgds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Updater {
	
	float currentVersion;
	URL url;
	FileOutputStream out;
	File file = new File("version.txt");

	public Updater(float version){
		System.setProperty("jsse.enableSNIExtension", "false");
		currentVersion = version;
		try {
			out = new FileOutputStream(file);
			if(file.exists()) file.delete();
			file.createNewFile();
			url = new URL("https://github.com/fhbgds14531/SnowAvoiderDL/raw/master/version.txt");
			checkVersion(this.currentVersion, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkVersion(float version, URL url) throws Exception{
		
		try{downloadFile(file, url);}catch(Exception e){System.err.println("ERROR DOWNLOADING VERSIONS"); return;}
		
		String[] versions = Snow.io.loadArrayFromTxt(file.getPath());
		float num = Float.valueOf(versions[0]);
		if (num > this.currentVersion){
			Snow.needsUpdate = true;
		}
	}
	
	public static void downloadFile(File file, URL url) throws Exception{
		URLConnection c = url.openConnection();
		InputStream in = c.getInputStream();
		FileOutputStream os = new FileOutputStream(file);
		
		int n = -1;
		byte[] buffer = new byte[4096];
		
		while((n = in.read(buffer)) != -1){
		   if(n>0){
			   os.write(buffer, 0, n);
		   }
		}
		in.close();
		os.close(); 
	}
	
}
