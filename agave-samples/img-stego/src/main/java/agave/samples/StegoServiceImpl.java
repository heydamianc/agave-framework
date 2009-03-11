/**
 * Copyright (c) 2008, Damian Carrillo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of 
 *     conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *     conditions and the following disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *   * Neither the name of the copyright holder's organization nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software without specific 
 *     prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package agave.samples;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import agave.Part;

/**
 * An implementation of a service that can encode text in an image and decode
 * the interpolated text from an image. This service class basically encompasses
 * the grunt work of the application.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class StegoServiceImpl implements StegoService {

    /**
     * The number of pixels that a code point can possibly span.
     */
    private static final int PIXEL_SPAN = 5;

    /**
     * Encodes message text in an image. Note that this supports UTF-16
     * characters so you won't be able to fit as much text as only supporting
     * ASCII. As a general rule, take the image height and multiply it by the
     * image width and then divide the result by five to get the number of
     * characters you can store.
     *
     * @param imageFile the original image file
     * @param message the message to hide
     * @throws java.io.IOException
     * @return the encoded image file
     */
    public File encode(Part part, String msg, String encodedFilenamePrefix) throws IOException {
        BufferedImage originalImage = ImageIO.read(part.getContents());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[] pixels = originalImage.getRGB(0, 0, width, height, null, 0, width);
        File encodedImageFile = null;
        if (pixels.length >= (msg.length() * PIXEL_SPAN)) {
            for (int i = 0, pos = 0; (pos + PIXEL_SPAN) < pixels.length; i++, pos += PIXEL_SPAN) {
                int codePoint = 0;
                if (i < msg.length()) {
                    codePoint = msg.codePointAt(i);
                }
                interpolate(codePoint, pixels, pos);
            }
            BufferedImage encodedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            encodedImage.setRGB(0, 0, width, height, pixels, 0, width);
            encodedImageFile =
                new File(part.getContents().getParentFile(), encodedFilenamePrefix + part.getContents().getName());
            encodedImageFile.createNewFile();

//			TODO figure out why this is screwy with JPEGs
//			Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(part.getContentType());
//			
//			while (writers.hasNext()) {
//				ImageWriter writer = writers.next();
//				ImageOutputStream out = ImageIO.createImageOutputStream(encodedImageFile);
//				writer.setOutput(out);
//				writer.write(encodedImage);
//				out.close();
//				break;
//			}

            // just use png encoding for now
            ImageIO.write(encodedImage, "PNG", encodedImageFile);
        } else {
            throw new RuntimeException("Could not fit text in image");
        }
        return encodedImageFile;
    }

    /**
     * Interpolates a UTF-16 character (possibly 5 nibbles) into the image by following this algorithm:
     * <ol>
     *   <li>Break the code point up into 5 nibbles.  Some nibbles may be 0 if the code point is not a
     *       higher character in UTF-16.  ASCII characters will still use only 7 bits and can be
     *       represented with 2 nibbles.</li>
     *   <li>Once the code point has been broken into nibbles, each bit of the nibble will be ANDed into 
     *       the least significant bits of each color channel in the pixel.  There is one alpha channel
     *       to represent transparency and 3 color channels, red, green, and blue.  They are all packed
     *       into an integer type and each byte represents the color channel.  It looks like this:
     *   <pre>
     *       Alpha     Red     Green     Blue
     *      11111111 11111111 11111111 11111111  =  0xffffffff (in hexadecimal)
     *      11111110 11111110 11111110 11111110  =  0xfefefefe (drop least significant bit of each color)
     *             ^        ^        ^        ^
     *             '--------+--------+--------+--- 0 \    Most significant nibble bit
     *                      '--------+--------+--- 1  \__ a nibble
     *                               '--------+--- 0  /   
     *                                        '----1 /    Least significant nibble bit
     *      11111110 11111111 11111110 11111111  =  Result
     *    </pre>
     *    </li>
     * </ol>
     * 
     * @param codePoint The UTF-16 character (or code point) to interpolate into the image
     * @param pixels The linear array of pixels represented as packed ARGB pixels
     * @param position The position int the pixels to begin interpolation
     * @return the position that the is the result of adding the initial position and PIXEL span
     */
    protected int interpolate(int codePoint, int[] pixels, int position) {
        for (int i = 0; i < PIXEL_SPAN && (i + position) < pixels.length; i++) {
            int nibble = (codePoint >> (i * 4)) & 0xf;
            pixels[i + position] =
                (pixels[i + position] & 0xfefefefe) | // drop the least significant bit of each channel
                ((nibble & 0x8) << 21) | // shift the highest nibble bit into the alpha channel
                ((nibble & 0x4) << 14) | // shift the next highest bit into the red channel
                ((nibble & 0x2) << 7) | // shift the next highest bit into the green channel
                (nibble & 0x1);          // or least significant nibble bit into the blue channel
        }
        return position + PIXEL_SPAN;
    }

    /**
     * Decodes a message hidden within an image file.
     *
     * @param imageFile the image file with encoded data
     * @return the decoded string
     */
    public String decode(File imageFile) throws IOException {
        BufferedImage encoded = ImageIO.read(imageFile);
        int width = encoded.getWidth();
        int height = encoded.getHeight();
        int[] pixels = encoded.getRGB(0, 0, width, height, null, 0, width);
        int[] codePoints = new int[pixels.length / PIXEL_SPAN];
        int codePointPtr = 0;
        for (int i = 0; i < pixels.length; i += PIXEL_SPAN, codePointPtr++) {
            codePoints[codePointPtr] = extract(pixels, i);
            if (codePoints[codePointPtr] == 0) {
                break;
            }
        }
        return new String(codePoints, 0, codePoints.length).trim();
    }

    /**
     * Reverses the process outlined in the comments to the interpolate method
     * in order to extract a UTF-16 code point that is embedded within a 5 pixel
     * span.
     *
     * @param pixels The pixel array
     * @param position The position in the pixel array
     * @return the codePoint embedded in the image that has been read in
     */
    protected int extract(int[] pixels, int position) {
        int codePoint = 0;
        for (int i = 0; i < PIXEL_SPAN && i < pixels.length; i++) {
            int tmp = 0;
            tmp |= ((pixels[i + position] & 0x1000000) >> 21);
            tmp |= ((pixels[i + position] & 0x0010000) >> 14);
            tmp |= ((pixels[i + position] & 0x0000100) >> 7);
            tmp |= (pixels[i + position] & 0x0000001);
            codePoint |= tmp << (i * 4);
        }
        return codePoint;
    }
}
