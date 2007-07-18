/*
 * Copyright (c) 2007 Damian Carrillo
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the <ORGANIZATION> nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dcarrillo.web;

import agave.FormHandler;
import agave.HandlerContext;
import agave.HandlerException;
import agave.StringTemplateHandler;
import agave.annotations.ContentType;
import agave.annotations.Converters;
import agave.annotations.Path;
import agave.annotations.Template;
import agave.converters.UploadedFileConverter;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.stringtemplate.StringTemplate;
import org.dcarrillo.image.TextEncodingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Groups Upload handlers together.
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
@Path(value = "/upload")
@ContentType(value = ContentType.APPLICATION_XHTML_XML)
@Template(path = "/", name = "upload")
public class Upload extends StringTemplateHandler implements FormHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Upload.class);

    private File image;
    private String text;

    @Converters(value = {UploadedFileConverter.class})
    public void setImage(File image) {
        this.image = image;
    }

    public File getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Prepares the upload form submission template for display.
     *
     * @param context the Agave Context in which we run
     * @param template the template to mutate for display
     * @throws agave.HandlerException
     * @throws java.io.IOException
     */
    public void prepareTemplate(final HandlerContext context, final StringTemplate template) throws HandlerException, IOException {
        HttpServletRequest request = context.getRequest();
        HttpSession session = request.getSession(true);

        File img = (File)session.getAttribute("img");
        String txt = (String)session.getAttribute("txt");

        if (img != null) {
            template.setAttribute("img", img.getName());

            BufferedImage bi = null;
            try {
                bi = ImageIO.read(img);
                TextEncodingFilter tef = new TextEncodingFilter(txt);
                Toolkit tk = Toolkit.getDefaultToolkit();
                FilteredImageSource fis = new FilteredImageSource(bi.getSource(), tef);
                Image fi = tk.createImage(fis);

                int w = bi.getWidth();
                int h = bi.getHeight();
                
                BufferedImage fbi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = fbi.createGraphics();
                g2d.drawImage(fi, 0, 0, w, h, null);

                Matcher fm = Pattern.compile("^(.*)\\.(.*)$").matcher(img.getAbsolutePath());
                if (fm.matches() && fm.groupCount() >= 2) {
                    File fimg = new File(fm.group(1) + ".filtered." + fm.group(2));
                    fimg.createNewFile();
                    ImageIO.write(fbi, "jpg", fimg);
                    template.setAttribute("fimg", fimg.getName());
                }
            } catch (IOException ex) {
                throw new HandlerException(ex);
            }
        }
    }

    /**
     * Process the upload form.
     *
     * @param context
     * @throws agave.HandlerException
     */
    public String process(final HandlerContext context) throws HandlerException {
        HttpServletRequest request = context.getRequest();
        HttpSession session = request.getSession(true);

        if (image != null) {
            session.setAttribute("img", image);
        }
        if (text != null && !text.equals("")) {
            session.setAttribute("txt", text);
        }
        return request.getContextPath() + "/upload";
    }
}