package com.squeezer.android.permission_micro.storage;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adnen on 24/01/16.
 */
public class ExternalStorage {

    public static final String SD_CARD = "sdCard";
    public static final String EXTERNAL_SD_CARD = "externalSdCard";
    private static final String TAG = "ExternalStorage";

    public static String state = Environment.getExternalStorageState();


    /**
     * @return True if the external storage is available. False otherwise.
     */
    public static boolean isAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    /**
     * @return True if the external storage is writable. False otherwise.
     */
    public static boolean isWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;

    }

    /**
     * @return A map of all storage locations available
     */
    public static Map<String, File> getFileRoot() {

        Map<String, File> map = new HashMap<String, File>();
        final File externalStorageRoot;


                final File primaryExternalStorage = Environment.getExternalStorageDirectory();
                //Retrieve the External Storages root directory:
                final String externalStorageRootDir;
                if ( (externalStorageRootDir = primaryExternalStorage.getParent()) != null ) {  // no parent...

                    externalStorageRoot = primaryExternalStorage;
                    Log.e(TAG, "External Storage: primaryExternalStorage = " + externalStorageRoot.getAbsolutePath() + "\n");
                }else {
                    externalStorageRoot = new File( externalStorageRootDir );
                    Log.e(TAG, "External Storage: externalStorageRootDir = " + externalStorageRoot.getAbsolutePath() + "\n");


                }


        map.put("externalStorageRootDir", externalStorageRoot);


        return map;

    }

    public static File[] getFilesName(File root) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] list = root.listFiles(); // here use the root object of File class to the list of files and directory from the external storage
        //Log.i("DIR", "PATH" +file.getPath());
        for (int i = 0; i < list.length; i++)
        {

            Log.e("File : ", list[i].getName());
            if (list[i].getName().toLowerCase().contains(".mp3"))
            {
                inFiles.add(list[i]);
                Log.e("Music Mp3 : ", list[i].getName());
            } else if (list[i].getName().toLowerCase().contains(".3gpp"))
            {
                inFiles.add(list[i]);
                Log.e("Music 3gpp : ", list[i].getName());
            }
        }

        return list;

    }

    public static File findFile(File dir, String name) {
        File[] children = dir.listFiles();
        Log.e(TAG, "============ children files ============");

        for(File child : children) {
            if(child.isDirectory()) {
                Log.e(TAG, "children files "+child+ " isDirectory()");
                File found = findFile(child, name);
                if(found != null) return found;
            } else {
                if(name.equals(child.getName())) {

                    Log.e(TAG, "child name "+child.getName());
                    return child;
                }
            }
        }

        return null;
    }



}
