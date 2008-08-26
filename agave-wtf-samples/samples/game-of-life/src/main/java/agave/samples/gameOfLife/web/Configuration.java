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
package agave.samples.gameOfLife.web;

import java.util.List;

import agave.samples.gameOfLife.simulation.Board;
import agave.samples.gameOfLife.simulation.Cell;
import agave.samples.gameOfLife.simulation.State;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public enum Configuration {

    Blinker, Toad, Glider, LightweightSpaceship, Pulsar, GosperGliderGun;

    public void initialize(Board board) {
        int row = Math.round(board.getRows() / 2.0f);
        int col = Math.round(board.getColumns() / 2.0f);
        List<List<Cell>> grid = board.getGrid();
        switch (this) {
            case Toad:
                grid.get(row - 1).get(col).setState(State.ALIVE);
                grid.get(row).get(col).setState(State.ALIVE);
                grid.get(row + 1).get(col).setState(State.ALIVE);
                grid.get(row).get(col + 1).setState(State.ALIVE);
                grid.get(row + 1).get(col + 1).setState(State.ALIVE);
                grid.get(row + 2).get(col + 1).setState(State.ALIVE);
                break;
            case Glider:
                grid.get(row - 1).get(col).setState(State.ALIVE);
                grid.get(row).get(col + 1).setState(State.ALIVE);
                grid.get(row + 1).get(col - 1).setState(State.ALIVE);
                grid.get(row + 1).get(col).setState(State.ALIVE);
                grid.get(row + 1).get(col + 1).setState(State.ALIVE);
                break;
            case LightweightSpaceship:
                grid.get(row - 2).get(col - 1).setState(State.ALIVE);
                grid.get(row - 2).get(col + 2).setState(State.ALIVE);
                grid.get(row - 1).get(col - 2).setState(State.ALIVE);
                grid.get(row).get(col - 2).setState(State.ALIVE);
                grid.get(row).get(col + 2).setState(State.ALIVE);
                grid.get(row + 1).get(col - 2).setState(State.ALIVE);
                grid.get(row + 1).get(col - 1).setState(State.ALIVE);
                grid.get(row + 1).get(col - 0).setState(State.ALIVE);
                grid.get(row + 1).get(col + 1).setState(State.ALIVE);
                break;
            case Pulsar:
                grid.get(row - 6).get(col - 4).setState(State.ALIVE);
                grid.get(row - 6).get(col - 3).setState(State.ALIVE);
                grid.get(row - 6).get(col + 3).setState(State.ALIVE);
                grid.get(row - 6).get(col + 4).setState(State.ALIVE);
                grid.get(row - 5).get(col - 3).setState(State.ALIVE);
                grid.get(row - 5).get(col - 2).setState(State.ALIVE);
                grid.get(row - 5).get(col + 2).setState(State.ALIVE);
                grid.get(row - 5).get(col + 3).setState(State.ALIVE);
                grid.get(row - 4).get(col - 6).setState(State.ALIVE);
                grid.get(row - 4).get(col - 3).setState(State.ALIVE);
                grid.get(row - 4).get(col - 1).setState(State.ALIVE);
                grid.get(row - 4).get(col + 1).setState(State.ALIVE);
                grid.get(row - 4).get(col + 3).setState(State.ALIVE);
                grid.get(row - 4).get(col + 6).setState(State.ALIVE);
                grid.get(row - 3).get(col - 6).setState(State.ALIVE);
                grid.get(row - 3).get(col - 5).setState(State.ALIVE);
                grid.get(row - 3).get(col - 4).setState(State.ALIVE);
                grid.get(row - 3).get(col - 2).setState(State.ALIVE);
                grid.get(row - 3).get(col - 1).setState(State.ALIVE);
                grid.get(row - 3).get(col + 1).setState(State.ALIVE);
                grid.get(row - 3).get(col + 2).setState(State.ALIVE);
                grid.get(row - 3).get(col + 4).setState(State.ALIVE);
                grid.get(row - 3).get(col + 5).setState(State.ALIVE);
                grid.get(row - 3).get(col + 6).setState(State.ALIVE);
                grid.get(row - 2).get(col - 5).setState(State.ALIVE);
                grid.get(row - 2).get(col - 3).setState(State.ALIVE);
                grid.get(row - 2).get(col - 1).setState(State.ALIVE);
                grid.get(row - 2).get(col + 1).setState(State.ALIVE);
                grid.get(row - 2).get(col + 3).setState(State.ALIVE);
                grid.get(row - 2).get(col + 5).setState(State.ALIVE);
                grid.get(row - 1).get(col - 4).setState(State.ALIVE);
                grid.get(row - 1).get(col - 3).setState(State.ALIVE);
                grid.get(row - 1).get(col - 2).setState(State.ALIVE);
                grid.get(row - 1).get(col + 2).setState(State.ALIVE);
                grid.get(row - 1).get(col + 3).setState(State.ALIVE);
                grid.get(row - 1).get(col + 4).setState(State.ALIVE);
                grid.get(row + 1).get(col - 4).setState(State.ALIVE);
                grid.get(row + 1).get(col - 3).setState(State.ALIVE);
                grid.get(row + 1).get(col - 2).setState(State.ALIVE);
                grid.get(row + 1).get(col + 2).setState(State.ALIVE);
                grid.get(row + 1).get(col + 3).setState(State.ALIVE);
                grid.get(row + 1).get(col + 4).setState(State.ALIVE);
                grid.get(row + 2).get(col - 5).setState(State.ALIVE);
                grid.get(row + 2).get(col - 3).setState(State.ALIVE);
                grid.get(row + 2).get(col - 1).setState(State.ALIVE);
                grid.get(row + 2).get(col + 1).setState(State.ALIVE);
                grid.get(row + 2).get(col + 3).setState(State.ALIVE);
                grid.get(row + 2).get(col + 5).setState(State.ALIVE);
                grid.get(row + 3).get(col - 6).setState(State.ALIVE);
                grid.get(row + 3).get(col - 5).setState(State.ALIVE);
                grid.get(row + 3).get(col - 4).setState(State.ALIVE);
                grid.get(row + 3).get(col - 2).setState(State.ALIVE);
                grid.get(row + 3).get(col - 1).setState(State.ALIVE);
                grid.get(row + 3).get(col + 1).setState(State.ALIVE);
                grid.get(row + 3).get(col + 2).setState(State.ALIVE);
                grid.get(row + 3).get(col + 4).setState(State.ALIVE);
                grid.get(row + 3).get(col + 5).setState(State.ALIVE);
                grid.get(row + 3).get(col + 6).setState(State.ALIVE);
                grid.get(row + 4).get(col - 6).setState(State.ALIVE);
                grid.get(row + 4).get(col - 3).setState(State.ALIVE);
                grid.get(row + 4).get(col - 1).setState(State.ALIVE);
                grid.get(row + 4).get(col + 1).setState(State.ALIVE);
                grid.get(row + 4).get(col + 3).setState(State.ALIVE);
                grid.get(row + 4).get(col + 6).setState(State.ALIVE);
                grid.get(row + 5).get(col - 3).setState(State.ALIVE);
                grid.get(row + 5).get(col - 2).setState(State.ALIVE);
                grid.get(row + 5).get(col + 2).setState(State.ALIVE);
                grid.get(row + 5).get(col + 3).setState(State.ALIVE);
                grid.get(row + 6).get(col - 4).setState(State.ALIVE);
                grid.get(row + 6).get(col - 3).setState(State.ALIVE);
                grid.get(row + 6).get(col + 3).setState(State.ALIVE);
                grid.get(row + 6).get(col + 4).setState(State.ALIVE);
                break;
            case GosperGliderGun: // 9 x 36
                grid.get(row - 4).get(col + 7).setState(State.ALIVE);
                grid.get(row - 3).get(col + 5).setState(State.ALIVE);
                grid.get(row - 3).get(col + 7).setState(State.ALIVE);
                grid.get(row - 2).get(col - 5).setState(State.ALIVE);
                grid.get(row - 2).get(col - 4).setState(State.ALIVE);
                grid.get(row - 2).get(col + 3).setState(State.ALIVE);
                grid.get(row - 2).get(col + 4).setState(State.ALIVE);
                grid.get(row - 2).get(col + 17).setState(State.ALIVE);
                grid.get(row - 2).get(col + 18).setState(State.ALIVE);
                grid.get(row - 1).get(col - 6).setState(State.ALIVE);
                grid.get(row - 1).get(col - 2).setState(State.ALIVE);
                grid.get(row - 1).get(col + 3).setState(State.ALIVE);
                grid.get(row - 1).get(col + 4).setState(State.ALIVE);
                grid.get(row - 1).get(col + 17).setState(State.ALIVE);
                grid.get(row - 1).get(col + 18).setState(State.ALIVE);
                grid.get(row).get(col - 17).setState(State.ALIVE);
                grid.get(row).get(col - 16).setState(State.ALIVE);
                grid.get(row).get(col - 7).setState(State.ALIVE);
                grid.get(row).get(col - 1).setState(State.ALIVE);
                grid.get(row).get(col + 3).setState(State.ALIVE);
                grid.get(row).get(col + 4).setState(State.ALIVE);
                grid.get(row + 1).get(col - 17).setState(State.ALIVE);
                grid.get(row + 1).get(col - 16).setState(State.ALIVE);
                grid.get(row + 1).get(col - 7).setState(State.ALIVE);
                grid.get(row + 1).get(col - 3).setState(State.ALIVE);
                grid.get(row + 1).get(col - 1).setState(State.ALIVE);
                grid.get(row + 1).get(col).setState(State.ALIVE);
                grid.get(row + 1).get(col + 5).setState(State.ALIVE);
                grid.get(row + 1).get(col + 7).setState(State.ALIVE);
                grid.get(row + 2).get(col - 7).setState(State.ALIVE);
                grid.get(row + 2).get(col - 1).setState(State.ALIVE);
                grid.get(row + 2).get(col + 7).setState(State.ALIVE);
                grid.get(row + 3).get(col - 6).setState(State.ALIVE);
                grid.get(row + 3).get(col - 2).setState(State.ALIVE);
                grid.get(row + 4).get(col - 5).setState(State.ALIVE);
                grid.get(row + 4).get(col - 4).setState(State.ALIVE);
                break;
            default: // Blinker
                grid.get(row - 1).get(col).setState(State.ALIVE);
                grid.get(row).get(col).setState(State.ALIVE);
                grid.get(row + 1).get(col).setState(State.ALIVE);
        }
    }

}

