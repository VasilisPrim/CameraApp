package com.example.mynicecamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.Serializable;

public class MediaFile implements Serializable {
    private String date;
    private String address;
    private double latitude;
    private double longitude;
    private String note;
    private String photoPath;
    private  int id;

    public MediaFile(String date, String address, double latitude, double longitude, String note, String photoPath, int id) {
        this.date = date;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;
        this.photoPath = photoPath;
        this.id = id;
    }

    public MediaFile(String date, String address, double latitude, double longitude, String note, String photoPath) {
        this.date = date;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }
    public String getDate() {
        return date;
    }
    public String getAddress() {
        return address;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getNote() {
        return note;
    }
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "date='" + date + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", note='" + note + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", id=" + id +
                '}';
    }
}
