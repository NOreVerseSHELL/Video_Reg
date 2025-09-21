package com.videoreg.videoreg.video;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import org.bytedeco.javacv.*;

import java.awt.image.BufferedImage;

public class OpenCvUtils {

    public static WritableImage toFXImage(BufferedImage bimg) {
        WritableImage wr = new WritableImage(bimg.getWidth(), bimg.getHeight());
        int[] data = bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), null, 0, bimg.getWidth());
        wr.getPixelWriter().setPixels(0, 0, bimg.getWidth(), bimg.getHeight(),
                PixelFormat.getIntArgbInstance(), data, 0, bimg.getWidth());
        return wr;
    }
}
