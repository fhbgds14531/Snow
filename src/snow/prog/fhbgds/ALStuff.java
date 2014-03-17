package snow.prog.fhbgds;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class ALStuff {
	
	private File[] files = new File[] {new File("Snow_Avoider_lib/" + Snow.musicFileName), new File("Snow_Avoider_lib/powerup.wav"), new File("Snow_Avoider_lib/upLevel.wav"), new File("Snow_Avoider_lib/downLevel.wav"), new File("Snow_Avoider_lib/death.wav")};
	IntBuffer[] sources = new IntBuffer[files.length];
	IntBuffer[] buffers = new IntBuffer[files.length];
	FloatBuffer sourcePos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer sourceVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer listenerPos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer listenerVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer listenerOri = (FloatBuffer)BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f }).rewind();
	
	HashMap<String, IntBuffer> map = new HashMap<String, IntBuffer>();

	public void initAL(){
		try{
			AL.create();
			for(int i = 0; i < files.length; i++){
				sources[i] = BufferUtils.createIntBuffer(1);
				buffers[i] = BufferUtils.createIntBuffer(1);
			}
			loadALData();
		} catch (LWJGLException le) {
			le.printStackTrace();
			return;
		}
		AL10.alGetError();
		setListenerValues();
		map.put("main", sources[0]);
		map.put("powerup", sources[1]);
		map.put("upLevel", sources[2]);
		map.put("downLevel", sources[3]);
		map.put("death", sources[4]);
	}
	
	public void killALData() {
		for(int i = 0; i < files.length; i++){
			AL10.alDeleteSources(sources[i]);
			AL10.alDeleteBuffers(buffers[i]);
		}
	}
	
	void setListenerValues() {
		AL10.alListener(AL10.AL_POSITION   , listenerPos);
		AL10.alListener(AL10.AL_VELOCITY   , listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}
	
	public int loadALData() {
		for(int i = 0; i < files.length; i++){
			if(buffers[i] == null) buffers[i] = BufferUtils.createIntBuffer(1);
			AL10.alGenBuffers(buffers[i]);
			FileInputStream in = null;
			BufferedInputStream bin = null;
			try {
				in = new FileInputStream(files[i].getPath());
				bin = new BufferedInputStream(in);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				return AL10.AL_FALSE;
			}
			WaveData wavFile = WaveData.create(bin);
			try {in.close(); bin.close();}catch(IOException ex){ex.printStackTrace();}
			
			AL10.alBufferData(buffers[i].get(0), wavFile.format, wavFile.data, wavFile.samplerate);
			wavFile.dispose();
			
			if(sources[i] == null) sources[i] = BufferUtils.createIntBuffer(1);
			AL10.alGenSources(sources[i]);
		}
		if (AL10.alGetError() == AL10.AL_NO_ERROR) return AL10.AL_TRUE;
		return AL10.AL_FALSE;
	}
	
	public void playSound(String name, float pitch, float gain, boolean loop){
		IntBuffer source = map.get(name);
		if(source == null){
			System.err.println("Invalid sound name: \"" + name + "\"");
			return;
		}
		int count = 0;
		while(source != sources[count]){
			count++;
		}
		AL10.alSourcei(source.get(0), AL10.AL_BUFFER,   buffers[count].get(0));
		AL10.alSourcef(source.get(0), AL10.AL_PITCH,    pitch    			 );
		AL10.alSourcef(source.get(0), AL10.AL_GAIN,     gain     			 );
		AL10.alSource (source.get(0), AL10.AL_POSITION, sourcePos			 );
		AL10.alSource (source.get(0), AL10.AL_VELOCITY, sourceVel			 );
		if(loop){
			AL10.alSourcei(source.get(0), AL10.AL_LOOPING, AL10.AL_TRUE);
		}
		AL10.alSourcePlay(source);
	}
}
