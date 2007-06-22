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

import agave.HandlerContext;
import agave.HandlerException;
import agave.annotations.ContentType;
import agave.annotations.Converter;
import agave.annotations.Path;
import agave.annotations.PositionalParameters;
import agave.annotations.Template;
import agave.converters.ConversionException;

import java.io.IOException;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;

/**
 * Present the grid so that the user can set it up by bring cells to life.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo>
 */
@Path("/setup")
@PositionalParameters("pattern")
@ContentType(ContentType.APPLICATION_XHTML_XML)
@Template(path = "/", name = "display")
public final class Setup extends AbstractGridHandler {
	
	public static class PatternConverter implements agave.converters.Converter<Pattern> {
		public Pattern convert(String value) throws ConversionException {
			return Enum.valueOf(Pattern.class, value.toUpperCase());
		}
	}
	
	private enum Pattern {
		BLANK,
		BLOCK,
		BOAT,
		BLINKER,
		TOAD,
		GLIDER,
		LIGHTWEIGHT_SPACE_SHIP,
		PULSAR,
		GOSPER_GLIDER_GUN
	}
	
	private Pattern pattern;
	
	public Setup() {
		setRow(35);
		setCol(55);
		setPattern(Pattern.BLANK);
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	@Converter(PatternConverter.class)
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	protected Boolean updateGrid(
		final HandlerContext context,
		final StringTemplate template,
		final Grid grid,
		Boolean running
	) throws HandlerException, IOException {
		context.getRequest().getSession().setAttribute(ITERATIONS, new Integer(0));
		
		for (List<Cell> row : grid.getCurrent()) {
			for (Cell cell : row) {
				cell.setAlive(false);
			}
		}
		
		switch(getPattern()) {
			case BLOCK:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(11).get(13).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				break;
			case BOAT:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(11).get(13).setAlive(true);
				grid.getCurrent().get(11).get(15).setAlive(true);
				grid.getCurrent().get(12).get(14).setAlive(true);
				break;
			case BLINKER:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(10).get(15).setAlive(true);
				break;
			case TOAD:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(10).get(15).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				grid.getCurrent().get(11).get(15).setAlive(true);
				grid.getCurrent().get(11).get(16).setAlive(true);
				break;
			case GLIDER:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				grid.getCurrent().get(12).get(12).setAlive(true);
				grid.getCurrent().get(12).get(13).setAlive(true);
				grid.getCurrent().get(12).get(14).setAlive(true);
				break;
			case LIGHTWEIGHT_SPACE_SHIP:
				grid.getCurrent().get(13).get(25).setAlive(true);
				grid.getCurrent().get(13).get(28).setAlive(true);
				grid.getCurrent().get(14).get(24).setAlive(true);
				grid.getCurrent().get(15).get(24).setAlive(true);
				grid.getCurrent().get(15).get(28).setAlive(true);
				grid.getCurrent().get(16).get(24).setAlive(true);
				grid.getCurrent().get(16).get(25).setAlive(true);
				grid.getCurrent().get(16).get(26).setAlive(true);
				grid.getCurrent().get(16).get(27).setAlive(true);
				break;
			case PULSAR:
				grid.getCurrent().get(10).get(19).setAlive(true);
				grid.getCurrent().get(10).get(20).setAlive(true);
				grid.getCurrent().get(10).get(26).setAlive(true);
				grid.getCurrent().get(10).get(27).setAlive(true);
				grid.getCurrent().get(11).get(20).setAlive(true);
				grid.getCurrent().get(11).get(21).setAlive(true);
				grid.getCurrent().get(11).get(25).setAlive(true);
				grid.getCurrent().get(11).get(26).setAlive(true);
				grid.getCurrent().get(12).get(17).setAlive(true);
				grid.getCurrent().get(12).get(20).setAlive(true);
				grid.getCurrent().get(12).get(22).setAlive(true);
				grid.getCurrent().get(12).get(24).setAlive(true);
				grid.getCurrent().get(12).get(26).setAlive(true);
				grid.getCurrent().get(12).get(29).setAlive(true);
				grid.getCurrent().get(13).get(17).setAlive(true);
				grid.getCurrent().get(13).get(18).setAlive(true);
				grid.getCurrent().get(13).get(19).setAlive(true);
				grid.getCurrent().get(13).get(21).setAlive(true);
				grid.getCurrent().get(13).get(22).setAlive(true);
				grid.getCurrent().get(13).get(24).setAlive(true);
				grid.getCurrent().get(13).get(25).setAlive(true);
				grid.getCurrent().get(13).get(27).setAlive(true);
				grid.getCurrent().get(13).get(28).setAlive(true);
				grid.getCurrent().get(13).get(29).setAlive(true);
				grid.getCurrent().get(14).get(18).setAlive(true);
				grid.getCurrent().get(14).get(20).setAlive(true);
				grid.getCurrent().get(14).get(22).setAlive(true);
				grid.getCurrent().get(14).get(24).setAlive(true);
				grid.getCurrent().get(14).get(26).setAlive(true);
				grid.getCurrent().get(14).get(28).setAlive(true);
				grid.getCurrent().get(15).get(19).setAlive(true);
				grid.getCurrent().get(15).get(20).setAlive(true);
				grid.getCurrent().get(15).get(21).setAlive(true);
				grid.getCurrent().get(15).get(25).setAlive(true);
				grid.getCurrent().get(15).get(26).setAlive(true);
				grid.getCurrent().get(15).get(27).setAlive(true);
				grid.getCurrent().get(17).get(19).setAlive(true);
				grid.getCurrent().get(17).get(20).setAlive(true);
				grid.getCurrent().get(17).get(21).setAlive(true);
				grid.getCurrent().get(17).get(25).setAlive(true);
				grid.getCurrent().get(17).get(26).setAlive(true);
				grid.getCurrent().get(17).get(27).setAlive(true);
				grid.getCurrent().get(18).get(18).setAlive(true);
				grid.getCurrent().get(18).get(20).setAlive(true);
				grid.getCurrent().get(18).get(22).setAlive(true);
				grid.getCurrent().get(18).get(24).setAlive(true);
				grid.getCurrent().get(18).get(26).setAlive(true);
				grid.getCurrent().get(18).get(28).setAlive(true);
				grid.getCurrent().get(19).get(17).setAlive(true);
				grid.getCurrent().get(19).get(18).setAlive(true);
				grid.getCurrent().get(19).get(19).setAlive(true);
				grid.getCurrent().get(19).get(21).setAlive(true);
				grid.getCurrent().get(19).get(22).setAlive(true);
				grid.getCurrent().get(19).get(24).setAlive(true);
				grid.getCurrent().get(19).get(25).setAlive(true);
				grid.getCurrent().get(19).get(27).setAlive(true);
				grid.getCurrent().get(19).get(28).setAlive(true);
				grid.getCurrent().get(19).get(29).setAlive(true);
				grid.getCurrent().get(20).get(17).setAlive(true);
				grid.getCurrent().get(20).get(20).setAlive(true);
				grid.getCurrent().get(20).get(22).setAlive(true);
				grid.getCurrent().get(20).get(24).setAlive(true);
				grid.getCurrent().get(20).get(26).setAlive(true);
				grid.getCurrent().get(20).get(29).setAlive(true);
				grid.getCurrent().get(21).get(20).setAlive(true);
				grid.getCurrent().get(21).get(21).setAlive(true);
				grid.getCurrent().get(21).get(25).setAlive(true);
				grid.getCurrent().get(21).get(26).setAlive(true);
				grid.getCurrent().get(22).get(19).setAlive(true);
				grid.getCurrent().get(22).get(20).setAlive(true);
				grid.getCurrent().get(22).get(26).setAlive(true);
				grid.getCurrent().get(22).get(27).setAlive(true);
				break;
			case GOSPER_GLIDER_GUN:
				grid.getCurrent().get(10).get(29).setAlive(true);
				grid.getCurrent().get(11).get(27).setAlive(true);
				grid.getCurrent().get(11).get(29).setAlive(true);
				grid.getCurrent().get(12).get(17).setAlive(true);
				grid.getCurrent().get(12).get(18).setAlive(true);
				grid.getCurrent().get(12).get(25).setAlive(true);
				grid.getCurrent().get(12).get(26).setAlive(true);
				grid.getCurrent().get(12).get(39).setAlive(true);
				grid.getCurrent().get(12).get(40).setAlive(true);
				grid.getCurrent().get(13).get(16).setAlive(true);
				grid.getCurrent().get(13).get(20).setAlive(true);
				grid.getCurrent().get(13).get(25).setAlive(true);
				grid.getCurrent().get(13).get(26).setAlive(true);
				grid.getCurrent().get(13).get(39).setAlive(true);
				grid.getCurrent().get(13).get(40).setAlive(true);
				grid.getCurrent().get(14).get(5).setAlive(true);
				grid.getCurrent().get(14).get(6).setAlive(true);
				grid.getCurrent().get(14).get(15).setAlive(true);
				grid.getCurrent().get(14).get(21).setAlive(true);
				grid.getCurrent().get(14).get(25).setAlive(true);
				grid.getCurrent().get(14).get(26).setAlive(true);
				grid.getCurrent().get(15).get(5).setAlive(true);
				grid.getCurrent().get(15).get(6).setAlive(true);
				grid.getCurrent().get(15).get(15).setAlive(true);
				grid.getCurrent().get(15).get(19).setAlive(true);
				grid.getCurrent().get(15).get(21).setAlive(true);
				grid.getCurrent().get(15).get(22).setAlive(true);
				grid.getCurrent().get(15).get(27).setAlive(true);
				grid.getCurrent().get(15).get(29).setAlive(true);
				grid.getCurrent().get(16).get(15).setAlive(true);
				grid.getCurrent().get(16).get(21).setAlive(true);
				grid.getCurrent().get(16).get(29).setAlive(true);
				grid.getCurrent().get(17).get(16).setAlive(true);
				grid.getCurrent().get(17).get(20).setAlive(true);
				grid.getCurrent().get(18).get(17).setAlive(true);
				grid.getCurrent().get(18).get(18).setAlive(true);
				break;
			default:
				// empty board
		}
		return Boolean.FALSE;
	}
	
}
