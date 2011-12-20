package co.cdev.agave.samples;

import co.cdev.agave.Destination;
import co.cdev.agave.Destinations;
import co.cdev.agave.RoutingContext;
import co.cdev.agave.Route;
import co.cdev.agave.exception.ConversionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XMLToolsHandler {

    /**
     * Pretty-prints (or formats) a submitted XML file.
     *
     * @param handlerContext the context that this handler method executes under
     * @throws Exception if anything goes wrong
     * @return a destination object that wraps the index.jsp page
     */
    @Route("/")
    public Destination welcome(RoutingContext handlerContext) throws Exception {
        return Destinations.forward("/WEB-INF/jsp/index.jsp");
    }

    @Route("/reformat")
    public Destination reformat(RoutingContext handlerContext, ReformatForm form) throws Exception {
        StringWriter sink = new StringWriter();

        File documentFile = form.getDocument().getContents();
        if (documentFile.canRead() && documentFile.exists()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(documentFile));
                StringBuilder documentContents = new StringBuilder();
                String line = null;
                while ((line = in.readLine()) != null) {
                    documentContents.append(line);
                }
                Document document = DocumentHelper.parseText(documentContents.toString());
                OutputFormat formatter = new OutputFormat(form.getIndentation(), form.isNewlines());
                new XMLWriter(sink, formatter).write(document);
            }
            catch (DocumentException ex) {
                throw new ConversionException("Unable to parse XML file", ex);
            }
            catch (IOException ex) {
                throw new ConversionException("Unable to read XML file", ex);
            }
        }

        handlerContext.getSession().setAttribute("contents", sink.toString());
        return Destinations.redirect("/reformatted/" + form.getDocument().getFilename());
    }

    @Route("/reformatted/${fileName}")
    public void output(RoutingContext handlerContext) throws Exception {
        handlerContext.getResponse().setContentType("text/xml");
        PrintWriter out = handlerContext.getResponse().getWriter();
        out.write(handlerContext.getSession().getAttribute("contents").toString());
        out.close();
    }
    
    @SuppressWarnings("unused")
	private void sendError(HttpServletResponse response, Exception ex) {
        StringWriter sink = new StringWriter();
        ex.printStackTrace(new PrintWriter(sink));
        try {
            response.sendError(500, sink.toString());
        }
        catch (IOException ex1) {
            // error occurred
        }
    }
    
}
