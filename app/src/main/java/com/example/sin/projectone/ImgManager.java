package com.example.sin.projectone;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by nanth on 12/2/2016.
 */

public class ImgManager {

    private static ImgManager imgManager;

    private ImgManager(){

    }

    public static ImgManager getInstance(){
        if(imgManager==null){
            imgManager = new ImgManager();
        }
        return imgManager;
    }

    public Bitmap loadImageFromStorage(String imgName){
        String path = ApplicationHelper.getAppContext().getApplicationInfo().dataDir+"/app_"+Constant.FOLDER_PHOTO;
        System.out.println("Path :" + path);
        try {
            File f=new File(path, imgName);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            return bitmap;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean checkImageName(String imgName){
        File file = new File(ApplicationHelper.getAppContext().getApplicationInfo().dataDir+"/app_"+Constant.FOLDER_PHOTO+"/"+imgName);
        if(file.exists())
            return true;
//Do something
        else
            return  false;
// Do something else.
    }

    public File saveImgToInternalStorage(Bitmap bitmapImage,String imgName){
        ContextWrapper cw = new ContextWrapper(ApplicationHelper.getAppContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(Constant.FOLDER_PHOTO, Context.MODE_PRIVATE);
        // Create imageDir
        File imgFile = new File(directory,imgName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgFile);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //return directory.getAbsolutePath();
        return imgFile;
    }

    public File saveImgURIToInternalStorage(Uri imgUri, String imgName,Context context){
        ContextWrapper cw = new ContextWrapper(ApplicationHelper.getAppContext());
        final int chunkSize = 1024;
        byte[] imageData = new byte[chunkSize];

        // I'll assume this is a Context and bitmap is a Bitmap

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(Constant.FOLDER_PHOTO, Context.MODE_PRIVATE);
        // Create imageDir
        File imgFile = new File(directory,imgName);

        FileOutputStream fos = null;
        try {
            InputStream in = context.getContentResolver().openInputStream(imgUri);
            fos = new FileOutputStream(imgFile);
            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                fos.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //return directory.getAbsolutePath();
        return imgFile;
    }



//    public void dispatchTakePictureIntent(Fragment fragment) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
//            fragment.startActivityForResult(takePictureIntent, Constant.REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//    public void dispatchTakePictureIntent(Activity activity) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
//            activity.startActivityForResult(takePictureIntent, Constant.REQUEST_IMAGE_CAPTURE);
//        }
//    }


}
