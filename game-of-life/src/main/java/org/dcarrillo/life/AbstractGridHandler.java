/*
 * Copyright (c) 2007, Damian Carrillo
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimer in the 
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <ORGANIZATION> nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
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
package org.dcarrillo.life;

import agave.StringTemplateHandler;
import agave.HandlerContext;
import agave.HandlerException;
import agave.annotations.Converter;
import agave.converters.IntegerConverter;

import javax.servlet.http.HttpSession;

import java.io.IOException;

import org.dcarrillo.life.Grid;
import org.antlr.stringtemplate.StringTemplate;

/**
 * The base grid handler that helps coordinate all actions that deal with the
 * grid.
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo>
 */
public abstract class AbstractGridHandler extends StringTemplateHandler {

	static final String GRID       = "grid";
	static final String RUNNING    = "running";
	static final String ITERATIONS = "iterations";

	private int row;
	private int col;
	
	public final int getRow() {
		return row;
	}
	
	@Converter(IntegerConverter.class)
	public void setRow(final int row) {
		this.row = row;
	}
	
	public final int getCol() {
		return col;
	}
	
	@Converter(IntegerConverter.class)
	public final void setCol(final int col) {
		this.col = col;
	}
	
	public final void prepareTemplate(final HandlerContext context, final StringTemplate template) 
	throws HandlerException, IOException {
		HttpSession session = context.getRequest().getSession(true);
		
		Grid grid = (Grid)session.getAttribute(GRID);
		if (grid == null) {
			grid = new Grid(getRow(), getCol());
		}
		
		Boolean running = (Boolean)session.getAttribute(RUNNING);
		if (running == null) {
			running = Boolean.FALSE;
		}
		
		running = updateGrid(context, template, grid, running);
		
		Integer iterations = (Integer)session.getAttribute(ITERATIONS);
		if (iterations != null && running) {
			iterations = iterations + 1;
		}
		
		template.setAttribute(GRID, grid.getCurrent());
		template.setAttribute(RUNNING, running);
		if (iterations > 0) {
			template.setAttribute(ITERATIONS, iterations);
		}
		
		session.setAttribute(GRID, grid);
		session.setAttribute(RUNNING, running);
		session.setAttribute(ITERATIONS, iterations);
	}
	
	protected abstract Boolean updateGrid(
		final HandlerContext context,
		final StringTemplate template,
		final Grid grid,
		Boolean running
	) throws HandlerException, IOException;
	
}
