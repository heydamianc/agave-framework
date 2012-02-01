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

import java.util.Collection;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerScanner extends EmptyVisitor implements ClassVisitor {

    Collection<ScanResult> scanResults;
    String className;

    public HandlerScanner(Collection<ScanResult> scanResults) {
        super();
        this.scanResults = scanResults;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, 
        String[] interfaces) {
        if ((access & Opcodes.ACC_PUBLIC) > 0) {
            this.className = name;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] interfaces) { 
        MethodVisitor methodScanner = null;
        
        if ((access & Opcodes.ACC_PUBLIC) > 0) {
            methodScanner = new MethodScanner(scanResults, className, name, desc);
        }
        
        return methodScanner;
    }

}
