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
