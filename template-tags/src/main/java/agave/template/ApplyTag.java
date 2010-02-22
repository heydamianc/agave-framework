/*
 *  Copyright (c) 2009 Modern Mingle, LLC.  All rights Reserved.
 */
package agave.template;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *
 * @author damian
 */
public class ApplyTag extends TemplateStateTag {

	private static final long serialVersionUID = 1L;
	private String path;

    public ApplyTag() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int doStartTag() throws JspException {
        super.doStartTag();

        pushTemplate();
        return BodyTagSupport.EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {
        super.doEndTag();

        try {
            pageContext.include(path);
        } catch (ServletException ex) {
            throw new JspException(ex);
        } catch (IOException ex) {
            throw new JspException(ex);
        } finally {
            popTemplate();
        }

        release();
        return EVAL_PAGE;
    }
}
