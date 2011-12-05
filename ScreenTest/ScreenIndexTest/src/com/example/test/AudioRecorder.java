package com.example.test;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

public class AudioRecorder {
	
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private String audioFileDir;
	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP }; 
	
	public AudioRecorder(String dirName) {
		this.audioFileDir = dirName;
	}
	
	public AudioRecorder() {
		//String dirPath = Environment.getExternalStorageDirectory().getPath();
		//audioFileDir = dirPath + "/" + AUDIO_RECORDER_FOLDER;
		audioFileDir = "/tmp";
	}
					
	private String getFilename(){	
		
		File file = new File(audioFileDir);
		if(!file.exists()){
			file.mkdirs();
		}
		
		return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
	}
	
	public void startRecording(){
		recorder = new MediaRecorder();
		
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());
		
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);
		
		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopRecording(){
		if(null != recorder){
			recorder.stop();
			recorder.reset();
			recorder.release();
			
			recorder = null;
		}
	}
	
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		
		public void onError(MediaRecorder mr, int what, int extra) {
			AppLog.logString("Error: " + what + ", " + extra);
		}
	};
	
	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		
		public void onInfo(MediaRecorder mr, int what, int extra) {
			AppLog.logString("Warning: " + what + ", " + extra);
		}
	};
}