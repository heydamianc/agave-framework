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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class CallChainTest {

    @Test
    public void testMutatorWithoutAccessors() throws Exception {
		CallChain callChain = new CallChainImpl("symian", true);
        Assert.assertEquals("setSymian", callChain.getMutatorName());
		Assert.assertTrue(callChain.getAccessorNames().isEmpty());
		Assert.assertNull(callChain.getIndex());
		Assert.assertNull(callChain.getKey());
		Assert.assertEquals(MutatorType.SETTING, callChain.getMutatorType());
    }

	@Test
	public void testMutatorWithAccessors() throws Exception {
		assertDrumKick(new CallChainImpl("drum.kick", true));
		assertDrumKick(new CallChainImpl("drum-kick", true));
		assertDrumKick(new CallChainImpl("drum:kick", true));
	}

	private void assertDrumKick(CallChain callChain) {
        Assert.assertEquals("setKick", callChain.getMutatorName());
		Assert.assertEquals(1, callChain.getAccessorNames().size());
		Assert.assertEquals("getDrum", callChain.getAccessorNames().get(0));
		Assert.assertNull(callChain.getIndex());
		Assert.assertNull(callChain.getKey());
		Assert.assertEquals(MutatorType.SETTING, callChain.getMutatorType());
	}

	@Test
	public void testAdderWithoutAccessors() throws Exception {
		CallChain callChain = new CallChainImpl("wombats", false);
		Assert.assertEquals("addToWombats", callChain.getMutatorName());
		Assert.assertTrue(callChain.getAccessorNames().isEmpty());
		Assert.assertNull(callChain.getIndex());
		Assert.assertNull(callChain.getKey());
		Assert.assertEquals(MutatorType.APPENDING, callChain.getMutatorType());
	}

   	@Test
	public void testAdderWithAccessors() throws Exception {
		CallChain callChain = new CallChainImpl("animal.wombats", false);
		Assert.assertEquals("addToWombats", callChain.getMutatorName());
		Assert.assertEquals(1, callChain.getAccessorNames().size());
		Assert.assertEquals("getAnimal", callChain.getAccessorNames().get(0));
		Assert.assertNull(callChain.getIndex());
		Assert.assertNull(callChain.getKey());
		Assert.assertEquals(MutatorType.APPENDING, callChain.getMutatorType());
	}

   	@Test
	public void testInserterWithoutAccessors() throws Exception {
		CallChain callChain = new CallChainImpl("wombats!1", false);
		Assert.assertEquals("insertInWombats", callChain.getMutatorName());
		Assert.assertTrue(callChain.getAccessorNames().isEmpty());
		Assert.assertEquals(1, (int)callChain.getIndex());
		Assert.assertNull(callChain.getKey());
		Assert.assertEquals(MutatorType.INSERTING, callChain.getMutatorType());
	}

   	@Test
	public void testInserterWithAccessors() throws Exception {
		CallChain callChain = new CallChainImpl("animal.wombats!2", false);
		Assert.assertEquals("insertInWombats", callChain.getMutatorName());
		Assert.assertEquals(1, callChain.getAccessorNames().size());
		Assert.assertEquals("getAnimal", callChain.getAccessorNames().get(0));
		Assert.assertEquals(2, (int)callChain.getIndex());
		Assert.assertNull(callChain.getKey());
		Assert.assertEquals(MutatorType.INSERTING, callChain.getMutatorType());
	}

	@Test
	public void testPutterWithoutAccessors() throws Exception {
		CallChain callChain = new CallChainImpl("koalas$kim", false);
		Assert.assertEquals("putInKoalas", callChain.getMutatorName());
		Assert.assertTrue(callChain.getAccessorNames().isEmpty());
		Assert.assertNull(callChain.getIndex());
		Assert.assertEquals("kim", callChain.getKey());
		Assert.assertEquals(MutatorType.PUTTING, callChain.getMutatorType());
	}

	@Test
	public void testPutterWithAccessors() throws Exception {
		CallChain callChain = new CallChainImpl("animal.koalas$kate", true);
		Assert.assertEquals("putInKoalas", callChain.getMutatorName());
		Assert.assertEquals(1, callChain.getAccessorNames().size());
		Assert.assertEquals("getAnimal", callChain.getAccessorNames().get(0));
		Assert.assertNull(callChain.getIndex());
		Assert.assertEquals("kate", callChain.getKey());
		Assert.assertEquals(MutatorType.PUTTING, callChain.getMutatorType());
	}

}
