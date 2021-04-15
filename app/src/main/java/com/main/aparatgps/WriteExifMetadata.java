package com.main.aparatgps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class WriteExifMetadata {
    // TODO support for mp4

    /**
     * This set the GPS values in JPEG EXIF metadata.
     *
     * @param file
     *
     * @throws IOException
     * @throws ImageReadException
     * @throws ImageWriteException
     */
    // TODO test
    public File modifyExif(File file, final double longitude, final double latitude)
            throws IOException, ImageReadException, ImageWriteException {
        String filePath = file.getAbsolutePath();
        String fileExtension = filePath.substring(filePath.lastIndexOf("."));

        File file2 = new File(file.getAbsolutePath().replace(fileExtension, "exif" + fileExtension));
        File resultFile = setExifGPSTag(file, file2, longitude, latitude);
        if (!file.delete())
            System.out.println("File not found");
        else
            return resultFile;
        return null;
    }

    /**
     * This illustrates how to set the GPS values in JPEG EXIF metadata.
     *
     * @param jpegImageFile
     *            A source image file.
     * @param dst
     *            The output file.
     * @throws IOException
     * @throws ImageReadException
     * @throws ImageWriteException
     */
    public File setExifGPSTag(final File jpegImageFile, final File dst, final double longitude, final double latitude)
            throws IOException, ImageReadException, ImageWriteException {
        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {
            TiffOutputSet outputSet = null;

            // note that metadata might be null if no metadata is found.
            final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                // note that exif might be null if no Exif metadata is found.
                final TiffImageMetadata exif = jpegMetadata.getExif();

                if (null != exif) {
                    // TiffImageMetadata class is immutable (read-only).
                    // TiffOutputSet class represents the Exif data to write.
                    //
                    // Usually, we want to update existing Exif metadata by
                    // changing
                    // the values of a few fields, or adding a field.
                    // In these cases, it is easiest to use getOutputSet() to
                    // start with a "copy" of the fields read from the image.
                    outputSet = exif.getOutputSet();
                }
            }

            // if file does not contain any exif metadata, we create an empty
            // set of exif metadata. Otherwise, we keep all of the other
            // existing tags.
            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            {
                /*
                // Example of how to add/update GPS info to output set.
                // New York City
                final double longitude = 21; // 74 degrees W (in Degrees East)
                final double latitude = 37; // 40 degrees N (in Degrees
                // North)
                 */

                outputSet.setGPSInDegrees(longitude, latitude);
            }

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
                    outputSet);
        }
        return dst;
    }

    /**
     * @param mp4File
     *            A source image file.
     * @param dst
     *            The output file.
     * @throws IOException
     * @throws ImageReadException
     * @throws ImageWriteException
     */
    public File setExifGPSTagMp4(final File mp4File, final File dst, final double longitude, final double latitude)
            throws IOException, ImageReadException, ImageWriteException {







        /*
        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {
            TiffOutputSet outputSet = null;

            // note that metadata might be null if no metadata is found.
            VideoCapture.Metadata m  = new VideoCapture.Metadata(mp4File);
            final ImageMetadata metadata = Imaging.getMetadata(mp4File);
            final VideoCapture.Metadata mp4Metadata = (VideoCapture.Metadata) metadata;
            if (null != mp4Metadata) {
                // note that exif might be null if no Exif metadata is found.
                final TiffImageMetadata exif = mp4Metadata.getExif();

                if (null != exif) {
                    // TiffImageMetadata class is immutable (read-only).
                    // TiffOutputSet class represents the Exif data to write.
                    //
                    // Usually, we want to update existing Exif metadata by
                    // changing
                    // the values of a few fields, or adding a field.
                    // In these cases, it is easiest to use getOutputSet() to
                    // start with a "copy" of the fields read from the image.
                    outputSet = exif.getOutputSet();
                }
            }

            // if file does not contain any exif metadata, we create an empty
            // set of exif metadata. Otherwise, we keep all of the other
            // existing tags.
            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            {
                outputSet.setGPSInDegrees(longitude, latitude);
            }

            new ExifRewriter().updateExifMetadataLossless(mp4File, os,
                    outputSet);
        }

         */

        return dst;
    }

    public static List<ImageMetadata.ImageMetadataItem> readMetadata(String path) throws IOException, ImageReadException {
        File file = new File(path);
        final ImageMetadata metadata = Imaging.getMetadata(file);

        List<? extends ImageMetadata.ImageMetadataItem> list = metadata.getItems();


        /*
        for (Object o :
                list) {
            System.out.println(o);
        }
         */

        return (List<ImageMetadata.ImageMetadataItem>) metadata.getItems();
    }

    private String getGPS(String path, String LatitudeORLongitude){
        try {
            List<ImageMetadata.ImageMetadataItem> metadata = readMetadata(path);
            String latitude = "";
            for (ImageMetadata.ImageMetadataItem data:
                    metadata) {
                if (data.toString().contains(LatitudeORLongitude))
                    latitude = data.toString();
            }
            latitude = latitude.replace(LatitudeORLongitude, "")
                    .replace(" ", "");

            //return  Arrays.stream(latitude.split(",")).mapToDouble(Double::parseDouble).toArray();
            return latitude;
        } catch (IOException | ImageReadException e) {
            e.printStackTrace();
        }
        //return new double[0];
        return "Error getGPS()";
    }

    public String getGPSLatitude(String path){
        String parameter = "GPSLatitude: ";
        return getGPS(path, parameter);
    }

    public String getGPSLongitude(String path){
        String parameter = "GPSLongitude: ";
        return getGPS(path, parameter);
    }

}
