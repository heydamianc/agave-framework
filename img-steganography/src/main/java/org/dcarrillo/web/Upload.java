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
import agave.converters.IntegerConverter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import org.antlr.stringtemplate.StringTemplate;
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
    private int x;
    private int y;

    @Converters(value = {UploadedFileConverter.class})
    public void setImage(File image) {
        this.image = image;
    }

    public File getImage() {
        return image;
    }

    @Converters(value = {IntegerConverter.class})
    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    @Converters(value = {IntegerConverter.class})
    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public Upload() {
        setX(-1);
        setY(-1);
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
        Integer xcoord = (Integer)session.getAttribute("x");
        Integer ycoord = (Integer)session.getAttribute("y");

        if (img != null) {
            template.setAttribute("img", img.getName());

            BufferedImage bimage = null;
            try {
                bimage = ImageIO.read(img);
                WritableRaster raster = bimage.getRaster();

                if (raster != null) {
                    template.setAttribute("raster", raster);
                    template.setAttribute("classname", raster.getSampleModel().getClass().getName());
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

        if (getImage() != null) {
            session.setAttribute("img", getImage());
        }

        if (getX() != -1) {
            session.setAttribute("x", new Integer(getX()));
        }

        if (getY() != -1) {
            session.setAttribute("y", new Integer(getY()));
        }

        return request.getContextPath() + "/upload";
    }
}