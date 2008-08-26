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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import agave.Destination;
import agave.HandlesRequestsTo;
import agave.exception.HandlerException;
import agave.samples.gameOfLife.simulation.Board;
import agave.samples.gameOfLife.simulation.Cell;
import agave.samples.gameOfLife.simulation.State;
import agave.samples.gameOfLife.simulation.Tick;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import freemarker.template.TemplateException;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class GameOfLifeHandler extends FreemarkerHandler {
    
    public static final String BOARD_KEY = "board";
    
    @HandlesRequestsTo("/init/${configuration}")
    public void init(GameOfLifeForm form) throws HandlerException, IOException, TemplateException {
        Board board = new Board(form.getRows(), form.getColumns());
        Configuration configuration = form.getConfiguration();
        configuration.initialize(board);
        Map<String, Object> templateModel = buildTemplateModel(board);
        request.getSession().setAttribute(BOARD_KEY, board);
        displayTemplate(templateModel, response.getWriter());
    }
    
    @HandlesRequestsTo("/advance")
    public Destination advance(GameOfLifeForm form) throws HandlerException, IOException, TemplateException {
        Destination destination = null;
        Board board = (Board)request.getSession().getAttribute(BOARD_KEY);
        if (board != null) {
            board.advance();
            Map<String, Object> templateModel = buildTemplateModel(board);
            request.getSession().setAttribute(BOARD_KEY, board);
            displayTemplate(templateModel, response.getWriter());
        } else {
            destination = new Destination("/init", true);
        }
        return destination;
    }
    
    @HandlesRequestsTo("/play")
    public Destination play(GameOfLifeForm form) throws HandlerException, IOException, TemplateException {
        Destination destination = null;
        Board board = (Board)request.getSession().getAttribute(BOARD_KEY);
        if (board != null) {
            Tick tick = board.advance();
            request.getSession().setAttribute(BOARD_KEY, board);
            response.setContentType("application/json");
            
            Gson serializer = new Gson();
            Type typeOfSrc = new TypeToken<Tick>(){}.getType();
            
            PrintWriter out = response.getWriter();
            out.print(serializer.toJson(tick, typeOfSrc));
            out.close();
        } else {
            destination = new Destination("/init", true);
        }
        return destination;
    }
    
    @HandlesRequestsTo("/toggleState")
    public void makeAlive(GameOfLifeForm form) {
        Board board = (Board)request.getSession().getAttribute(BOARD_KEY);
        Cell clickedCell = board.getGrid().get(form.getY()).get(form.getX());
        if (clickedCell.getState() == State.ALIVE) {
            clickedCell.setState(State.DEAD);
        } else {
            clickedCell.setState(State.ALIVE);
        }
        board.associateNeighbors();
        request.getSession().setAttribute(BOARD_KEY, board);
    }
    
    private Map<String, Object> buildTemplateModel(Board board) {
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("contextPath", request.getContextPath());
        templateModel.put("board", board);
        templateModel.put("play", false);
        return templateModel;
    }
    
}
