package com.example.test;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class ScreenIndexTestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }
    
    float x = 0;
    float y = 0;
    
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

    	x = e.getX();
        y = e.getY();
       
        TextView tvX = (TextView)findViewById(R.id.xIndex); 
        TextView tvY = (TextView)findViewById(R.id.yIndex); 
        tvX.setText("" + x);
        tvY.setText("" + y);
        
        return true;
    }
    
    private class AudioSynthesisTask extends AsyncTask<Void, Void, Void> {
	    @Override
	    protected Void doInBackground(Void... params) {
	      final int SAMPLE_RATE = 22000;

	      int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
	          AudioFormat.CHANNEL_CONFIGURATION_MONO,
	          AudioFormat.ENCODING_PCM_16BIT);

	      AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	          SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
	          AudioFormat.ENCODING_PCM_16BIT, minSize,
	          AudioTrack.MODE_STREAM);

	      audioTrack.play();

	      short[] buffer = new short[minSize];
	      
	      
	      
	      float angular_frequency = (float) R.string.frequency
	          / SAMPLE_RATE;
	      float angle = 0;

	   //   while (keepGoing) {
	        for (int i = 0; i < buffer.length; i++) {
	          buffer[i] = (short) (Short.MAX_VALUE * ((float) Math
	              .sin(angle)));
	          angle += angular_frequency;
	        }
	        audioTrack.write(buffer, 0, buffer.length);
	   //   }

	      return null;
	    }
	  }
    
}