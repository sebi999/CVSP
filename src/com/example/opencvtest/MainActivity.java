package com.example.opencvtest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import android.util.Log;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.JavaCameraView;

public class MainActivity extends Activity implements CvCameraViewListener2, View.OnTouchListener {

	
    private static final String  TAG = "OpenCVTest";
	
    private CameraBridgeViewBase mOpenCvCameraView;
    
	    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	        @Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                {
	                    Log.i(TAG, "OpenCV loaded successfully");
	                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
	                    mOpenCvCameraView.enableView();
	                } break;
	                default:
	                {
	                    super.onManagerConnected(status);
	                } break;
	            }
	        }
	    };

	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	        Log.d(TAG, "Creating and setting view");
	        mOpenCvCameraView = (CameraBridgeViewBase) new JavaCameraView(this, -1);
	        mOpenCvCameraView.enableFpsMeter();
	        mOpenCvCameraView.setMaxFrameSize(320, 240);
	        setContentView(mOpenCvCameraView);
	        mOpenCvCameraView.setCvCameraViewListener(this);
	    }

	    @Override
	    public void onPause()
	    {
	        super.onPause();
	        if (mOpenCvCameraView != null)
	            mOpenCvCameraView.disableView();
	    }

	    @Override
	    public void onResume()
	    {
	        super.onResume();
	        if (!OpenCVLoader.initDebug()) {
	            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
	            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
	        } else {
	            Log.d(TAG, "OpenCV library found inside package. Using it!");
	            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
	        }
	    }

	    public void onDestroy() {
	        super.onDestroy();
	        if (mOpenCvCameraView != null)
	            mOpenCvCameraView.disableView();
	    }
	    
	    
//	    @Override
//	    public void onResume()
//	    {
//	        super.onResume();
//	        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
//	    }
	    
	    public void onCameraViewStarted(int width, int height) {
	    }

	    public void onCameraViewStopped() {
	    }
	    
	    
	    
	    private boolean calcHist = false;
	    private boolean calcedHist = false;
	    
	    public boolean onTouch(View view, MotionEvent event) {
    		Log.i(TAG, "touch");
	    	calcHist = true;
	    	calcedHist = true;
	        return false;
	    }

	    public Mat onCameraFrame(Mat inputFrame) {
	    	
    		//Mat inputFrame = cameraFrame.rgba();

	    	if(calcHist){
		    	calcHist(inputFrame);
	    		calcHist = false;
	    	}
        
	        return inputFrame;
	    }
	    

	    Mat hist;
	    
	    public Mat onCameraFrame(CvCameraViewFrame cameraFrame) {
	    	
    		Mat inputFrame = cameraFrame.rgba();
    		Mat outputFrame = inputFrame.clone();
    		
	    	if(calcHist){
	    		calcHist = false;
	    		
		    	
		    	hist = calcHist(inputFrame);
		    	
		    	Imgproc.cvtColor(hist, hist, Imgproc.COLOR_GRAY2RGBA);
		    	hist.convertTo(hist, CvType.CV_8UC4);
		    	
		    	Log.i(TAG, "hist: " +  CvType.typeToString(hist.type()) + " " + hist.height() + " " + hist.width());
		    	Log.i(TAG, "outputFrame: " +  CvType.typeToString(outputFrame.type()) + " " + outputFrame.height() + " " + outputFrame.width());
		    	Log.i(TAG, "inputFrame: " +  CvType.typeToString(inputFrame.type()));
		    	
	    	}

	    	if(calcedHist){

		    	hist.copyTo(outputFrame.submat(outputFrame.height() - hbins, outputFrame.height(), 0,  sbins));
	    	}

	    	
	        return outputFrame;
	    }
	    
	    private int hbins = 30;
	    private int sbins = 32;
	    
	    public Mat calcHist(Mat inputFrame){

	    		Log.i(TAG, "calchist");
		    	//Mat outputFrame = inputFrame.clone();
		    	//inputFrame.convertTo(inputFrame, CvType.CV_8UC4);
		    	
//	    	    int size = (int) (inputFrame.total() * inputFrame.channels());
//	    	    double[] temp = new double[size];
//	    	    //byte[] temp = new byte[size];
//	    	    //int[] temp = new int[size];
//	    	    
//	    	    inputFrame.get(0, 0, temp);
//	    	    for (int i = 0; i < size; i++)
//	    	       temp[i] = (temp[i] / 2);
//	    	    
//				outputFrame.put(0, 0, temp);

		    	Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_RGBA2RGB);
		    	
		    	Mat hsv = new Mat();

		    	Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_RGB2HSV);
		    	
	    		List<Mat> frames = new ArrayList<Mat>();
	    		
	    		frames.add(inputFrame);
	    		

	    		MatOfInt channels = new MatOfInt(0, 1);
				MatOfInt histSize = new MatOfInt(hbins, sbins);
				MatOfFloat hranges = new MatOfFloat( 0f, 180f, 0f, 256f );
	    		
	    		
//
//	    		MatOfInt channels = new MatOfInt(0, 1, 2, 3);
//				MatOfInt histSize = new MatOfInt(256, 4, 1, 1);
//				MatOfFloat hranges = new MatOfFloat( 0f, 255f, 0f, 255f, 0f, 255f, 0f, 255f );
	    		
//	    		MatOfInt channels = new MatOfInt(0);
//				MatOfInt histSize = new MatOfInt(256);
//				MatOfFloat hranges = new MatOfFloat( 0f, 255f);

	    		Mat hist = new Mat();
				Imgproc.calcHist(frames, channels, new Mat(), hist, histSize, hranges);
	    		
				int size = (int)hist.total();
				
				
	    		Log.i(TAG, hist.size() + "");
	    		Log.i(TAG, hist.total() + "");
	    		Log.i(TAG, hist.dims() + "");
	    	
	    		return hist;
	    }

	    
	    
	
	}