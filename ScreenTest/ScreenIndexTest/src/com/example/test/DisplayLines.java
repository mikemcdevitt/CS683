package com.example.test;

public class DisplayLines extends ScreenIndexTestActivity{
	float[] lines;
	
	public float[] SetUpGridX(){
		int j;
        for (int i = 0; i <= x_segments; i++)
        {
        	j = (i * 4);
        	lines[j] = (width / x_segments) * i;
        	lines[j + 1] = 0;
        	lines[j + 2] = (width / x_segments) * i;
        	lines[j + 3] = height;
        }
        j = x_segments * 4;
        lines[j] = width - 1;
        lines[j + 1] = 0;
        lines[j + 2] = width - 1;
        lines[j + 3] = height;
		
		
		return lines;
	}

}
