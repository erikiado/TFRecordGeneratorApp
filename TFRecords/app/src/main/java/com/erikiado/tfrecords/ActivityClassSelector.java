package com.erikiado.tfrecords;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.erikiado.tfrecords.Adapters.ClassItemAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityClassSelector extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_selector);

        SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // Lookup the recyclerview in activity layout
        Set clases = sp.getStringSet("clases", new HashSet<String>());

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recycler_gallery);

        // Initialize contacts
        List<String> contacts = new ArrayList<>();
        contacts.addAll(clases);
//                Contact.createContactsList(20);
        // Create adapter passing in the sample user data
        ClassItemAdapter adapter = new ClassItemAdapter(this, contacts);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

    }
}
