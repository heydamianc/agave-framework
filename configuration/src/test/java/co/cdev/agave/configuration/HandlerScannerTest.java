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
package co.cdev.agave.configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import co.cdev.agave.sample.SampleHandler;

public class HandlerScannerTest {
    
    Collection<ScanResult> scanResults = new ArrayList<ScanResult>();
    
    @Test
    public void testVisit() throws Exception {
        HandlerScanner scanner = new HandlerScanner(scanResults);
        scanner.visit(1, Opcodes.ACC_PUBLIC, "className", "", "", new String[] {});
        Assert.assertEquals("className", scanner.className);
        
        scanner = new HandlerScanner(scanResults);
        scanner.visit(1, Opcodes.ACC_PROTECTED, "className", "", "", new String[] {});
        Assert.assertNull(scanner.className);
        
        scanner = new HandlerScanner(scanResults);
        scanner.visit(1, Opcodes.ACC_PRIVATE, "className", "", "", new String[] {});
        Assert.assertNull(scanner.className);
    }

    @Test
    public void testLocateSampleScanner() throws Exception {
        InputStream sampleStream = 
            getClass().getClassLoader().getResourceAsStream("co/cdev/agave/sample/SampleHandler.class");
        try {
            ClassReader classReader = new ClassReader(sampleStream);
            scanResults = new ArrayList<ScanResult>();
            classReader.accept(new HandlerScanner(scanResults), ClassReader.SKIP_CODE );
            ScanResult hi = null;
            for (ScanResult identifier : scanResults) {
                if (identifier.getUri().equals("/login")) {
                    hi = identifier;
                }
            }
            Assert.assertNotNull(hi);
            Assert.assertEquals(SampleHandler.class.getName(), hi.getClassName());
        } finally {
            sampleStream.close();
        }
    }
    
}
