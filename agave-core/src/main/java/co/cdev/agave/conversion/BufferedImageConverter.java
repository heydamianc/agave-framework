package co.cdev.agave.conversion;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;

import co.cdev.agave.Part;

public class BufferedImageConverter implements PartConverter<BufferedImage, File> {

    @Override
    public BufferedImage convert(Part<File> input, Locale locale) throws AgaveConversionException {
        BufferedImage image = null;
        if (input != null) {
            try {
                image = ImageIO.read((File) input.getContents());
            } catch (IOException ex) {
                throw new AgaveConversionException(ex.getCause());
            }
        }
        return image;
    }
}
