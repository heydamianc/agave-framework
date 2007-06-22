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

import java.util.ArrayList;
import java.util.List;

/**
 * The Grid model class is the representation of the grid on screen.  It is
 * constructed by having a list of lists of cells.  The outermost list 
 * represents the grid, and the inner lists represent the rows.
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo>
 */
public final class Grid {
	
	private List<List<Cell>> current;
	private List<List<Cell>> evolution;
	
	public Grid(int rows, int cols) {
		current = new ArrayList<List<Cell>>();
		for (int i = 0; i < rows; i++) {
			List<Cell> row = new ArrayList<Cell>();
			for (int j = 0; j < cols; j++) {
				row.add(new Cell(i, j, false));
			}
			current.add(row);
		}
	}
	
	public List<List<Cell>> getCurrent() {
		return current;
	}
	
	public void setCurrent(List<List<Cell>> current) {
		this.current = current;
	}
	
	public boolean evolve() {
		boolean evolved = false;
		evolution = new ArrayList<List<Cell>>();
		
		for (int r = 0; r < getCurrent().size(); ++r) {
			
			List<Cell> row = getCurrent().get(r);
			List<Cell> evolvedRow = new ArrayList<Cell>();
			
			for (int c = 0; c < row.size(); ++c) {
				Cell cell = row.get(c);
				Cell evolvedCell = (Cell)cell.clone();
				List<Cell> neighbors = new ArrayList<Cell>();
				
				if (r - 1 >= 0) {
					if (c - 1 >= 0)
						neighbors.add(getCurrent().get(r - 1).get(c - 1));
					
					neighbors.add(getCurrent().get(r - 1).get(c));
					
					if (c + 1 < row.size())
						neighbors.add(getCurrent().get(r - 1).get(c + 1));
				}
				
				if (c - 1 >= 0) {
					neighbors.add(getCurrent().get(r).get(c - 1));
				}
				
				if (c + 1< row.size()) {
					neighbors.add(getCurrent().get(r).get(c + 1));
				}
				
				if (r + 1 < getCurrent().size()) {
					if (c - 1 >= 0)
						neighbors.add(getCurrent().get(r + 1).get(c - 1));
					
					neighbors.add(getCurrent().get(r + 1).get(c));
					
					if (c + 1 < row.size())
						neighbors.add(getCurrent().get(r + 1).get(c + 1));
				}
				
				evolvedCell.setNeighbors(neighbors);
				evolved |= evolvedCell.evolve();
				evolvedRow.add(evolvedCell);
			}
			evolution.add(evolvedRow);
		}
		setCurrent(evolution);
		return evolved;
	}
	
}