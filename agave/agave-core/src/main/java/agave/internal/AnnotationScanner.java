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
package agave.internal;

import java.util.Collection;

import org.objectweb.asm.AnnotationVisitor;

/**
 * Scans the annotation of methods in search for a handler method.
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class AnnotationScanner implements AnnotationVisitor {

    private Collection<HandlerIdentifier> handlerIdentifiers;
    private String className;
    private String methodName;
    
    @SuppressWarnings("unused")
	private String methodDesciptor;
    
    @SuppressWarnings("unused")
    private String annotationDescriptor;

    public AnnotationScanner(Collection<HandlerIdentifier> handlerIdentifiers, String className, String methodName,
        String methodDescriptor, String annotationDescriptor) {
        this.handlerIdentifiers = handlerIdentifiers;
        this.className = className.replace("/", ".");
        this.methodName = methodName;
        this.methodDesciptor = methodDescriptor;
        this.annotationDescriptor = annotationDescriptor;
    }
    
    public void visit(String name, Object value) {
        if ("value".equals(name) && className != null && methodName != null) {
            handlerIdentifiers.add(new HandlerIdentifierImpl(value.toString(), className, methodName));
        }
    }
    
    public void visitEnum(String name, String desc, String value) {
    }
    
    public AnnotationVisitor visitArray(String name) {
        return null;
    }
    
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return null;
    }
    
    public void visitEnd() {
    }

}
