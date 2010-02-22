/*
 *  Copyright (c) 2009 Modern Mingle, LLC.  All rights Reserved.
 */
package agave.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.servlet.ServletRequest;

/**
 *
 * @author damian
 */
public abstract class TemplateStateTag extends TemplateTag {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected void pushTemplate() {
        ServletRequest request = pageContext.getRequest();

        Stack<Map<String, Stack<String>>> templateStore =
            (Stack<Map<String, Stack<String>>>)request.getAttribute(TEMPLATE_STORE_KEY);
        if (templateStore == null) {
            templateStore = new Stack<Map<String, Stack<String>>>();
        }

        templateStore.push(new HashMap<String, Stack<String>>());
        request.setAttribute(TEMPLATE_STORE_KEY, templateStore);
    }

	@SuppressWarnings("unchecked")
    protected void popTemplate() {
        ServletRequest request = pageContext.getRequest();
        Stack<Map<String, Stack<String>>> templateStore =
            (Stack<Map<String, Stack<String>>>)request.getAttribute(TEMPLATE_STORE_KEY);
        templateStore.pop();
        request.setAttribute(TEMPLATE_STORE_KEY, templateStore);
    }

}
