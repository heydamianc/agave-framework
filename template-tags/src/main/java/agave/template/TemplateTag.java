/*
 *  Copyright (c) 2009 Modern Mingle, LLC.  All rights Reserved.
 */
package agave.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *
 * @author damian
 */
@SuppressWarnings("serial")
public class TemplateTag extends BodyTagSupport {

    protected static final String ENDL = System.getProperty("line.separator");
    protected static final String TEMPLATE_STORE_KEY = TemplateTag.class.getName() + ".TEMPLATE_STORE_KEY";
    protected static final String GLOBAL_FRAGMENTS_KEY = TemplateTag.class.getName() + "GLOBAL_FRAGMENTS_KEY";

    @SuppressWarnings("unchecked")
    protected Map<String, List<String>> getGlobalFragmentMap() {
        Map<String, List<String>> globalFragmentMap = null;

        if (pageContext.getRequest().getAttribute(GLOBAL_FRAGMENTS_KEY) != null) {
            globalFragmentMap =
                (Map<String, List<String>>)pageContext.getRequest().getAttribute(GLOBAL_FRAGMENTS_KEY);
        } else {
            globalFragmentMap = new HashMap<String, List<String>>();
            pageContext.getRequest().setAttribute(GLOBAL_FRAGMENTS_KEY, globalFragmentMap);
        }

        return globalFragmentMap;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String> getCurrentFragmentMap() {
        return ((Stack<Map<String, String>>)pageContext.getRequest()
            .getAttribute(TEMPLATE_STORE_KEY)).peek();
    }

    protected String indent(String fragment, int level) {
        String indentedFragment = fragment;
        
        String indentation = "";
        for (int i = 0; i < level; i++) {
            indentation += " ";
        }

        indentedFragment = fragment.replace(ENDL, ENDL + indentation);
        if (indentedFragment.endsWith(indentation)) {
            indentedFragment = indentedFragment.substring(0, indentedFragment.lastIndexOf(indentation));
        }

        return indentedFragment;
    }

}
