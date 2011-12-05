                                                                     
                                                                     
                                                                     
                                             
package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class ScreenIndexTestActivity extends Activity {
    private TextView tv = null;
    private CircleView cv = null;
    private Resources resources;
    float x = 0;
    float y = 0;
	//private static final float width = 30; 
	//private static final float height = 30;
	private int baseFreqIndex;
	public int height;
	public int width;
	public static int octaves = 9;
	public static int x_segments = 21;
	public static int y_segments = 1;
	public static int blackKeys = 24;
	public static float[] x_lines = new float[(x_segments + 1) * 4];
	public static float[] y_lines = new float[(y_segments + 1) * 4];
	public static float[] bk_lines = new float[blackKeys * 12];
	public double y_fraction;
	double freq;
	int keyIndex;
	public float wk_width;
	AudioRecorder audioRecorder;
	public static float[] pitches = new float[octaves * 12];
	public static float[] basePitches = {32.7f, 34.65f, 36.71f, 38.89f, 41.2f, 43.65f, 46.25f, 49f, 51.91f, 55f, 58.28f, 61.74f};
	AudioSynthesisTask audioSynth;
	public static int octave;
	public static int[] keyTranslation = {0, 2, 4, 5, 7, 9, 11};
	
	

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        //setContentView(R.layout.main);
        this.cv = new CircleView(this, x,y);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.cv = new CircleView(this, x,y);
        this.audioRecorder = new AudioRecorder();
           
        // Build the layout
        LinearLayout l1 = new LinearLayout(this);
        l1.setOrientation(LinearLayout.VERTICAL);
        l1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.main_record, null);
        l1.addView(layout);
        l1.addView(this.cv);

        // Set the content view.
		this.setContentView(l1);

		// Set the record button.
		CheckBox cb = (CheckBox) findViewById(R.id.recordcbx);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					AppLog.logString("Start Recording");
					// audioRecorder.startRecording();
				} else {
					AppLog.logString("Stop Recording");
					// audioRecorder.stopRecording();
				}
			}
		});


        
        tv = (TextView)findViewById(R.id.pitchIndex);
        
        resources = this.getResources();
        setOptionText();    
        

    	
        Display d = getWindowManager().getDefaultDisplay(); 
        width = d.getWidth();           // gets maximum x value (1280 on galaxy tab)
        height = d.getHeight();          // gets maximum y value (800 on galaxy tab)

        int j;
        
        for (int i = 0; i < octaves; i++)
    	{
        	for (j = 0; j < 12; j++)
        	{
        		pitches[j + (i * 12)] = basePitches[j] * (float)Math.pow(2, i);
        	}
    	}
        
        //lines = {0f, 0f, 0f, 800f};
        
        for (int i = 0; i <= y_segments; i++)
        {
        	j = (i * 4);
        	y_lines[j] = 0;
        	y_lines[j + 1] = (height / y_segments) * i;
        	y_lines[j + 2] = width;
        	y_lines[j + 3] = (height / y_segments) * i;
        }
        j = y_segments * 4;
        y_lines[j] = 0;
        y_lines[j + 1] = height - 1;
        y_lines[j + 2] = width;
        y_lines[j + 3] = height - 1;
        int bk_index = 0;
        wk_width = width / x_segments;
        float bk_width = wk_width / 2;
        for (int i = 0; i <= x_segments; i++)
        {
        	bk_index = i * 12;

        	j = (i * 4);
    		
        	if(i > 0 && i < x_segments && (float)(i%7)/3 != 1.0f && (float)(i%7)/7 != 0.0f)
        	{
        		bk_lines[bk_index] = (wk_width * i) - bk_width/2;
        		bk_lines[bk_index + 1] = 0;
        		bk_lines[bk_index + 2] = (wk_width * i) - bk_width/2; 
        		bk_lines[bk_index + 3] = (height / y_segments) * 0.5f;
        		bk_lines[bk_index + 4] = (wk_width * i) - bk_width/2;
        		bk_lines[bk_index + 5] = (height / y_segments) * 0.5f;
        		bk_lines[bk_index + 6] = (wk_width * i) + bk_width/2;
        		bk_lines[bk_index + 7] = (height / y_segments) * 0.5f;
        		bk_lines[bk_index + 8] = (wk_width * i) + bk_width/2;
        		bk_lines[bk_index + 9] = 0;
        		bk_lines[bk_index + 10] = (wk_width * i) + bk_width/2;
        		bk_lines[bk_index + 11] = (height / y_segments) * 0.5f;

            	x_lines[j] = (width / x_segments) * i;
        		x_lines[j + 1] = (height / y_segments * 0.5f);
            	x_lines[j + 2] = (width / x_segments) * i;
            	x_lines[j + 3] = height;
        	}
        	else
        	{
        	x_lines[j] = (width / x_segments) * i;
        	x_lines[j + 1] = 0;
        	x_lines[j + 2] = (width / x_segments) * i;
        	x_lines[j + 3] = height;
        	}
        }
        j = x_segments * 4;
        x_lines[j] = width - 1;
        x_lines[j + 1] = 0;
        x_lines[j + 2] = width - 1;
        x_lines[j + 3] = height;
        
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) { 
    	if (item.getItemId() == R.id.menu_prefs) {
    		Intent intent = new Intent();
    		intent.setClass(this, com.example.test.ScreenPreferencesActivity.class);
    		startActivityForResult(intent, 0);
    	}
    	return true;
    }
    
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) { 
    	super.onActivityResult(reqCode, resCode, data);
    	setOptionText();
    }
    
    private void setOptionText() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	String option = prefs.getString(resources.getString(R.string.start_list), resources.getString(R.string.default_start_value_preference));
    	
    	String[] optionText = resources.getStringArray(R.array.start_list_options);
    	baseFreqIndex = Integer.valueOf(option);
    	//tv.setText("preference for start is " + option + " (" + optionText[Integer.parseInt(option)] + ")" );
    	
    }
    
    
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

    	x = e.getX();
        y = e.getY();
        
        //int width;
    	//int height;
        
        double f_start = 27.5 * Math.pow(2,baseFreqIndex);
        double f_final = f_start * 2;
        
        double x_fraction = x/width;
        //double y_fraction = y/height;
        y_fraction = y/height;
        
        int key = keyTranslation[((int)Math.floor(x/wk_width))%7] + (12 * (int)((x/wk_width)/7));
        
        //double freq = f_start + x_fraction*(f_final - f_start);
        
        double freq = pitches[key];
       
        //TextView tvX = (TextView)findViewById(R.id.xIndex); 
        //TextView tvY = (TextView)findViewById(R.id.yIndex); 
        //TextView tvFLabel = (TextView)findViewById(R.id.rollIndexLabel);
        //TextView tvFreq = (TextView)findViewById(R.id.rollIndex);
        //tvX.setText("" + x);
        //tvY.setText("" + y);
        //tvFreq.setText("" + freq);
        //tvFLabel.setText("Output Frequency:");
        
        
         if (e.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            audioSynth = new AudioSynthesisTask();
            audioSynth.setFreq(freq);
            audioSynth.execute();
          } else if (e.getAction() == android.view.MotionEvent.ACTION_MOVE) {
        	  audioSynth.setFreq(freq);
          } else if (e.getAction() == android.view.MotionEvent.ACTION_UP) {
        	audioSynth.stopPlay();
          } 
         
        

         
         cv.mySpritePos.x = Math.round(x);
         cv.mySpritePos.y = Math.round(y);
         cv.invalidate();
        
        return true;
    }
    
    
    
    private static class CircleView extends View {
    	private  ShapeDrawable sprite = new ShapeDrawable(new OvalShape());
    	private int spriteWidth, spriteHeight = 50;
    	private Paint p;
    	public Point mySpritePos = new Point(0, 0);

    	
    	public CircleView(Context context, float x, float y) {
    		super(context);
    		setFocusable(true);
    		this.sprite.getPaint().setColor(Color.CYAN);
    	}
    	
    	@Override
    	protected void onDraw(Canvas canvas) {   		
    		this.sprite.setBounds(mySpritePos.x - 25, mySpritePos.y - 25, mySpritePos.x + 25, mySpritePos.y + 25);
    		this.sprite.draw(canvas);
		     Paint p = new Paint();
    		     p.setColor(Color.BLUE);
    		     canvas.drawLines(x_lines, p);
    		     p.setColor(Color.YELLOW);
    		     canvas.drawLines(y_lines, p);
    	}
    }
    
    private class AudioSynthesisTask extends AsyncTask<Void, Void, Void> {
    	AudioTrack audioTrack;
    	boolean keepGoing = true;
    	double freq = 440.0;
    	
    	
        @Override
        protected Void doInBackground(Void... params) {
          final int SAMPLE_RATE = 44025;

          int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
              AudioFormat.CHANNEL_CONFIGURATION_MONO,
              AudioFormat.ENCODING_PCM_16BIT);

          audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
              SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
              AudioFormat.ENCODING_PCM_16BIT, minSize,
              AudioTrack.MODE_STREAM);

          audioTrack.play();

          short[] buffer = new short[minSize];

          
          float angle = 0;

          float angular_frequency;
          while (keepGoing) {
        	//  angular_frequency = (float) (2 * Math.PI) * (float)freq
            //  / SAMPLE_RATE;
        	//  angular_frequency = (float) (2 * (float)freq)
            //  / SAMPLE_RATE;
          	    
          if ( y_fraction >= 0.5 ) {
        	  angular_frequency = (float) (2 * Math.PI) * (((float)freq
        	              / SAMPLE_RATE));
        	for (int i = 0; i < buffer.length / 6; i++) {           	
        	  buffer[i] = (short) (Short.MAX_VALUE * ((float) Math
                  .sin(angle)));
              angle += angular_frequency;
        	}
          }
              else{
            	  angular_frequency = (float) (2 * (float)freq)
                          / SAMPLE_RATE;
              	for (int i = 0; i < buffer.length / 6; i++) {           	
              	  buffer[i] = (short) (Short.MAX_VALUE * ((float) Math
                    .floor(angle)));
                angle += angular_frequency;
              	}
            }
            audioTrack.write(buffer, 0, buffer.length / 6);
          }

          return null;
        }
        
        protected void stopPlay() {
        	keepGoing=false;
        	audioTrack.stop();
        }
        
        protected void setFreq(double dfreq) {
        	freq = dfreq;
        }
        
        

        
      }
    
    
}