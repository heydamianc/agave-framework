/*
 *  Copyright (c) 2009 Modern Mingle, LLC.  All rights Reserved.
 */
package agave.template;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;

/**
 * Injects a fragment of content into a templated page.  A JSP is intended to
 * have one or multiple {@code <template:use />} tags so that the fragments can
 * be pulled from the request and injected into the page.
 *
 * @author damian
 */
public class UseFragmentTag extends TemplateTag {

	private static final long serialVersionUID = 1L;
	private String name;
    private boolean required = false;
    private int indent = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    /**
     * Inserts the named JSP fragment after the tag has finished processing.
     *
     * @return the constant to continue with page evaluation
     * @throws javax.servlet.jsp.JspException
     */
    @Override
    public int doEndTag() throws JspException {
        super.doEndTag();

        String fragment = null;

        Map<String, String> templateFragments = getCurrentFragmentMap();
        if (templateFragments != null && templateFragments.containsKey(name)) {
            fragment = templateFragments.get(name);

            if (fragment == null) {
                // in the event that a supplied fragment has not been provide
                // render the content of the use tag, if any
                fragment = bodyContent.getString();
            } else {
                // otherwise use the supplied fragment, and work with the
                // indentation, so that it's pretty-printed
                fragment = indent(fragment, indent);
            }
        } else {
            // try in the global section of additive fragments
            Map<String, List<String>> globalFragmentMap = getGlobalFragmentMap();
            if (globalFragmentMap.containsKey(name)) {
                StringBuilder compoundFragment = new StringBuilder();
                for (String additiveFragment : globalFragmentMap.get(name)) {
                    compoundFragment.append(additiveFragment);
                }
                fragment = indent(compoundFragment.toString(), indent);
            }
        }
        try {
            pageContext.getOut().write(fragment);
        } catch (IOException ex) {
            throw new JspException(ex);
        }

        this.release();
        return EVAL_PAGE;
    }

}
