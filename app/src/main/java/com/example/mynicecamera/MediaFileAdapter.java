package com.example.mynicecamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class MediaFileAdapter extends ArrayAdapter<MediaFile> implements Serializable {
    private final Context mContext;
    private  final ArrayList<MediaFile> mediaFiles;

    public MediaFileAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MediaFile> mediaFiles) {
        super(context, resource, mediaFiles);
        this.mContext = context;
        this.mediaFiles = new ArrayList<MediaFile>();
        this.mediaFiles.addAll(mediaFiles);
    }

    public Context getContext() {
        return mContext;
    }
    @Override
    public int getCount() {
        return mediaFiles.size();
    }

    @Override
    public MediaFile getItem(int i) {
        return mediaFiles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final MediaFile mediaFile = mediaFiles.get(i);


        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.image_view, null);

            final ImageView imageViewPic = (ImageView)view.findViewById(R.id.imageView);
            final ViewHolder viewHolder = new ViewHolder(imageViewPic) ;
            view.setTag(viewHolder);

        }
        final ViewHolder viewHolder = (ViewHolder)view.getTag();
        int targetW = 250;
        int targetH = 250;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
       BitmapFactory.decodeFile(mediaFile.getPhotoPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mediaFile.getPhotoPath(), bmOptions);
        try {
            viewHolder.imageViewPicture.setImageBitmap(needsRotate(mediaFile.getPhotoPath(),bitmap));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
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
    private class ViewHolder {
        private final ImageView imageViewPicture;
        public ViewHolder(ImageView imageViewPicture) {
            this.imageViewPicture = imageViewPicture;

        }
    }
}
