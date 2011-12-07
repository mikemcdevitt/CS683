package com.example.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLU;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OpenGLCubeActivity extends Activity {
	
	DrawingSurfaceView dsv;
	float x_down, y_down, x1, x2, y1, y2, x_diff, y_diff = 0;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.dsv = new DrawingSurfaceView(this);
		setContentView(this.dsv);
	}
	
	public void onResume()
	{
		super.onResume();
	}
	
	public void onRestart()
	{
		super.onRestart();
	}
	
	public void onPause()
	{
		super.onPause();
	}
	
	@Override 
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
		float x_dir = 1.0f;
		float y_dir = 1.0f;
		
		boolean moved = false;
		
		this.x1 = x2;
		this.y1 = y2;
     	this.x2 = e.getX();
        this.y2 = e.getY();
        this.x_diff = x2 - x1;
        this.y_diff = y2 - y1;
        if (x_diff < 0)
        	x_dir = -1.0f;
        if (y_diff < 0)
        	y_dir = -1.0f;

         if (e.getAction() == android.view.MotionEvent.ACTION_DOWN) {
        	 this.x_down = x2;
        	 this.y_down = y2;
        	 this.dsv.yrot_delta = 0.0f;
        	 this.dsv.xrot_delta = 0.0f;
        	 //moved = false;
          } else if (e.getAction() == android.view.MotionEvent.ACTION_MOVE ) {
        	 this.dsv.yrot += x_diff;
        	 this.dsv.xrot += y_diff;
        	 this.dsv.yrot_delta = 0.0f;
        	 this.dsv.xrot_delta = 0.0f;
        	 //moved = true;
          } else if (e.getAction() == android.view.MotionEvent.ACTION_UP) {
        	 x_diff = x2 - x_down;
        	 y_diff = y2 - y_down;
        	 double xsin = x_diff/(Math.sqrt(Math.pow(x_diff,2.0f) + Math.pow(y_diff,2.0f)));
        	 double ysin = y_diff/(Math.sqrt(Math.pow(x_diff,2.0f) + Math.pow(y_diff,2.0f)));
        	 this.dsv.xrot_delta = 1.0f;
        	 this.dsv.yrot_delta = 1.0f;
    		 int i = 0;

    		 Intent myIntent;
        	 if (moved == false)
        	 if ((x_diff) > 100 && (y_diff) < -100)
        	 {
        		 myIntent = new Intent(this, ScreenIndexTestActivity.class);
        		 startActivity(myIntent);
        	 }
        	 else if ((x_diff) > 100 && (y_diff) > 100)
        	 {
        		 myIntent = new Intent(this, PianoActivity.class);
        		 startActivity(myIntent);
        	 }else if ((x_diff) < -100 && (y_diff) < -100)
        	 {
        		 myIntent = new Intent(this, GuitarActivity.class);
        		 startActivity(myIntent);
        	 }else if ((x_diff) < -100 && (y_diff) > 100)
        	 {
        		 myIntent = new Intent(this, BassActivity.class);
        		 startActivity(myIntent);
        	 }
          }
         
         return true;
    }
    

	class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

		public SurfaceHolder mHolder;
		public float xrot = 0.0f; 
		public float yrot = 0.0f; 
		public float xrot_delta = 0.0f; 
		public float yrot_delta = 0.0f; 


		public DrawingThread mThread;

		public DrawingSurfaceView(Context c) {
			super(c);
			init();
		}

		public void init() {
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mThread = new DrawingThread();
			mThread.start();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mThread.waitForExit();
			mThread = null;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			mThread.onWindowResize(w, h);
		}

		class DrawingThread extends Thread {
			boolean stop;
			int w;
			int h;
			boolean changed = true;

			DrawingThread() {
				super();
				stop = false;
				w = 0;
				h = 0;
			}

			@Override
			public void run() {

				EGL10 egl = (EGL10) EGLContext.getEGL();
				EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
				int[] version = new int[2];
				egl.eglInitialize(dpy, version);

				int[] configSpec = {

				EGL10.EGL_RED_SIZE, 5, EGL10.EGL_GREEN_SIZE, 6,
						EGL10.EGL_BLUE_SIZE, 5, EGL10.EGL_DEPTH_SIZE, 16,
						EGL10.EGL_NONE

				};

				EGLConfig[] configs = new EGLConfig[1];
				int[] num_config = new int[1];
				egl.eglChooseConfig(dpy, configSpec, configs, 1, num_config);
				EGLConfig config = configs[0];

				EGLContext context = egl.eglCreateContext(dpy, config,
						EGL10.EGL_NO_CONTEXT, null);

				EGLSurface surface = null;
				GL10 gl = null;

				while (!stop) {
					int W, H;
					boolean updated;
					synchronized (this) {
						updated = this.changed;
						W = this.w;
						H = this.h;
						this.changed = false;
					}

					if (updated) {
						if (surface != null) {
							egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE,
									EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
							egl.eglDestroySurface(dpy, surface);
						}

						surface = egl.eglCreateWindowSurface(dpy, config,
								mHolder, null);
						egl.eglMakeCurrent(dpy, surface, surface, context);
						gl = (GL10) context.getGL();
						gl.glDisable(GL10.GL_DITHER);
						gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
								GL10.GL_FASTEST);
						gl.glClearColor(0, 0, 0, 0);
						gl.glEnable(GL10.GL_CULL_FACE);
						gl.glShadeModel(GL10.GL_SMOOTH);
						gl.glEnable(GL10.GL_DEPTH_TEST);
						gl.glViewport(0, 0, W, H);
						float ratio = (float) W / H;
						gl.glMatrixMode(GL10.GL_PROJECTION);
						gl.glLoadIdentity();
						gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
					}
					drawFrame(gl, 1280, 700);
					egl.eglSwapBuffers(dpy, surface);
					if (egl.eglGetError() == EGL11.EGL_CONTEXT_LOST) {
						Context c = getContext();
						if (c instanceof Activity) {
							((Activity) c).finish();
						}
					}
				}
				egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE,
						EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
				egl.eglDestroySurface(dpy, surface);
				egl.eglDestroyContext(dpy, context);
				egl.eglTerminate(dpy);
			}

			public void onWindowResize(int w, int h) {
				synchronized (this) {
					this.w = w;
					this.h = h;
					this.changed = true;
				}
			}

			public void waitForExit() {

				this.stop = true;
				try {
					join();
				} catch (InterruptedException ex) {
				}
			}
			
			protected FloatBuffer makeFloatBuffer(float[] arr) {  
				ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);  
				bb.order(ByteOrder.nativeOrder());  
				FloatBuffer fb = bb.asFloatBuffer();  
				fb.put(arr);  
				fb.position(0);  
				return fb;  
			} 

			private void drawFrame(GL10 gl, int w1, int h1) {
				 
				float mycube[] = {   
				// FRONT  
				-0.5f, -0.5f, 0.5f,  
				0.5f, -0.5f, 0.5f,  
				-0.5f, 0.5f, 0.5f,  
				0.5f, 0.5f, 0.5f,  
				// BACK  
				-0.5f, -0.5f, -0.5f,  
				-0.5f, 0.5f, -0.5f,  
				0.5f, -0.5f, -0.5f,  
				0.5f, 0.5f, -0.5f,  
				// LEFT  
				-0.5f, -0.5f, 0.5f,  
				-0.5f, 0.5f, 0.5f,  
				-0.5f, -0.5f, -0.5f,  
				-0.5f, 0.5f, -0.5f,  
				// RIGHT  
				0.5f, -0.5f, -0.5f,  
				0.5f, 0.5f, -0.5f,  
				0.5f, -0.5f, 0.5f,  
				0.5f, 0.5f, 0.5f,  
				// TOP  
				-0.5f, 0.5f, 0.5f,  
				0.5f, 0.5f, 0.5f,  
				-0.5f, 0.5f, -0.5f,  
				0.5f, 0.5f, -0.5f,  
				// BOTTOM 
				-0.5f, -0.5f, 0.5f,  
				-0.5f, -0.5f, -0.5f,  
				0.5f, -0.5f, 0.5f,  
				0.5f, -0.5f, -0.5f,  
				};  
				 
				FloatBuffer cubeBuff;   
				cubeBuff = makeFloatBuffer(mycube);   
				 
				gl.glEnable(GL10.GL_DEPTH_TEST);   
				 
				gl.glEnable(GL10.GL_CULL_FACE);  
				 
				gl.glDepthFunc(GL10.GL_LEQUAL);   
				 
				gl.glClearDepthf(1.0f);  
				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);  
				gl.glMatrixMode(GL10.GL_PROJECTION);  
				gl.glLoadIdentity();  
				gl.glViewport(0,0,w1,h1);  
				 
				GLU.gluPerspective(gl, 45.0f, ((float)w1)/h1, 1f, 100f);  
				 
				gl.glMatrixMode(GL10.GL_MODELVIEW);  
				gl.glLoadIdentity();  
				 
				GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);   
				 
				gl.glShadeModel(GL10.GL_SMOOTH);   
				 
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeBuff);  
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  
				 
				gl.glRotatef(xrot, 1, 0, 0);   
				gl.glRotatef(yrot, 0, 1, 0); 
				 
				gl.glColor4f(1.0f, 0, 0, 1.0f);  
				 
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);  
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);   
				 
				gl.glColor4f(0, 1.0f, 0, 1.0f);  
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);  
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);  
				gl.glColor4f(0, 0, 1.0f, 1.0f);  
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);  
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);  
				 
				xrot += xrot_delta;   
				yrot += yrot_delta;  
			}
		}
	}
}
