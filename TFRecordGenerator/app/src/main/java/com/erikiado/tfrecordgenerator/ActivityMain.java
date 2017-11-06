package com.erikiado.tfrecordgenerator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;

public class ActivityMain extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2  {

    private static final String TAG = "OCVSample::Activity";
    private int w, h;
    private int xLastTouch, yLastTouch;
    private int xPreTouch, yPreTouch;
    private CameraBridgeViewBase mOpenCvCameraView;
    private TextView tvName;
    private boolean last;
    private String className;
    private Button classNameButton;
    private Context context;
//    Scalar RED = new Scalar(255, 0, 0);
//    Scalar GREEN = new Scalar(0, 255, 0);
//    FeatureDetector detector;
//    DescriptorExtractor descriptor;
//    DescriptorMatcher matcher;
//    Mat descriptors2,descriptors1;
//    Mat img1;
//    MatOfKeyPoint keypoints1,keypoints2;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    try {
                        initializeOpenCVDependencies();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void initializeOpenCVDependencies() throws IOException {
        mOpenCvCameraView.enableView();
//        detector = FeatureDetector.create(FeatureDetector.ORB);
//        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
//        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//        img1 = new Mat();
//        AssetManager assetManager = getAssets();
//        InputStream istr = assetManager.open("a.jpeg");
//        Bitmap bitmap = BitmapFactory.decodeStream(istr);
//        Utils.bitmapToMat(bitmap, img1);
//        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY);
//        img1.convertTo(img1, 0); //converting the image to match with the type of the cameras image
//        descriptors1 = new Mat();
//        keypoints1 = new MatOfKeyPoint();
//        detector.detect(img1, keypoints1);
//        descriptor.compute(img1, keypoints1, descriptors1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;
        setContentView(R.layout.activity_main);
        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        tvName = (TextView) findViewById(R.id.class_name);
        classNameButton = (Button) findViewById(R.id.edit_name);
        xLastTouch = 1920;
        yLastTouch = 1080;
        xPreTouch = 0;
        yPreTouch = 0;
        last = false;
        className = "Clase Prueba";
        tvName.setText(className);
        classNameButton.setText(className);
        classNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(context);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(et)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String input = et.getText().toString();
                                className = input;
                                tvName.setText(className);
                                classNameButton.setText(className);
                            }
                        });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
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


    @Override
    public void onCameraViewStarted(int width, int height) {
        w = width;
        h = height;
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mRgba = inputFrame.rgba();

//        rectangle(mRgba, contoursArray.tl(), contoursArray.br(), (255, 0, 0, 255), 3); //error:Cannot invoke tl() on the array type Rect[]、Cannot invoke br() on the array type Rect[]
//        Rect r = new Rect(100,100,100,100);//
//
        int xRectLeftUp, yRectLeftUp;
        int xRectRightDown, yRectRightDown;
        if(last){
            xRectLeftUp = xLastTouch;
            yRectLeftUp = yLastTouch;
            xRectRightDown = xPreTouch;
            yRectRightDown = yPreTouch;
        }else{
            xRectLeftUp = xPreTouch;
            yRectLeftUp = yPreTouch;
            xRectRightDown = xLastTouch;
            yRectRightDown = yLastTouch;
        }
        Imgproc.rectangle(mRgba,new Point(xRectLeftUp,yRectLeftUp),new Point(xRectRightDown,yRectRightDown),new Scalar(0,255,0),4);
        Imgproc.GaussianBlur(mRgba,mRgba,new Size(11,11),0);
        Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_BGR2HSV);
        return mRgba;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)(event.getX()*1080)/1440;
        int y = (int)(event.getY()*1920)/2560;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(last){
                    last = false;
                }else{
                    last = true;
                }
                xPreTouch = xLastTouch;
                yPreTouch = yLastTouch;
                xLastTouch = x;
                yLastTouch = y;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                xLastTouch = x;
                yLastTouch = y;
        }
        return false;
    }
}
