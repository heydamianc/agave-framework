package org.dcarrillo.life;

import agave.HandlerContext;
import agave.HandlerException;
import agave.annotations.ContentType;
import agave.annotations.Converter;
import agave.annotations.Path;
import agave.annotations.PositionalParameters;
import agave.annotations.Template;
import agave.converters.ConversionException;

import java.io.IOException;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;

@Path("/setup")
@PositionalParameters("pattern")
@ContentType(ContentType.APPLICATION_XHTML_XML)
@Template(path = "/", name = "display")
public final class Setup extends AbstractGridHandler {
	
	public static class PatternConverter implements agave.converters.Converter<Pattern> {
		public Pattern convert(String value) throws ConversionException {
			return Enum.valueOf(Pattern.class, value);
		}
	}
	
	private enum Pattern {
		block,
		boat,
		blinker,
		toad,
		glider,
		lwss,
		pulsar,
		gosper_glider_gun
	}
	
	private Pattern pattern;
	
	public Setup() {
		setRow(25);
		setCol(40);
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	@Converter(PatternConverter.class)
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	protected Boolean updateGrid(
		final HandlerContext context,
		final StringTemplate template,
		final Grid grid,
		Boolean running
	) throws HandlerException, IOException {
		if (getPattern() == null) {
			setPattern(Pattern.glider);
		}
		
		for (List<Cell> row : grid.getCurrent()) {
			for (Cell cell : row) {
				cell.setAlive(false);
			}
		}
		
		switch(getPattern()) {
			case block:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(11).get(13).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				break;
			case boat:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(11).get(13).setAlive(true);
				grid.getCurrent().get(11).get(15).setAlive(true);
				grid.getCurrent().get(12).get(14).setAlive(true);
				break;
			case blinker:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(10).get(13).setAlive(true);
				break;
			case toad:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(10).get(15).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				grid.getCurrent().get(11).get(15).setAlive(true);
				grid.getCurrent().get(11).get(16).setAlive(true);
				break;
			default: // a glider
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				grid.getCurrent().get(12).get(12).setAlive(true);
				grid.getCurrent().get(12).get(13).setAlive(true);
				grid.getCurrent().get(12).get(14).setAlive(true);
		}
		return Boolean.FALSE;
	}
	
}