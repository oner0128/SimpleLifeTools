package com.hrong.simplelifetools.camera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.afollestad.materialcamera.MaterialCamera;
import com.hrong.simplelifetools.R;

import java.io.File;

/**
 * Created by hrong on 2017/2/18.
 */

public class OpenCamera {


    public OpenCamera() {
    }

    public static MaterialCamera createCamera(Activity activity) {
        File saveDir = null;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "SimpleLifeTools/MaterialCamera");
            saveDir.mkdirs();
        }
        Log.v("StorageDirectory", Environment.getExternalStorageDirectory().toString());
        Log.v("ExternalStorageState", Environment.getExternalStorageState());
        Log.v("saveDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
        Boolean permission = (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        Log.v("saveDir", Environment.getRootDirectory().toString());
        Log.v("saveDir", Environment.getDataDirectory().toString());
        Log.v("saveDir", permission.toString());

        MaterialCamera materialCamera = new MaterialCamera(activity)
                .saveDir(saveDir)
                .showPortraitWarning(true)
                .allowRetry(true)
                .allowRetry(true)
                .autoSubmit(false)
                .labelConfirm(R.string.mcam_use_video);
        return materialCamera;
    }
}
