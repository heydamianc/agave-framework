package org.dcarrillo.life;

import agave.HandlerContext;
import agave.HandlerException;
import agave.annotations.ContentType;
import agave.annotations.Path;
import agave.annotations.PositionalParameters;
import agave.annotations.Template;

import java.io.IOException;

import org.antlr.stringtemplate.StringTemplate;

@Path("/toggle")
@PositionalParameters({"row", "col"})
@ContentType(ContentType.APPLICATION_XHTML_XML)
@Template(path = "/", name = "display")
public final class Toggle extends AbstractGridHandler {
	
	protected Boolean updateGrid(
		final HandlerContext context,
		final StringTemplate template,
		final Grid grid,
		Boolean running
	) throws HandlerException, IOException {
		Cell cell = grid.getCurrent().get(getRow()).get(getCol());
		cell.toggle();
		
		if (running) {
			context.getResponse().sendRedirect(context.getRequest().getContextPath() + "/run");
		}
		
		return running;
	}
	
}