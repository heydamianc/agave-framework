package org.dcarrillo.life;

import agave.HandlerContext;
import agave.HandlerException;
import agave.annotations.ContentType;
import agave.annotations.Path;
import agave.annotations.Template;

import java.io.IOException;

import org.antlr.stringtemplate.StringTemplate;

@Path("/stop")
@ContentType(ContentType.APPLICATION_XHTML_XML)
@Template(path = "/", name = "display")
public final class Stop extends AbstractGridHandler {
	
	protected Boolean updateGrid(
		final HandlerContext context,
		final StringTemplate template,
		final Grid grid,
		Boolean running
	) throws HandlerException, IOException {
		return Boolean.FALSE;
	}
	
}