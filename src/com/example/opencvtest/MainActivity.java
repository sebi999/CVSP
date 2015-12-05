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
import org.opencv.utils.Converters;
import org.opencv.android.CameraBridgeViewBase;
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
	        
	        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	        
            
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
	    
	    
	    public void onCameraViewStarted(int width, int height) {
	    }

	    public void onCameraViewStopped() {
	    }
	    
	    
	    
	    private boolean calcHist = false;
	    private int keyframeCount = 0;
	    
	    public boolean onTouch(View view, MotionEvent event) {
	    	calcHist = true;
	    	keyframeCount++;
	        return false;
	    }

	    

	    Mat bgHist;
	    Mat fgHist;
	    
	    public Mat onCameraFrame(CvCameraViewFrame cameraFrame) {
	    	
    		Mat inputFrame = cameraFrame.rgba();
    		Mat outputFrame = inputFrame.clone();

	    	Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_RGBA2RGB);
	    	Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_RGB2HSV);
	    	
	    	if(calcHist){
	    		calcHist = false;
	    		//if(!calcedHist)
	    		if(keyframeCount < 6){
	    			bgHist = calcBgHist(inputFrame);
	    			log("bgframes: " + (keyframeCount));
//			    	Imgproc.cvtColor(bgHist, bgHist, Imgproc.COLOR_GRAY2RGBA);
//			    	bgHist.convertTo(bgHist, CvType.CV_8UC4);
	    		}else if(keyframeCount<11){
//	    			if(fgMask == null){
//	    				fgMask = Mat.zeros(inputFrame.height(), inputFrame.width(), CvType.CV_8U);
//	    				Mat ones = Mat.ones((int)(inputFrame.height() * 0.5), (int)(inputFrame.width() * 0.5), CvType.CV_8U);
//	    				int startPixY = (int)((inputFrame.height() - ones.height()) * 0.5);
//	    				ones.copyTo(fgMask.submat(startPixY, startPixY + ones.height(), inputFrame.width()-ones.width(),   inputFrame.width()));
//	    			}
//
//	    			log("mask: " + fgMask.size() + " " + fgMask.channels() +" " + CvType.typeToString(fgMask.type()));
//	    			log("inputFrame: " + inputFrame.size() + " " + inputFrame.channels() );
	    			log("fgframes: " + (keyframeCount-5));
	    			fgHist = calcFgHist(inputFrame.submat((int)(inputFrame.height() * 0.25), (int)(inputFrame.height() * 0.75), (int)(inputFrame.width() * 0.25), inputFrame.width() ));
	    			
	    			if(keyframeCount==10){
	    				framesFg.release();
	    				framesBg.release();
	    				
	    			}
	    			
	    			
	    		}
		    	
	    	}

//	    	if(keyframeCount>0){
//	    		bgHist.copyTo(outputFrame.submat(outputFrame.height() - hbins, outputFrame.height(), 0,  sbins));
//	    	}
//	    	
	    	
	    	if(keyframeCount>=1){
    			List<Mat> frame = new ArrayList<Mat>();
    			Mat temp = new Mat();
    			inputFrame.copyTo(temp);
    			
    			frame.add(temp);
        		MatOfInt channels = new MatOfInt(1,2);
    			MatOfFloat hranges = new MatOfFloat( 0f, 180f, 0f, 256f );
    			Mat backProj = new Mat(inputFrame.size(), inputFrame.type());
    			
    			
    			double scale = 1d;
    			//Mat images_mat = Converters.vector_Mat_to_Mat(frame);
    			//log(images_mat.nativeObj+ " " + channels.nativeObj + " " + fgHist.nativeObj  + " " + backProj.nativeObj  + " " + hranges.nativeObj );
    			
    			Imgproc.calcBackProject(frame, channels, bgHist, backProj, hranges, scale);
//    			
//    			outputFrame = backProj;
    		}

	    	
	        return outputFrame;
	    }
	    
	    private Mat framesFg;
	    public Mat calcFgHist(Mat inputFrame){

	    	if(framesFg == null)
	    		framesFg = new Mat();
	    	
	    	framesFg.push_back(inputFrame);
    		
    		List<Mat> framesList = new ArrayList<Mat>();
    		framesList.add(framesFg);
	    	return calcHist(framesList);
	    }

	    private Mat framesBg;
	    public Mat calcBgHist(Mat inputFrame){
	    	
	    	if(framesBg == null)
	    		framesBg = new Mat();
	    	
	    	framesBg.push_back(inputFrame);
    		
    		List<Mat> framesList = new ArrayList<Mat>();
    		framesList.add(framesBg);
	    	return calcHist(framesList);
	    }


	    private int hbins = 30;
	    private int sbins = 32;
	    
	    public Mat calcHist(List<Mat> framesList){

//	    	Mat frame = new Mat();
//	    	inputFrame.copyTo(frame);
//    		
//	    	if(frames == null)
//	    		frames = new Mat();
//	    	
//    		frames.push_back(frame);
//    		
//    		List<Mat> framesList = new ArrayList<Mat>();
//    		framesList.add(frames);

    		MatOfInt channels = new MatOfInt(1,2);
			MatOfInt histSize = new MatOfInt(hbins, sbins);
			MatOfFloat hranges = new MatOfFloat( 0f, 180f, 0f, 256f );
			Mat hist = new Mat();

			Imgproc.calcHist(framesList, channels, new Mat(), hist, histSize, hranges, true);
			Core.normalize(hist, hist, 0, 255, Core.NORM_MINMAX, -1);
			
			
	    	//log("hist: " +  CvType.typeToString(hist.type()) + " " + hist.toString() + " " + hist.total());
	    	
			hist.convertTo(hist, CvType.CV_8UC1);
			
    		return hist;
	    }
	    
	    private static void log(String msg){
	    	Log.i(TAG, msg);
	    	
	    	
	    }

	    
	
	}