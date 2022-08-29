package com.example.mynicecamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageInfo extends AppCompatActivity implements Serializable {
    ImageView imageView;
    EditText myDate, myLocation, myNote;
    Button save_btn, delete_btn,update_btn;
    MyDatabase myDatabase = new MyDatabase(this);
    private FusedLocationProviderClient fusedLocationClient;
    private static  final int MY_PERMISSIONS = 1;
    Location location;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_config);

        Intent intent = getIntent();



        imageView = findViewById(R.id.imageView2);
        myDate = findViewById(R.id.date_text);
        myLocation = findViewById(R.id.location_text);
        myNote = findViewById(R.id.note_text);
        save_btn = findViewById(R.id.save_btn);
        delete_btn = findViewById(R.id.delete_btn);
        update_btn = findViewById(R.id.update_btn);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(intent.getStringExtra("isForUpdate") != null) {
            update_btn.setEnabled(false);
            update_btn.setVisibility(View.INVISIBLE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS);
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    ImageInfo.this.location = location;
                }
            });

            String photoPath = intent.getStringExtra("my_pic");
            Bitmap myBitmap = BitmapFactory.decodeFile(photoPath);
            try {
                imageView.setImageBitmap(needsRotate(photoPath,myBitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }

            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNewMediaFile();
                }
            });
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String filepath = intent.getStringExtra("my_pic");
                    File myFile  = new File(filepath);
                    myFile.delete();
                    finish();
                    Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent1);
                    finish();

                }
            });
        }
        else{
            save_btn.setVisibility(View.INVISIBLE);
            save_btn.setEnabled(false);
            myDate.setVisibility(View.VISIBLE);
            myLocation.setVisibility(View.VISIBLE);
            MediaFile mediaFile = (MediaFile) intent.getSerializableExtra("mediaFile");
            myDate.setText(mediaFile.getDate());
            myLocation.setText(mediaFile.getAddress());
            myNote.setText(mediaFile.getNote());

            Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getPhotoPath());
            try {
                imageView.setImageBitmap(needsRotate(mediaFile.getPhotoPath(),myBitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }


            update_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaFile newMediaFile = new MediaFile(myDate.getText().toString(),mediaFile.getAddress(),mediaFile.getLatitude(),mediaFile.getLongitude(),myNote.getText().toString(),mediaFile.getPhotoPath());
                    myDatabase.onUpdate(newMediaFile,mediaFile.getId());
                    finish();
                    Intent intent1 = new Intent(getApplicationContext(),HistoryActivity.class);
                    startActivity(intent1);

                }
            });

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDatabase.deleteOne(mediaFile);
                    File myFile  = new File(mediaFile.getPhotoPath());
                    myFile.delete();
                    finish();
                    Intent intent1 = new Intent(getApplicationContext(),HistoryActivity.class);
                    startActivity(intent1);

                }
            });
        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                } else {
                    return;
                }
            }
            break;
        }
    }
    private void createNewMediaFile(){
        String mediaPath = getIntent().getStringExtra("my_pic");
        if(isNetworkConnected()){
        Geocoder geocoder = new Geocoder(ImageInfo.this);
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            String locality = addressList.get(0).getLocality();
            MediaFile mediaFile = new MediaFile(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),locality,latLng.latitude,latLng.longitude,myNote.getText().toString(),mediaPath);
            myDatabase.addOne(mediaFile);
            setResult(0,getIntent());
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }}
        else{
            MediaFile mediaFile = new MediaFile(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),"No internet",0,0,myNote.getText().toString(),mediaPath);
            myDatabase.addOne(mediaFile);
            setResult(0,getIntent());
            finish();
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap needsRotate(String photoPath,Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateBitmap(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateBitmap(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateBitmap(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


}