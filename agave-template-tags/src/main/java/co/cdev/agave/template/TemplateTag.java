/*
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
package co.cdev.agave.template;

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
