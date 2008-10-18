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
package agave.samples.gameOfLife.simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class Board implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Tick tick;
    private int rows;
    private int columns;
    private List<List<Cell>> grid;
    
    public Board(int rows, int columns) {
        this(rows, columns, null);
    }
    
    public Board(int rows, int columns, List<Position> initiallyAlive) {
        this.tick = new Tick();
        this.rows = rows;
        this.columns = columns;
        grid = new ArrayList<List<Cell>>(rows);

        for (int row = 0; row < rows; row++) {
            grid.add(new ArrayList<Cell>(columns));
            for (int column = 0; column < columns; column++) {
                Position position = new Position(row, column);
                State state = State.DEAD;
                if (initiallyAlive != null && initiallyAlive.contains(position)) {
                    state = State.ALIVE;
                }
                grid.get(row).add(new Cell(position, state));
            }
        }
        associateNeighbors();
    }
    
    public void associateNeighbors() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Cell cell = grid.get(row).get(column);
                
                if (row > 0) {
                    if (column > 0) {
                        cell.addNeighbor(grid.get(row - 1).get(column - 1));
                    }
                    cell.addNeighbor(grid.get(row - 1).get(column));
                    if (column < columns - 1) {
                        cell.addNeighbor(grid.get(row - 1).get(column + 1));
                    }
                }
                
                if (column > 0) {
                   cell.addNeighbor(grid.get(row).get(column - 1));
                }
                if (column < columns - 1) {
                    cell.addNeighbor(grid.get(row).get(column + 1));
                }
                
                if (row < rows - 1) {
                    if (column > 0) {
                        cell.addNeighbor(grid.get(row + 1).get(column - 1));
                    }
                    cell.addNeighbor(grid.get(row + 1).get(column));
                    if (column < columns - 1) {
                        cell.addNeighbor(grid.get(row + 1).get(column + 1));
                    }
                }
            }
        }
    }
    
    public Tick advance() {
        List<List<Cell>> previousGrid = grid;
        grid = new ArrayList<List<Cell>>(rows);
        
        for (int row = 0; row < rows; row++) {
            grid.add(new ArrayList<Cell>(columns));
            for (int column = 0; column < columns; column++) {
                grid.get(row).add(previousGrid.get(row).get(column).advance());
            }
        }
        associateNeighbors();
        return tick.advance(previousGrid, grid);
    }
    
    public List<Position> getAliveCellPositions() {
        List<Position> aliveCellPositions = new ArrayList<Position>();
        for (List<Cell> row : grid) {
            for (Cell cell : row) {
                if (cell.isAlive()) {
                    aliveCellPositions.add(cell.getPosition());
                }
            }
        }
        return aliveCellPositions;
    }
    
    public String toJSON() {
        return null;
    }

    public Tick getTick() {
        return tick;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public List<List<Cell>> getGrid() {
        return grid;
    }

    public void makeAlive(int x, int y) {
        if (grid != null) {
            grid.get(x).get(y).setState(State.ALIVE);
        }
    }

    
}
