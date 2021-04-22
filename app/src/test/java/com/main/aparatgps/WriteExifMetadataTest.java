package com.main.aparatgps;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.DoublePredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WriteExifMetadataTest {
    @Before
    public void createFile() throws IOException {
        /*
        String fileStr = ".\\src\\test\\java\\p.jpg";
        //Files.copy(fileStr, ".\\src\\test\\java\\p1.jpg", new StandardCopyOption[]{StandardCopyOption.REPLACE_EXISTING});
        Files.copy(fileStr, ".\\src\\test\\java\\p1.jpg", StandardCopyOption.REPLACE_EXISTING);

         */

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(".\\src\\test\\java\\p.jpg");
            os = new FileOutputStream(".\\src\\test\\java\\p1.jpg");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    @Test
    public void modifyExifJPGTest() throws ImageWriteException, ImageReadException, IOException {
        //String fileStr = "/fileTest.txt";
        //String fileStr = "/src/test/java/p1.jpg";
        String fileStr = ".\\src\\test\\java\\p1.jpg";
        String fileStrExif = ".\\src\\test\\java\\p1exif.jpg";
        File file1 = new File(fileStr);


        new WriteExifMetadata().modifyExif(file1, 21, 20);


        File file2 = new File(fileStrExif);
        boolean fileExist = file2.exists();

        BasicFileAttributes attr = Files.readAttributes(Paths.get(fileStrExif), BasicFileAttributes.class);

        System.out.println("creationTime: " + attr.creationTime());
        System.out.println("lastAccessTime: " + attr.lastAccessTime());
        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());

        System.out.println("isDirectory: " + attr.isDirectory());
        System.out.println("isOther: " + attr.isOther());
        System.out.println("isRegularFile: " + attr.isRegularFile());
        System.out.println("isSymbolicLink: " + attr.isSymbolicLink());
        System.out.println("size: " + attr.size());


        assertTrue(fileExist);

        final ImageMetadata metadata = Imaging.getMetadata(file2);
        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

        System.out.println(jpegMetadata);
        file2.delete();

    }

    @Test
    public void readMetadataTest() throws IOException, ImageReadException {
        List<ImageMetadata.ImageMetadataItem> metadata =
            WriteExifMetadata.readMetadata(".\\src\\test\\java\\p1exif.jpg");

        String latitude = "";
        for (ImageMetadata.ImageMetadataItem data:
             metadata) {
            if (data.toString().contains("GPSLatitude"))
                latitude = data.toString();
        }

        //System.out.println("latitude:" + latitude);
        assertEquals( "GPSLatitude: 37, 0, 0",latitude);
    }

    /*
    @Test
    public void readMetadataTest2() throws IOException, ImageReadException {
        double[] latitude = new WriteExifMetadata().getGPSLatitude(".\\src\\test\\java\\p1exif.jpg");

        double[] expected = {37, 0, 0};

        for (int i = 0; i < latitude.length; i++) {
            assertEquals(expected[i], latitude[i], 0.0);
        }
    }

     */
}
