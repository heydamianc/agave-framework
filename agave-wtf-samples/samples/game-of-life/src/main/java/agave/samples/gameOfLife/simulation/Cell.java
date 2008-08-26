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
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class Cell implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    
    private State state;
    private Position position;
    private Map<Direction, Cell> neighbors;
    
    public Cell(int row, int column, State state) {
        this(new Position(row, column), state);
    }
    
    public Cell(Position position, State state) {
        this.position = position;
        this.state = state;
        this.neighbors = new HashMap<Direction, Cell>();
    }
    
    public void addNeighbor(Cell neighbor) {
        Direction direction = null;
        
        if (neighbor.position.getRow() == position.getRow() - 1) {
            if (neighbor.position.getColumn() == position.getColumn() - 1) {
                direction = Direction.NORTHWEST;
            } else if (neighbor.position.getColumn() == position.getColumn()) {
                direction = Direction.NORTH;
            } else if (neighbor.position.getColumn() == position.getColumn() + 1) {
                direction = Direction.NORTHEAST;
            }
        } else if (neighbor.position.getRow() == position.getRow()) {
            if (neighbor.position.getColumn() == position.getColumn() - 1) {
                direction = Direction.WEST;
            } else if (neighbor.position.getColumn() == position.getColumn() + 1) {
                direction = Direction.EAST;
            }
        } else if (neighbor.position.getRow() == position.getRow() + 1) {
            if (neighbor.position.getColumn() == position.getColumn() - 1) {
                direction = Direction.SOUTHWEST;
            } else if (neighbor.position.getColumn() == position.getColumn()) {
                direction = Direction.SOUTH;
            } else if (neighbor.position.getColumn() == position.getColumn() + 1) {
                direction = Direction.SOUTHEAST;
            }
        }
        
        if (direction != null) {
            neighbors.put(direction, neighbor);
        }
    }
    
    public boolean isAlive() { 
        return state == State.ALIVE;
    }
    
    @Override
    public Object clone() {
        return new Cell((Position)position.clone(), state);
    }
    
    public Cell advance() {
        Cell advanced = (Cell)clone();
        int livingNeighbors = 0;
        
        for (Direction direction : neighbors.keySet()) {
            if (neighbors.get(direction).isAlive()) {
                ++livingNeighbors;
            }
        }
        
        if (isAlive()) {
            if (livingNeighbors < 2 || livingNeighbors > 3) {
                advanced.state = State.DEAD;
            }
        } else {
            if (livingNeighbors == 3) {
                advanced.state = State.ALIVE;
            }
        }
        
        return advanced;
    }
 
    public String toJSON() {
        return position.toJSON();
    }

    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }

    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }

    public Map<Direction, Cell> getNeighbors() {
        return neighbors;
    }
}
