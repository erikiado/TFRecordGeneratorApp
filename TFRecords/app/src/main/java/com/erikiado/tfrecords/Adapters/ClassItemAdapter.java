package com.erikiado.tfrecords.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.erikiado.tfrecords.ActivityClassGallery;
import com.erikiado.tfrecords.DataPart;
import com.erikiado.tfrecords.R;
import com.erikiado.tfrecords.RequestSingleton;
import com.erikiado.tfrecords.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by e on 19/11/17.
 */

public class ClassItemAdapter extends
        RecyclerView.Adapter<ClassItemAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<String> mContacts;
    // Store the context for easy access
    private Context mContext;

    private File[] files;
    private BitmapFactory.Options bmOptions;

    // Pass in the contact array into the constructor
    public ClassItemAdapter(Context context, List<String> contacts) {
        mContacts = contacts;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;
        public Button uploadButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.class_name_text);
            messageButton = (Button) itemView.findViewById(R.id.class_item_button);
            uploadButton = (Button) itemView.findViewById(R.id.class_item_upload_button);
        }
    }


    @Override
    public ClassItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_class_gallery, parent, false);

        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(contactView);
        viewHolder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ActivityClassGallery.class);
                i.putExtra("className",viewHolder.nameTextView.getText().toString());
                getContext().startActivity(i);
            }
        });
        viewHolder.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView mTxtDisplay;
                ImageView mImageView;
                mTxtDisplay = (TextView) viewHolder.nameTextView;
                String url = getContext().getResources().getString(R.string.server_url) + "test/post";
                HashMap hashMap = new HashMap();

                File sdRoot = Environment.getExternalStorageDirectory();
                String baseDir = "/tfd/";
                String className = viewHolder.nameTextView.getText().toString();
                File root = new File(sdRoot, baseDir + className + "/");
                bmOptions = new BitmapFactory.Options();
                files = root.listFiles();
                File dir = new File(root.getAbsolutePath()+"/"+className+".txt");
                try {
                    FileInputStream fileInputStream = new FileInputStream(dir);
                    DataInputStream in = new DataInputStream(fileInputStream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    int cont = 0;
                    while ((line = br.readLine()) != null) {
                        // do something with the line you just read, e.g.
                        if(line.split(",")[0].equals(String.valueOf(cont))) {
                            hashMap.put(String.valueOf(cont), line);
                            cont++;
                        }
//                temp2 = line;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int cont = 0;
                for (File f:files) {
                    if(f.getName().contains(".jpg")){
                        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
                        uploadBitmap(bitmap, f, (String) hashMap.get(String.valueOf(cont)));
//                        break;
                    }
                }
            }
        });
        return viewHolder;

    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ClassItemAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        String contact = mContacts.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(contact);
        Button button = viewHolder.messageButton;
//        button.setText(contact.isOnline() ? "Message" : "Offline");
//        button.setEnabled(contact.isOnline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap, final File f, final String csvRow) {

        //getting the tag from the edittext
        final String tags = csvRow.split(",")[1];//editTextTags.getText().toString().trim();
        String url = getContext().getResources().getString(R.string.server_url) + "test/post";

//        JSONObject jsonBody = null;
//        try {
//            jsonBody = new JSONObject("{\"type\":\"example\"}");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("clase", tags);
                params.put("csv_row",csvRow);
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(f.getName() , getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
//        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
        RequestSingleton.getInstance(getContext()).addToRequestQueue(volleyMultipartRequest);

    }
}