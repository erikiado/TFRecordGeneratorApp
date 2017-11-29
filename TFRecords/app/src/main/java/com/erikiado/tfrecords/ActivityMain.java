package com.erikiado.tfrecords;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActivityMain extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2  {

    private static final String TAG = "OCVSample::Activity";
    private int w, h;
    private int xLastTouch, yLastTouch;
    private int xPreTouch, yPreTouch;
    private CameraBridgeViewBase mOpenCvCameraView;
    private TextView tvName;
    private boolean last;
    private String className;
    private Button classNameButton, galleryButton, recordButton;
    private Context context;
    private int cont;
    private int frameWidth, frameHeight;
    private boolean recording;
    private boolean first;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Set classes;
    private static final int  PERM_CAMERA_INT = 12;
    private File sdRoot;
    private String baseDir;
    private int classCount;
    private Mat mOriginalImg;
    private OutputStreamWriter out;
    private FileOutputStream fileWriter;
    private Scalar minValues, maxValues;
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

        // Here, thisActivity is the current activity
        if ((ContextCompat.checkSelfPermission(ActivityMain.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(ActivityMain.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(ActivityMain.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(ActivityMain.this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERM_CAMERA_INT);
        }

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        tvName = (TextView) findViewById(R.id.class_name);
        classNameButton = (Button) findViewById(R.id.edit_name);
        galleryButton = (Button) findViewById(R.id.gallery_btn);
        recordButton = (Button) findViewById(R.id.record_btn);
        recording = false;
        classCount = 0;
        sdRoot = Environment.getExternalStorageDirectory();
        baseDir = "/tfd/";
        first = true;
        xLastTouch = frameWidth = 1920;
        yLastTouch = frameHeight = 1080;
        xPreTouch = 0;
        yPreTouch = 0;
        last = false;
        cont = 0;
        minValues = new Scalar(29, 2, 6);//Green
        maxValues = new Scalar(255, 255, 255);//Green
//        minValues = new Scalar(57, 68, 0); //Blue
//        maxValues = new Scalar(151, 255, 255); //Blue

        sp = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        classes = sp.getStringSet("clases", new HashSet<String>());
        editor = sp.edit();
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
                                classes.add(normalizeClassName(className));
                                classCount = sp.getInt(normalizeClassName(className),0);
                                editor.putStringSet("clases", classes).apply();
                                File mkDir = new File(sdRoot, baseDir + normalizeClassName(className) + "/");
                                mkDir.mkdirs();
                                File classFile = new File(sdRoot, baseDir + normalizeClassName(className) + "/" + normalizeClassName(className) + ".txt");
                                try {
//                                    classFile.
//                                    out = new OutputStreamWriter(openFileOutput(className + ".txt", Context.MODE_APPEND));
                                    fileWriter = new FileOutputStream(classFile,true);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recording){
                    recordButton.setTextColor(getResources().getColor(R.color.recording_on));
                    recording = true;
                }else{
                    recordButton.setTextColor(getResources().getColor(R.color.recording_off));
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recording = false;
                }
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityMain.this, ActivityClassSelector.class);
                startActivity(i);
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

    public String normalizeClassName(String s){
            return s.replace(" ","_").toLowerCase();
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
        Imgproc.cvtColor(mRgba,mRgba, Imgproc.COLOR_RGBA2BGR);

        if (first){
            frameHeight = mRgba.height();
            frameWidth = mRgba.width();
            first = false;
        }

//        rectangle(mRgba, contoursArray.tl(), contoursArray.br(), (255, 0, 0, 255), 3); //error:Cannot invoke tl() on the array type Rect[]、Cannot invoke br() on the array type Rect[]
//        Rect r = new Rect(100,100,100,100);//
//
        int xRectLeftUp, yRectLeftUp;
        int xRectRightDown, yRectRightDown;
//        Mat small = new Mat();//.reshape(600);
//        Imgproc.resize(mRgba, small, new Size((mRgba.cols()*0.5), (int)(mRgba.rows()*0.5)));
//        Mat blurred = new Mat();
//        Mat hsvImage = new Mat();
//        Mat mask = new Mat();
//        Imgproc.GaussianBlur(small,blurred,new Size(11,11),0);
//        Imgproc.cvtColor(blurred,hsvImage, Imgproc.COLOR_BGR2HSV);
//        Core.inRange(hsvImage,minValues,maxValues,mask);
//
//        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
//        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
//
//        Imgproc.erode(mask,mask,erodeElement);
//        Imgproc.erode(mask,mask,erodeElement);
//        Imgproc.dilate(mask,mask,dilateElement);
//        Imgproc.dilate(mask,mask,dilateElement);

        // init
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//        if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
//        {
//            // for each contour, display it in blue
//            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
//            {
//                Imgproc.drawContours(small, contours, idx, new Scalar(250, 0, 0));
//            }
//        }

        //        Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_BGR2HSV);



//        Mat samples = mRgba.reshape(1, mRgba.cols() * mRgba.rows());
//        Mat samples32f = new Mat();
//        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
//        Mat labels = new Mat();
//        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
//        Mat centers = new Mat();
//        Core.kmeans(samples32f, 2, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);
//        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
//        centers.reshape(3);
//        List<Mat> clusters = new ArrayList<Mat>();
//        for(int i = 0; i < centers.rows(); i++) {
//            clusters.add(Mat.zeros(mRgba.size(), mRgba.type()));
//        }
//        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
//        for(int i = 0; i < centers.rows(); i++) counts.put(i, 0);
//        int rows = 0;
//        for(int y = 0; y < mRgba.rows(); y++) {
//            for(int x = 0; x < mRgba.cols(); x++) {
//                int label = (int)labels.get(rows, 0)[0];
//                int r = (int)centers.get(label, 2)[0];
//                int g = (int)centers.get(label, 1)[0];
//                int b = (int)centers.get(label, 0)[0];
//                counts.put(label, counts.get(label) + 1);
//                clusters.get(label).put(y, x, b, g, r);
//                rows++;
//            }
//        }




        if(cont != 0){
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
            Scalar color;
            if(recording){
                color = new Scalar(255,0,0);
                if(cont == 1){
                    if(className != "Clase Prueba"){
                        mOriginalImg = mRgba.clone();
//                        mOriginalImg.set
//                        Imgproc.cvtColor(mOriginalImg,mOriginalImg, Imgproc.COLOR_RGBA2BGR);
//                        Mat bgrMat = Imgcodecs.imdecode(mOriginalImg, Imgcodecs.IMREAD_COLOR);
                        String fileName = normalizeClassName(className) + "_" + classCount + ".jpg";

//                        File pictureFile = new File(sdRoot, baseDir + className + "/" + fileName);

                        Imgcodecs.imwrite("/sdcard/" + baseDir + normalizeClassName(className) + "/" + fileName,mOriginalImg);
                        editor.putInt(normalizeClassName(className),classCount).apply();
                        String currentRow = "" + classCount + "," + className + "," + getCurrentSquare() +"\n";
                        try {
                            fileWriter.write(currentRow.getBytes());

//                            out.write(currentRow);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        classCount++;
                    }
                }
            }else{
                color = new Scalar(0,255,0);
            }
            Imgproc.rectangle(mRgba,new Point(xRectLeftUp,yRectLeftUp),new Point(xRectRightDown,yRectRightDown),color,4);
        }

        cont++;
        cont = cont % 5;



//        Imgproc.GaussianBlur(mRgba,mRgba,new Size(11,11),0);
//        Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_BGR2HSV);
        return mRgba;

    }

    public String getCurrentSquare(){
        float xRectLeftUp, yRectLeftUp;
        float xRectRightDown, yRectRightDown;

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

//        xRectLeftUp = xRectLeftUp/frameWidth;
//        yRectLeftUp = yRectLeftUp/frameHeight;
//        xRectRightDown = xRectRightDown/frameWidth;
//        yRectRightDown = yRectRightDown/frameHeight;

        return "" + xRectLeftUp + "," + yRectLeftUp + "," + xRectRightDown + "," + yRectRightDown;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)(event.getX()*frameHeight)/1440;
        int y = (int)(event.getY()*frameWidth)/2560;

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERM_CAMERA_INT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
