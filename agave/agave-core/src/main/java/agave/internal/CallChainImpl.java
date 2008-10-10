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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class CallChainImpl implements CallChain {

    private String parameterName;
    private List<String> accessorNames = new LinkedList<String>();
    private MutatorType mutatorType;
    private String mutatorName;
    private String key;
    private Integer index;
    
    @SuppressWarnings("unchecked")
	public CallChainImpl(String parameterName, boolean unique) {
        this.parameterName = parameterName;
        LinkedList<String> callChain = new LinkedList(Arrays.asList(parameterName.split("\\.")));

        String mutator = null;
        
        if (callChain != null && !callChain.isEmpty()) {
            mutator = callChain.removeLast();
            for (String call : callChain) {
                accessorNames.add(createAccessorFrom(call));
            }
        } else {
            mutator = parameterName;
        }
        
        if (mutator.contains("$")) {
            String[] values = mutator.split("\\$");
            mutatorName = createPuttingMutatorFrom(values[0]);
            key = values[1];
            mutatorType = MutatorType.PUTTING;
        } else if (mutator.contains("!")) {
            String[] values = mutator.split("!");
            mutatorName = createInsertingMutatorFrom(values[0]);
            index = Integer.parseInt(values[1]);
            mutatorType = MutatorType.INSERTING;
        } else if (unique) {
            mutatorName = createSettingMutatorFrom(mutator);
            mutatorType = MutatorType.SETTING;
        } else {
            mutatorName = createAppendingMutatorFrom(mutator);
            mutatorType = MutatorType.APPENDING;
        }
    }
    
    public String getParameterName() {
        return parameterName;
    }

    public List<String> getAccessorNames() {
        return accessorNames;
    }
    
    public String getMutatorName() {
        return mutatorName;
    }
    
    public MutatorType getMutatorType() {
        return mutatorType;
    }
    
    public String getKey() {
        return key;
    }
    
    public Integer getIndex() {
        return index;
    }
 
    private String createAccessorFrom(String value) {
        return "get" + capitalize(value);
    }
    
    private String createPuttingMutatorFrom(String value) {
        return "putIn" + capitalize(value);
    }
    
    private String createInsertingMutatorFrom(String value) {
        return "insertIn" + capitalize(value);
    }
    
    private String createAppendingMutatorFrom(String value) {
        return "addTo" + capitalize(value);
    }
    
    private String createSettingMutatorFrom(String value) {
        return "set" + capitalize(value);
    }
    
    private String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
    
}
