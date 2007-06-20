package org.dcarrillo.life;

import java.util.List;

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
		return evolved;
	}
	
	@Override
	public Object clone() {
		return new Cell(getRow(), getCol(), isAlive());
	}
}
