package com.main.aparatgps.photo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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

    public Task<String> shareImage(String photoPath, String user) {
    //public void shareImage(String photoPath, String user) {
        return Tasks.call(mExecutor, () -> {
            FileList fileList = mDriveService.files().list().execute();

            return fileList.getKind();
        });
    }

    public void fileList(String photoPath, String user) throws IOException {
            FileList fileList = mDriveService.files().list().execute();

    }

}
