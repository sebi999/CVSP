package com.example.opencvtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import android.util.Log;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.JavaCameraView;

public class MainActivity extends Activity implements CvCameraViewListener {

	
    private static final String  TAG = "OpenCVTest";
	
    private CameraBridgeViewBase mOpenCvCameraView;
    
	    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	        @Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                {
	                    Log.i(TAG, "OpenCV loaded successfully");
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

	    public boolean onTouch(View view, MotionEvent event) {
	    
	        return false;
	    }

	    public Mat onCameraFrame(Mat inputFrame) {
	    	
//	    	Mat outputFrame = inputFrame.clone();
//	    	inputFrame.convertTo(inputFrame, CvType.CV_64FC3);
//	    	
//    	    int size = (int) (inputFrame.total() * inputFrame.channels());
//    	    double[] temp = new double[size];
//    	    //byte[] temp = new byte[size];
//    	    inputFrame.get(0, 0, temp);
//    	    for (int i = 0; i < size; i++)
//    	       temp[i] = (temp[i] / 2);
//    	    
//    	    outputFrame.put(0, 0, temp);
	    	
	    	
	        return inputFrame;
	    }
	
	}