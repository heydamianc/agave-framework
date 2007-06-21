package org.dcarrillo.life;

import agave.HandlerContext;
import agave.HandlerException;
import agave.ResourceHandler;
import agave.annotations.ContentType;
import agave.annotations.Path;
import agave.annotations.PositionalParameters;
import agave.annotations.Template;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Path("/dump")
@ContentType(ContentType.TEXT_PLAIN)
public final class Dump implements ResourceHandler {
	
    public void render(HandlerContext context) 
	throws HandlerException, IOException {
		HttpSession session = context.getRequest().getSession(true);
		Grid grid = (Grid)session.getAttribute(AbstractGridHandler.GRID);
		PrintWriter out = context.getResponse().getWriter();
		
		for (int r = 0; r < grid.getCurrent().size(); r++) {
			List<Cell> row = grid.getCurrent().get(r);
			for (int c = 0; c < row.size(); c++) {
				Cell cell = row.get(c);
				if (cell.isAlive()) {
					out.println(r + ", " + c);
				}
			}
		}
		
		out.close();
	}
	
}