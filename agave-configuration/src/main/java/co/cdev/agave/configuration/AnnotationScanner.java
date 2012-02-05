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
package co.cdev.agave.configuration;

import java.util.Arrays;
import java.util.Collection;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import co.cdev.agave.HttpMethod;

/**
 * Scans the annotation of methods in search for a handler method.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class AnnotationScanner implements AnnotationVisitor {

    private static final String ANNOTATION_VALUE_PARAM = "value";
    private static final String ANNOTATION_URI_PARAM = "uri";
    private static final String ANNOTATION_METHOD_PARAM = "method";
    private Collection<ScanResult> scanResults;
    private ScanResult scanResult;

    public AnnotationScanner(Collection<ScanResult> scanResults,
            String className, String methodName, String methodDescriptor,
            String annotationDescriptor) {
        this.scanResults = scanResults;
        this.scanResult = new ScanResultImpl();
        
        // TODO I'm pretty sure this won't work on Windows
        
        this.scanResult.setClassName(className.replace("/", "."));
        this.scanResult.setMethodName(methodName);
        this.scanResult.setMethod(HttpMethod.ANY);
        
        Type[] argumentTypes = Type.getArgumentTypes(methodDescriptor);
        String[] argumentClassNames = new String[argumentTypes.length];
        
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            
            switch (argumentType.getSort()) {
                case Type.ARRAY:
                    // TODO FIGURE OUT HOW TO SUPPORT THIS
                    break;
                case Type.BOOLEAN:
                    argumentClassNames[i] = boolean.class.getCanonicalName();
                    break;
                case Type.BYTE:
                    argumentClassNames[i] = byte.class.getCanonicalName();
                    break;
                case Type.CHAR:
                    argumentClassNames[i] = char.class.getCanonicalName();
                    break;
                case Type.DOUBLE:
                    argumentClassNames[i] = double.class.getCanonicalName();
                    break;
                case Type.FLOAT:
                    argumentClassNames[i] = float.class.getCanonicalName();
                    break;
                case Type.INT:
                    argumentClassNames[i] = int.class.getCanonicalName();
                    break;
                case Type.LONG:
                    argumentClassNames[i] = long.class.getCanonicalName();
                    break;
                case Type.SHORT:
                    argumentClassNames[i] = short.class.getCanonicalName();
                    break;
                default:
                    argumentClassNames[i] = argumentTypes[i].getClassName();
                    break;
            }
        }
        
        this.scanResult.setParameterClassNames(Arrays.asList(argumentClassNames));
    }

    @Override
    public void visit(String name, Object value) {
        if (ANNOTATION_VALUE_PARAM.equals(name)
                || ANNOTATION_URI_PARAM.equals(name)) {
            this.scanResult.setUri(value.toString());
        }
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        if (ANNOTATION_METHOD_PARAM.equals(name)
                && Type.getDescriptor(HttpMethod.class).equals(desc)) {
            this.scanResult.setMethod(HttpMethod.valueOf(value));
        }
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return null;
    }

    @Override
    public void visitEnd() {
        if (this.scanResult.getClassName() != null
                && this.scanResult.getMethodName() != null
                && this.scanResult.getUri() != null
                && this.scanResult.getMethod() != null) {
            scanResults.add(this.scanResult);
        }
    }
}
