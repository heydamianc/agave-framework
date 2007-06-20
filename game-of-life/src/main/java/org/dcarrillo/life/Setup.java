package org.dcarrillo.life;

import agave.HandlerContext;
import agave.HandlerException;
import agave.annotations.ContentType;
import agave.annotations.Path;
import agave.annotations.Template;

import java.io.IOException;

import org.antlr.stringtemplate.StringTemplate;

@Path("/setup")
@ContentType(ContentType.APPLICATION_XHTML_XML)
@Template(path = "/", name = "display")
public final class Setup extends AbstractGridHandler {
	
	public Setup() {
		setRow(25);
		setCol(40);
	}
	
	protected Boolean updateGrid(
		final HandlerContext context,
		final StringTemplate template,
		final Grid grid,
		Boolean running
	) throws HandlerException, IOException {
		grid.getCurrent().get(10).get(13).setAlive(true);
		grid.getCurrent().get(11).get(14).setAlive(true);
		grid.getCurrent().get(12).get(12).setAlive(true);
		grid.getCurrent().get(12).get(13).setAlive(true);
		grid.getCurrent().get(12).get(14).setAlive(true);
		return Boolean.FALSE;
	}
	
}