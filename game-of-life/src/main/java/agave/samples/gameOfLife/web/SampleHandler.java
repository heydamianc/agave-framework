/**
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
package agave.samples.gameOfLife.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agave.HandlesRequestsTo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class SampleHandler extends FreemarkerHandler {

    public static class SampleObject {
        private int count;
        
        public SampleObject(int count) {
            this.count = count;
        }
        
        public int getCount() {
            return count;
        }
    }
    
    @HandlesRequestsTo("/sample")
    public void handleSample() throws IOException, TemplateException {
        
        int rows = 5;
        int cols = 8;
        
        int i = 0;
        
        List<List<SampleObject>> samples = new ArrayList<List<SampleObject>>(rows);
        for (int row = 0; row < rows; row++) {
            samples.add(new ArrayList<SampleObject>());
            for (int col = 0; col < cols ; col++) {
                samples.get(row).add(new SampleObject(i++));
            }
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("objects", samples);
        
        PrintWriter out = response.getWriter();
        Configuration config = 
            (Configuration)servletContext.getAttribute(FreemarkerContextListener.FREEMARKER_CONFIG_KEY);
        Template template = config.getTemplate("sample.ftl");
        response.setContentType("text/html");
        template.process(model, out);
        out.flush();
        out.close();
        
    }
    
}
