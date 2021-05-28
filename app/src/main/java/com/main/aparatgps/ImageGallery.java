package com.main.aparatgps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import com.main.aparatgps.photo.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageGallery {

    public static ArrayList<String> listOfImages(Context context){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;

        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy+" DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        //folder name
        //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while(cursor.moveToNext()){
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }

        return listOfAllImages;
    }

    /**
     *
     * @param context context aktywności
     * @return lista polubionych zdjęć
     */
    public static List<String> listOfFavoritesImages(Context context) {
        List<String> allImagesPaths = listOfImages(context);
        List<String> allLikedImagesPaths = databaseItems(context);


        return allImagesPaths.stream()
                .distinct()
                .filter(allLikedImagesPaths::contains)
                .collect(Collectors.toList());
    }

    private static List<String> databaseItems(Context context){
        SQLiteDatabase db = (new DatabaseHelper(context, "FavouritePhotos", null, 1))
                .getReadableDatabase();

        // Filter results WHERE "title" = 'My Title'
        String selection = "";


        String[] projection = {
                BaseColumns._ID,
                "Absolute_Path"
        };

        String sortOrder =
                "Absolute_Path" + " ASC";

        Cursor cursor = db.query(
                "FavouritePhotos",             // The table to query
                projection,                         // The array of columns to return (pass null to get all)
                selection,                          // The columns for the WHERE clause
                null,                    // The values for the WHERE clause
                null,                       // don't group the rows
                null,                        // don't filter by row groups
                sortOrder                           // The sort order
        );

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow("Absolute_Path"));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds;
    }
}
