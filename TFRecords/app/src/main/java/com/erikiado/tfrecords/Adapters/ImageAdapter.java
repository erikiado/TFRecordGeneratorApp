package com.erikiado.tfrecords.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.erikiado.tfrecords.ActivityClassGallery;
import com.erikiado.tfrecords.ActivityFullImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by e on 19/11/17.
 */

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private String className;
    private ArrayList<File> bitmapList;

    public ImageAdapter(Context context, ArrayList<File> bitmapList, String className) {
        this.context = context;
        this.className = className;
        this.bitmapList = bitmapList;
    }

    public int getCount() {
        return this.bitmapList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        final File img = this.bitmapList.get(position);
        Picasso.with(context).load(img).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ActivityFullImage.class);
                i.putExtra("file",img.getAbsolutePath());
                i.putExtra("parent",img.getParentFile().getAbsolutePath());
                i.putExtra("name",className);
                i.putExtra("position",position);
                context.startActivity(i);
//                context.startActivity(new Intent(context, Uri.parse(img.toURI().toString())));
            }
        });
//        imageView.setImageBitmap(this.bitmapList.get(position));
        return imageView;
    }

}