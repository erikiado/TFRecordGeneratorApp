package com.erikiado.tfrecords;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.erikiado.tfrecords.Adapters.ImageAdapter;

import org.opencv.core.Scalar;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class ActivityClassGallery extends AppCompatActivity {

    private GridView imageGrid;
    private ArrayList<File> bitmapList;
    private static final int  PERM_INTERNET_INT = 11;
    private int numberImages;
    private File[] files;
    private BitmapFactory.Options bmOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_gallery);
        this.imageGrid = (GridView) findViewById(R.id.gridview);
        this.bitmapList = new ArrayList<File>();


        String className = getIntent().getStringExtra("className");
        File sdRoot = Environment.getExternalStorageDirectory();
        String baseDir = "/tfd/";
        File root = new File(sdRoot, baseDir + className + "/");
        bmOptions = new BitmapFactory.Options();
        files = root.listFiles();
        SharedPreferences sp = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        numberImages = sp.getInt(className,0);
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(ActivityClassGallery.this,
//                Manifest.permission.INTERNET)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(ActivityClassGallery.this,
//                    new String[]{Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE},
//                    PERM_INTERNET_INT);
//        } else{
//            fillWithPlaceholders();
//        }

        fillWithLocal();

        this.imageGrid.setAdapter(new ImageAdapter(this, this.bitmapList, className));

    }

    private Bitmap urlImageToBitmap(String imageUrl) throws Exception {
        Bitmap result = null;
        URL url = new URL(imageUrl);
        if(url != null) {
            result = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }
        return result;
    }

//    private void fillWithPlaceholders(){
//        try {
//            for(int i = 0; i < 10; i++) {
//                this.bitmapList.add(urlImageToBitmap("http://placehold.it/150x150"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void fillWithLocal(){
        for (int i = 0; i < files.length; i++){
            if(files[i].getName().contains(".jpg")){
//                Bitmap bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath(),bmOptions);
                this.bitmapList.add(files[i]);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERM_INTERNET_INT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    fillWithPlaceholders();
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

