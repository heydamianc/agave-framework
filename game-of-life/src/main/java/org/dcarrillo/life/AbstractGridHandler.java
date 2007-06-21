package org.dcarrillo.life;

import agave.StringTemplateHandler;
import agave.HandlerContext;
import agave.HandlerException;
import agave.annotations.Converter;
import agave.converters.IntegerConverter;

import javax.servlet.http.HttpSession;

import java.io.IOException;

import org.dcarrillo.life.Grid;
import org.antlr.stringtemplate.StringTemplate;

public abstract class AbstractGridHandler extends StringTemplateHandler {

	static final String GRID       = "grid";
	static final String RUNNING    = "running";
	static final String ITERATIONS = "iterations";

	private int row;
	private int col;
	
	public final int getRow() {
		return row;
	}
	
	@Converter(IntegerConverter.class)
	public void setRow(final int row) {
		this.row = row;
	}
	
	public final int getCol() {
		return col;
	}
	
	@Converter(IntegerConverter.class)
	public final void setCol(final int col) {
		this.col = col;
	}
	
	public final void prepareTemplate(final HandlerContext context, final StringTemplate template) 
	throws HandlerException, IOException {
		HttpSession session = context.getRequest().getSession(true);
		
		Grid grid = (Grid)session.getAttribute(GRID);
		if (grid == null) {
			grid = new Grid(getRow(), getCol());
		}
		
		Boolean running = (Boolean)session.getAttribute(RUNNING);
		if (running == null) {
			running = Boolean.FALSE;
		}
		
		running = updateGrid(context, template, grid, running);
		
		Integer iterations = (Integer)session.getAttribute(ITERATIONS);
		if (iterations != null && running) {
			iterations = iterations + 1;
		}
		
		template.setAttribute(GRID, grid.getCurrent());
		template.setAttribute(RUNNING, running);
		if (iterations > 0) {
			template.setAttribute(ITERATIONS, iterations);
		}
		
		session.setAttribute(GRID, grid);
		session.setAttribute(RUNNING, running);
		session.setAttribute(ITERATIONS, iterations);
	}
	
	protected abstract Boolean updateGrid(
		final HandlerContext context,
		final StringTemplate template,
		final Grid grid,
		Boolean running
	) throws HandlerException, IOException;
	
}
