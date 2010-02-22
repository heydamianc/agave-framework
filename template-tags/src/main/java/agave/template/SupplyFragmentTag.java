/*
 *  Copyright (c) 2009 Modern Mingle, LLC.  All rights Reserved.
 */
package agave.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;

/**
 * Supplies a JSP fragment to be used by the applied template.
 * 
 * @author damian
 */
public class SupplyFragmentTag extends TemplateTag {

	private static final long serialVersionUID = 1L;
	private String name;
    private boolean additive = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdditive() {
        return additive;
    }

    public void setAdditive(boolean additive) {
        this.additive = additive;
    }
    
    @Override
    public int doEndTag() throws JspException {
        super.doEndTag();

        if (additive) {
            Map<String, List<String>> globalFragments = getGlobalFragmentMap();
            if (globalFragments.get(name) == null) {
                globalFragments.put(name, new ArrayList<String>());
            }
            globalFragments.get(name).add(bodyContent.getString());
        } else {
            Map<String, String> templateFragments = getCurrentFragmentMap();
            templateFragments.put(name, bodyContent.getString());
        }
        
        return EVAL_PAGE;
    }

}
