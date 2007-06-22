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
import agave.ResourceHandler;
import agave.annotations.ContentType;
import agave.annotations.Path;
import agave.annotations.PositionalParameters;
import agave.annotations.Template;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Dump the coordinates of all live cells to the screen.
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo>
 */
@Path("/dump")
@ContentType(ContentType.TEXT_PLAIN)
public final class Dump implements ResourceHandler {
	
    public void render(HandlerContext context) 
	throws HandlerException, IOException {
		HttpSession session = context.getRequest().getSession(true);
		Grid grid = (Grid)session.getAttribute(AbstractGridHandler.GRID);
		PrintWriter out = context.getResponse().getWriter();
		
		for (int r = 0; r < grid.getCurrent().size(); r++) {
			List<Cell> row = grid.getCurrent().get(r);
			for (int c = 0; c < row.size(); c++) {
				Cell cell = row.get(c);
				if (cell.isAlive()) {
					out.println(r + ", " + c);
				}
			}
		}
		
		out.close();
	}
	
}