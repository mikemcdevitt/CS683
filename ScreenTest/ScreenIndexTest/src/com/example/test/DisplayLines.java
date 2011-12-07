                                                                     
                                                                     
                                                                     
                                             
package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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


public class DisplayLines extends Activity {
    private CircleView cv = null;
    private Resources resources;
    float x = 0;
    float y = 0;
	//private static final float width = 30;
	//private static final float height = 30;
	private int baseFreqIndex;
	public int height;
	public int width;
	public static int octaves = 1;
	public static int x_segments = 12;
	public static int y_segments = 4;
	public static float[] x_lines = new float[(x_segments + 1) * 4];
	public static float[] y_lines = new float[(y_segments + 1) * 4];
	public static float[] string_lines = new float[(y_segments + 1) * 4];
	public static float[] fret_lines = new float[(x_segments + 1) * 4];
	public static double baseFreq = 41.2;
	public static float[] fretDotsX = new float[octaves * 5];
	public static int noteCounter = 0;        // counter for array for teaching a song.
	public static int nextNote[];
	public static int songLength;
	public static int[] learnSongFrets;
	public static int[] learnSongStrings;
	public static int[] stringOffset = new int[4];
	


    public static float[] bk_lines = new float[96];
	public double y_fraction;
	double freq;
	AudioSynthesisTask audioSynth;
	AudioRecorder audioRecorder;
	
	

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
					//AppLog.logString("Start Recording");
					//audioRecorder.startRecording();
				} else {
					//AppLog.logString("Stop Recording");
					//audioRecorder.stopRecording();
				}
			}
		});
        

        
        resources = this.getResources();
        setOptionText();    
        

    	
        Display d = getWindowManager().getDefaultDisplay(); 
        width = d.getWidth();           // gets maximum x value (1280 on galaxy tab)
        height = d.getHeight();          // gets maximum y value (800 on galaxy tab)
        height = height - 100;
    	
        
        //lines = {0f, 0f, 0f, 800f};
        int j;
        int i = 0;
        
        for (i = 0; i <= (x_segments * 4); i = i + 4)
        {
        	fret_lines[i] = width / x_segments * i / 4;
   			fret_lines[i + 1] = 0;
        	fret_lines[i + 2] = width / x_segments * i / 4;
        	fret_lines[i + 3] = height;
        }	
        
        	
        
        
        for (i = 1; i <= y_segments; i++)
        {
        	j = (i * 4);
        	string_lines[j] = 0;
        	string_lines[j + 1] = (height / y_segments) * i - (height / y_segments / 2);
        	string_lines[j + 2] = width;
        	string_lines[j + 3] = (height / y_segments) * i - (height / y_segments / 2);
        }
        
        for (i = 0; i < 4; i++)
        	stringOffset[i] = (height / y_segments) * i - (height / y_segments / 2);
        
        j = y_segments * 4;
        y_lines[0] = 0;
        y_lines[1] = 0;
        y_lines[2] = width;
        y_lines[3] = 0;
        y_lines[4] = 0;
        y_lines[5] = height - 1;
        y_lines[6] = width;
        y_lines[7] = height - 1;
        for (i = 0; i <= x_segments; i++)
        {
        	
        	j = (i * 4);
        	x_lines[j] = (width / x_segments) * i;
        	x_lines[j + 1] = 0;
        	x_lines[j + 2] = (width / x_segments) * i;
        	x_lines[j + 3] = height;
        }
        j = x_segments * 4;
        x_lines[j] = width - 1;
        x_lines[j + 1] = 0;
        x_lines[j + 2] = width - 1;
        x_lines[j + 3] = height;
        
       fretDotsX[0] = width * (5f/24f);
       fretDotsX[1] = width * (9f/24f);
       fretDotsX[2] = width * (13f/24f);
       fretDotsX[3] = width * (17f/24f);
       fretDotsX[4] = width * (23f/24f);
       
        
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
    	learnSongFrets = resources.getIntArray(R.array.learnSongFret);
    	learnSongStrings = resources.getIntArray(R.array.learnSongString);
    	songLength = learnSongStrings.length;
    	
    	baseFreqIndex = 1;
    	//tv.setText("preference for start is " + option + " (" + optionText[Integer.parseInt(option)] + ")" );
    }
    
    
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

    	x = e.getX();
        y = e.getY() - 10;
        
        //int width;
    	//int height;

        y_fraction = y/height;
        
        double f_start = baseFreq * Math.pow(2,baseFreqIndex) * Math.pow((4f/3f), Math.floor(y_fraction * 4));
        		//Math.pow(4/3, Math.floor(y_fraction * 4));
        double f_final = f_start * 2;
        
        double x_fraction = x/width;
        //double y_fraction = y/height;
        
        double freq = f_start - x_fraction*(f_start - f_final);
       
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
        	if (noteCounter < songLength - 1)
        	noteCounter++;
        	else noteCounter = 0;
          }
         

         cv.mySpritePos.x = Math.round(x);
         cv.mySpritePos.y = Math.round(y);
         cv.invalidate();
        
        return true;
    }
    
    
    
    private static class CircleView extends View {
    	private  ShapeDrawable sprite = new ShapeDrawable(new OvalShape());
    	//private int spriteWidth, spriteHeight = 50;
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
		     p = new Paint();
		     Paint p2 = new Paint();
		     p2.setColor(Color.BLACK);
    		     p.setColor(Color.BLUE);
    		     //canvas.drawLines(x_lines, p);
    		     //canvas.drawLines(bk_lines, p);
    		     p.setColor(Color.YELLOW);
    		     canvas.drawLines(y_lines, p);

    		     p.setColor(Color.WHITE);
    		     canvas.drawLines(fret_lines, p);
    		     
    		     p.setColor(Color.GRAY);
    		     //canvas.drawLines(y_lines, p);
    		     canvas.drawLines(string_lines, p);
    		     for (int i = 0; i < 4; i++){
    		     canvas.drawCircle(fretDotsX[i], 350, 15, p);}

    		     canvas.drawCircle(fretDotsX[4], 300, 15, p);
    		     canvas.drawCircle(fretDotsX[4], 400, 15, p);
    		     
    		     p.setColor(Color.RED);
    		     //canvas.drawCircle(learnSongFrets[noteCounter] * 200, stringOffset[learnSongStrings[noteCounter]], 20, p);
    		     canvas.drawCircle(learnSongFrets[noteCounter] * (1280/12), stringOffset[learnSongStrings[noteCounter]], 70, p);
    		     canvas.drawCircle(learnSongFrets[noteCounter] * (1280/12), stringOffset[learnSongStrings[noteCounter]], 30, p2);
    		     
    		     
    		     this.sprite.setBounds(mySpritePos.x - 25, mySpritePos.y - 25, mySpritePos.x + 25, mySpritePos.y + 25);
    	    		this.sprite.draw(canvas);
    	}
    }
    
    private class AudioSynthesisTask extends AsyncTask<Void, Void, Void> {
    	AudioTrack audioTrack;
    	boolean keepGoing = true;
    	
    	
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
          	    
          /*if ( y_fraction >= 0.5 ) {
        	  angular_frequency = (float) (2 * Math.PI) * (float)freq
        	              / SAMPLE_RATE;
        	for (int i = 0; i < buffer.length / 6; i++) {           	
        	  buffer[i] = (short) (Short.MAX_VALUE * ((float) Math
                  .sin(angle)));
              angle += angular_frequency;
        	}
          }
              else{*/
        	  
        //freq = freq * Math.pow(4/3, Math.floor(y_fraction * 4));
        	  
        	 /* if (y_fraction < 0.25)
        			  baseFreq = 41.2 * Math.pow(1.3333333333, 3);
        	  else if (y_fraction < 0.5)
        			  baseFreq = 41.2 * Math.pow(1.3333333333, 2);
        	  else if (y_fraction < 0.75)
        			  baseFreq = 41.2 * 1.3333333333;
        */
            	  angular_frequency = (float) (2 * (float)freq)
                          / SAMPLE_RATE;
              	for (int i = 0; i < buffer.length / 6; i++) {           	
              	  buffer[i] = (short) (Short.MAX_VALUE * ((float) Math
                    .floor(angle)) * 0.5);
                angle += angular_frequency;
              	//}
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