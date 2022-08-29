package com.example.mynicecamera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements Serializable {
    GridView gridView;
    MyDatabase database = new MyDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        gridView = findViewById(R.id.gridview);
        MediaFileAdapter adapter = new MediaFileAdapter(this,R.layout.image_view, (ArrayList<MediaFile>) database.getThePics());

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MediaFile mediaFile = (MediaFile) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(HistoryActivity.this,ImageInfo.class);
                intent.putExtra("mediaFile", (Serializable) mediaFile);
                startActivity(intent);
                adapter.notifyDataSetChanged();
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        gridView = findViewById(R.id.gridview);
        MediaFileAdapter adapter = new MediaFileAdapter(this,R.layout.image_view, (ArrayList<MediaFile>) database.getThePics());
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}