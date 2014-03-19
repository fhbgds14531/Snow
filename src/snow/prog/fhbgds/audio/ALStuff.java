package snow.prog.fhbgds.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import snow.prog.fhbgds.Snow;

public class ALStuff {
	
	private String musicLoc = "assets";
	
	private File[] files = new File[] {new File(musicLoc + "/" + Snow.musicFileName), new File(musicLoc + "/powerup.wav"), new File(musicLoc + "/upLevel.wav"), new File(musicLoc + "/downLevel.wav"), new File(musicLoc + "/death.wav")};
	private IntBuffer[] sources = new IntBuffer[getFiles().length];
	IntBuffer[] buffers = new IntBuffer[getFiles().length];
	FloatBuffer sourcePos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer sourceVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer listenerPos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer listenerVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer listenerOri = (FloatBuffer)BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f }).rewind();
	
	HashMap<String, IntBuffer> map = new HashMap<String, IntBuffer>();

	public void initAL(){
		try{
			AL.create();
			for(int i = 0; i < getFiles().length; i++){
				getSources()[i] = BufferUtils.createIntBuffer(1);
				buffers[i] = BufferUtils.createIntBuffer(1);
			}
			loadALData();
		} catch (LWJGLException le) {
			le.printStackTrace();
			return;
		}
		AL10.alGetError();
		setListenerValues();
		map.put("main", getSources()[0]);
		map.put("powerup", getSources()[1]);
		map.put("upLevel", getSources()[2]);
		map.put("downLevel", getSources()[3]);
		map.put("death", getSources()[4]);
	}
	
	public void killALData() {
		for(int i = 0; i < getFiles().length; i++){
			AL10.alDeleteSources(getSources()[i]);
			AL10.alDeleteBuffers(buffers[i]);
		}
	}
	
	void setListenerValues() {
		AL10.alListener(AL10.AL_POSITION   , listenerPos);
		AL10.alListener(AL10.AL_VELOCITY   , listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}
	
	public int loadALData() {
		for(int i = 0; i < getFiles().length; i++){
			if(buffers[i] == null) buffers[i] = BufferUtils.createIntBuffer(1);
			AL10.alGenBuffers(buffers[i]);
			InputStream in = null;
			BufferedInputStream bin = null;
			try {
				in = new FileInputStream(getFiles()[i].getPath());
				bin = new BufferedInputStream(in);
			} catch (Exception e) {
				e.printStackTrace();
				return AL10.AL_FALSE;
			}
			WaveData wavFile = WaveData.create(bin);
			try {if(in != null)in.close(); if(bin != null)bin.close();}catch(IOException ex){ex.printStackTrace();}
			
			AL10.alBufferData(buffers[i].get(0), wavFile.format, wavFile.data, wavFile.samplerate);
			wavFile.dispose();
			
			if(getSources()[i] == null) getSources()[i] = BufferUtils.createIntBuffer(1);
			AL10.alGenSources(getSources()[i]);
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
		while(source != getSources()[count]){
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
		System.out.println("Playing sound \"" + name + "\"");
		AL10.alSourcePlay(source);
	}

	public IntBuffer[] getSources() {
		return sources;
	}
	
	public File[] getFiles() {
		return files;
	}
}
