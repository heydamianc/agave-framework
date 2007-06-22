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

import java.util.List;

/**
 * The cell model class that represents a cell in the board.  Each cell 
 * can be alive or dead, and if it is alive, it will continue living if it 
 * has 2 or 3 live neighbors, otherwise it will die.  If it is dead and has
 * exactly three neighbors it will come to life.  Neighbors of a cell are 
 * all the cells immediately surrounding it.  A cell can have up to 8 
 * neighbors if it is not on the border of the grid.  It can have as little
 * as 3 neighbors if it is in the corner.
 *
 * Cells are actually sort of "dumb" in that they need the grid to coordinate
 * how many neighbors they have. They can't immediately know this, but if 
 * a cell knows how many neighbors it has, it can effectively evolve itself.
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo>
 */
public final class Cell implements Cloneable {
	
	private List<Cell> neighbors;
	private int row;
	private int col;
	private boolean alive = false;
	
	public Cell (int row, int col, boolean alive) {
		setRow(row);
		setCol(col);
		setAlive(alive);
	}
	
	public List<Cell> getNeighbors() {
		return neighbors;
	}
	
	public void setNeighbors(List<Cell> neighbors) {
		this.neighbors = neighbors;
	}
	
	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	public boolean isAlive() {
		return alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public void toggle() {
		alive = !alive;
	}
	
	public boolean evolve() {
		boolean evolved = false;
		int aliveNeighbors = 0;
		
		if (getNeighbors() != null) {
			for (Cell neighbor : getNeighbors()) {
				aliveNeighbors += neighbor.isAlive() ? 1 : 0;
			}
		
			if (isAlive()) {
				if (aliveNeighbors < 2 || aliveNeighbors > 3) {
					evolved = true;
					toggle();
				}
			} else {
				if (aliveNeighbors == 3) {
					evolved = true;
					toggle();
				}
			}
			neighbors = null;
		}
		
		return evolved;
	}
	
	@Override
	public Object clone() {
		return new Cell(getRow(), getCol(), isAlive());
	}
}
