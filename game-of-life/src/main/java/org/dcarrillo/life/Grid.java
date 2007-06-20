package org.dcarrillo.life;

import java.util.ArrayList;
import java.util.List;

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