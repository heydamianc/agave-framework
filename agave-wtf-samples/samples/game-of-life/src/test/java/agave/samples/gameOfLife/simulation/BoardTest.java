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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class BoardTest {
    
    @Test
    public void testConstructor() {
        Assert.assertNotNull(new Board(5, 5));
    }
    
    @Test
    public void testBoardWithBlockConfiguration() {
        List<Position> alive = new ArrayList<Position>();
        alive.add(new Position(3,3));
        alive.add(new Position(3,4));
        alive.add(new Position(4,3));
        alive.add(new Position(4,4));
        Board board = new Board(5, 5, alive);
        
        Assert.assertTrue(board.getAliveCellPositions().containsAll(alive));
        Assert.assertEquals(board.getAliveCellPositions().size(), alive.size());

        board.advance();
        
        Assert.assertTrue(board.getAliveCellPositions().containsAll(alive));
        Assert.assertEquals(board.getAliveCellPositions().size(), alive.size());
    }

    @Test
    public void testBoardWithBlinkerConfiguration() {
        List<Position> alive = new ArrayList<Position>();
        alive.add(new Position(2, 3));
        alive.add(new Position(3, 3));
        alive.add(new Position(4, 3));
        Board board = new Board(5, 5, alive);
        
        Assert.assertTrue(board.getAliveCellPositions().containsAll(alive));
        Assert.assertEquals(board.getAliveCellPositions().size(), alive.size());
        
        board.advance();
        
        List<Position> aliveAfterAdvance = new ArrayList<Position>();
        aliveAfterAdvance.add(new Position(3, 2));
        aliveAfterAdvance.add(new Position(3, 3));
        aliveAfterAdvance.add(new Position(3, 4));
        
        Assert.assertTrue(board.getAliveCellPositions().containsAll(aliveAfterAdvance));
        Assert.assertEquals(board.getAliveCellPositions().size(), aliveAfterAdvance.size());
    }
    
    @Test
    public void testMakeAlive() {
        Board board = new Board(5, 5);
        Assert.assertFalse(board.getGrid().get(2).get(2).isAlive());
        board.makeAlive(2, 2);
        Assert.assertTrue(board.getGrid().get(2).get(2).isAlive());
    }
    
}
