package com.main.aparatgps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.FileUtils;

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

}
