package com.main.aparatgps.photo;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServieHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mDriveService;

    public DriveServieHelper(Drive mDriveService){
        this.mDriveService = mDriveService;
    }

    public Task<String> createImage(String imageName){
        return Tasks.call(mExecutor, () -> {
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(imageName);
            File file = new File("/storage/emulated/0/DCIM/Camera/"+imageName);
            FileContent mediaContent = new FileContent("image/jpg", file);
            com.google.api.services.drive.model.File myFile = null;
            try{
                myFile = mDriveService.files().create(fileMetaData, mediaContent).execute();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(myFile==null){
                throw new IOException("Null result when requesting image creation");
            }
            return myFile.getId();
        });
    }
}
