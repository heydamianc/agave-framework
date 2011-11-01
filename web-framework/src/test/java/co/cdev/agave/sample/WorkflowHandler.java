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
package co.cdev.agave.sample;

import java.io.IOException;

import javax.servlet.ServletException;

import co.cdev.agave.CompletesWorkflow;
import co.cdev.agave.HandlerContext;
import co.cdev.agave.HandlesRequestsTo;
import co.cdev.agave.InitiatesWorkflow;
import co.cdev.agave.ResumesWorkflow;


/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class WorkflowHandler {

    private int step = 0;
    
    @InitiatesWorkflow("wizard")
    @HandlesRequestsTo("/wizard/step1")
    public void step1(HandlerContext context, WorkflowForm form) throws IOException, ServletException {
        form.setStep1Result("one");
        step++;
    }
    
    @ResumesWorkflow("wizard")
    @HandlesRequestsTo("/wizard/step2")
    public void step2(HandlerContext context, WorkflowForm form) throws IOException, ServletException {
        form.setStep2Result("two");
        step++;
    }
    
    @CompletesWorkflow("wizard")
    @HandlesRequestsTo("/wizard/step3")
    public void step3(HandlerContext context, WorkflowForm form) throws IOException, ServletException {
        form.setStep3Result("three");
        step++;
    }
    
    public int getStep() {
        return step;
    }
    
    // this is used for testing to determine whether the AgaveFilter stores a handler in the session
    // of the desirable type
    
    @Override
    public boolean equals(Object that) {
        return this.getClass().equals(that.getClass());
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
    
}
