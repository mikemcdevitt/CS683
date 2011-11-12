package com.example.test;

import android.app.Activity;
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
    
    
    
}