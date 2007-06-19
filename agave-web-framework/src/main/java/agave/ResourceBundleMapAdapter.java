/*
 * Copyright (c) 2006 - 2007 Damian Carrillo.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *	o Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 *	o Redistributions in binary form must reproduce the above copyright notice,
 *	  this list of conditions and the following disclaimer in the documentation
 *	  and/or other materials provided with the distribution.
 *	o Neither the name of the <ORGANIZATION> nor the names of its contributors
 *	  may be used to endorse or promote products derived from this software
 *	  without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package agave;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 * @since 1.0
 */
public class ResourceBundleMapAdapter implements Map<String, String> {

	private ResourceBundle bundle;
	private Set<String> keys;
	
	/**
	 *
	 * @param baseName
	 */
	public ResourceBundleMapAdapter(String baseName) {
		this(ResourceBundle.getBundle(baseName));
	}

	/**
	 *
	 * @param bundle
	 */	
	public ResourceBundleMapAdapter(ResourceBundle bundle) {
		this.bundle = bundle;
		keys = new HashSet<String>();
		for (Enumeration<String> ks = bundle.getKeys(); ks.hasMoreElements();) {
			String key = ks.nextElement();
			keys.add(key);
		}
	}

	/**
	 * 
	 */
	public void clear() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	public boolean containsKey(Object key) {
		return (key != null) ? keys.contains(key.toString()) : false;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	public Collection<String> values() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public Set<Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public boolean equals(Object o) {
		if (o instanceof ResourceBundle) {
			return bundle.equals((ResourceBundle)o);
		}
		return false;
	}
	
	/**
	 * 
	 */
	public int hashCode() {
		return bundle.hashCode();
	}
	
	/**
	 * 
	 */
	public String get(Object key) {
		return bundle.getString(key.toString());
	}
	
	/**
	 * 
	 */
	public boolean isEmpty() {
		return keys.isEmpty();
	}
	
	/**
	 * 
	 */
	public Set<String> keySet() {
		return keys;
	}
	
	/**
	 * 
	 */
	public String put(String key, String value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public void putAll(Map<? extends String, ? extends String> values) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public String remove(Object key) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public int size() {
		return keys.size();
	}

	/**
	 * 
	 */
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

}
