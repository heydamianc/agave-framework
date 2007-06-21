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
			return Enum.valueOf(Pattern.class, value.toUpperCase());
		}
	}
	
	private enum Pattern {
		BLANK,
		BLOCK,
		BOAT,
		BLINKER,
		TOAD,
		GLIDER,
		LIGHTWEIGHT_SPACE_SHIP,
		PULSAR,
		GOSPER_GLIDER_GUN
	}
	
	private Pattern pattern;
	
	public Setup() {
		setRow(35);
		setCol(55);
		setPattern(Pattern.BLANK);
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
		context.getRequest().getSession().setAttribute(ITERATIONS, new Integer(0));
		
		for (List<Cell> row : grid.getCurrent()) {
			for (Cell cell : row) {
				cell.setAlive(false);
			}
		}
		
		switch(getPattern()) {
			case BLOCK:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(11).get(13).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				break;
			case BOAT:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(11).get(13).setAlive(true);
				grid.getCurrent().get(11).get(15).setAlive(true);
				grid.getCurrent().get(12).get(14).setAlive(true);
				break;
			case BLINKER:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(10).get(15).setAlive(true);
				break;
			case TOAD:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(10).get(14).setAlive(true);
				grid.getCurrent().get(10).get(15).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				grid.getCurrent().get(11).get(15).setAlive(true);
				grid.getCurrent().get(11).get(16).setAlive(true);
				break;
			case GLIDER:
				grid.getCurrent().get(10).get(13).setAlive(true);
				grid.getCurrent().get(11).get(14).setAlive(true);
				grid.getCurrent().get(12).get(12).setAlive(true);
				grid.getCurrent().get(12).get(13).setAlive(true);
				grid.getCurrent().get(12).get(14).setAlive(true);
				break;
			case LIGHTWEIGHT_SPACE_SHIP:
				grid.getCurrent().get(13).get(25).setAlive(true);
				grid.getCurrent().get(13).get(28).setAlive(true);
				grid.getCurrent().get(14).get(24).setAlive(true);
				grid.getCurrent().get(15).get(24).setAlive(true);
				grid.getCurrent().get(15).get(28).setAlive(true);
				grid.getCurrent().get(16).get(24).setAlive(true);
				grid.getCurrent().get(16).get(25).setAlive(true);
				grid.getCurrent().get(16).get(26).setAlive(true);
				grid.getCurrent().get(16).get(27).setAlive(true);
				break;
			case PULSAR:
				grid.getCurrent().get(10).get(19).setAlive(true);
				grid.getCurrent().get(10).get(20).setAlive(true);
				grid.getCurrent().get(10).get(26).setAlive(true);
				grid.getCurrent().get(10).get(27).setAlive(true);
				grid.getCurrent().get(11).get(20).setAlive(true);
				grid.getCurrent().get(11).get(21).setAlive(true);
				grid.getCurrent().get(11).get(25).setAlive(true);
				grid.getCurrent().get(11).get(26).setAlive(true);
				grid.getCurrent().get(12).get(17).setAlive(true);
				grid.getCurrent().get(12).get(20).setAlive(true);
				grid.getCurrent().get(12).get(22).setAlive(true);
				grid.getCurrent().get(12).get(24).setAlive(true);
				grid.getCurrent().get(12).get(26).setAlive(true);
				grid.getCurrent().get(12).get(29).setAlive(true);
				grid.getCurrent().get(13).get(17).setAlive(true);
				grid.getCurrent().get(13).get(18).setAlive(true);
				grid.getCurrent().get(13).get(19).setAlive(true);
				grid.getCurrent().get(13).get(21).setAlive(true);
				grid.getCurrent().get(13).get(22).setAlive(true);
				grid.getCurrent().get(13).get(24).setAlive(true);
				grid.getCurrent().get(13).get(25).setAlive(true);
				grid.getCurrent().get(13).get(27).setAlive(true);
				grid.getCurrent().get(13).get(28).setAlive(true);
				grid.getCurrent().get(13).get(29).setAlive(true);
				grid.getCurrent().get(14).get(18).setAlive(true);
				grid.getCurrent().get(14).get(20).setAlive(true);
				grid.getCurrent().get(14).get(22).setAlive(true);
				grid.getCurrent().get(14).get(24).setAlive(true);
				grid.getCurrent().get(14).get(26).setAlive(true);
				grid.getCurrent().get(14).get(28).setAlive(true);
				grid.getCurrent().get(15).get(19).setAlive(true);
				grid.getCurrent().get(15).get(20).setAlive(true);
				grid.getCurrent().get(15).get(21).setAlive(true);
				grid.getCurrent().get(15).get(25).setAlive(true);
				grid.getCurrent().get(15).get(26).setAlive(true);
				grid.getCurrent().get(15).get(27).setAlive(true);
				grid.getCurrent().get(17).get(19).setAlive(true);
				grid.getCurrent().get(17).get(20).setAlive(true);
				grid.getCurrent().get(17).get(21).setAlive(true);
				grid.getCurrent().get(17).get(25).setAlive(true);
				grid.getCurrent().get(17).get(26).setAlive(true);
				grid.getCurrent().get(17).get(27).setAlive(true);
				grid.getCurrent().get(18).get(18).setAlive(true);
				grid.getCurrent().get(18).get(20).setAlive(true);
				grid.getCurrent().get(18).get(22).setAlive(true);
				grid.getCurrent().get(18).get(24).setAlive(true);
				grid.getCurrent().get(18).get(26).setAlive(true);
				grid.getCurrent().get(18).get(28).setAlive(true);
				grid.getCurrent().get(19).get(17).setAlive(true);
				grid.getCurrent().get(19).get(18).setAlive(true);
				grid.getCurrent().get(19).get(19).setAlive(true);
				grid.getCurrent().get(19).get(21).setAlive(true);
				grid.getCurrent().get(19).get(22).setAlive(true);
				grid.getCurrent().get(19).get(24).setAlive(true);
				grid.getCurrent().get(19).get(25).setAlive(true);
				grid.getCurrent().get(19).get(27).setAlive(true);
				grid.getCurrent().get(19).get(28).setAlive(true);
				grid.getCurrent().get(19).get(29).setAlive(true);
				grid.getCurrent().get(20).get(17).setAlive(true);
				grid.getCurrent().get(20).get(20).setAlive(true);
				grid.getCurrent().get(20).get(22).setAlive(true);
				grid.getCurrent().get(20).get(24).setAlive(true);
				grid.getCurrent().get(20).get(26).setAlive(true);
				grid.getCurrent().get(20).get(29).setAlive(true);
				grid.getCurrent().get(21).get(20).setAlive(true);
				grid.getCurrent().get(21).get(21).setAlive(true);
				grid.getCurrent().get(21).get(25).setAlive(true);
				grid.getCurrent().get(21).get(26).setAlive(true);
				grid.getCurrent().get(22).get(19).setAlive(true);
				grid.getCurrent().get(22).get(20).setAlive(true);
				grid.getCurrent().get(22).get(26).setAlive(true);
				grid.getCurrent().get(22).get(27).setAlive(true);
				break;
			case GOSPER_GLIDER_GUN:
				grid.getCurrent().get(10).get(29).setAlive(true);
				grid.getCurrent().get(11).get(27).setAlive(true);
				grid.getCurrent().get(11).get(29).setAlive(true);
				grid.getCurrent().get(12).get(17).setAlive(true);
				grid.getCurrent().get(12).get(18).setAlive(true);
				grid.getCurrent().get(12).get(25).setAlive(true);
				grid.getCurrent().get(12).get(26).setAlive(true);
				grid.getCurrent().get(12).get(39).setAlive(true);
				grid.getCurrent().get(12).get(40).setAlive(true);
				grid.getCurrent().get(13).get(16).setAlive(true);
				grid.getCurrent().get(13).get(20).setAlive(true);
				grid.getCurrent().get(13).get(25).setAlive(true);
				grid.getCurrent().get(13).get(26).setAlive(true);
				grid.getCurrent().get(13).get(39).setAlive(true);
				grid.getCurrent().get(13).get(40).setAlive(true);
				grid.getCurrent().get(14).get(5).setAlive(true);
				grid.getCurrent().get(14).get(6).setAlive(true);
				grid.getCurrent().get(14).get(15).setAlive(true);
				grid.getCurrent().get(14).get(21).setAlive(true);
				grid.getCurrent().get(14).get(25).setAlive(true);
				grid.getCurrent().get(14).get(26).setAlive(true);
				grid.getCurrent().get(15).get(5).setAlive(true);
				grid.getCurrent().get(15).get(6).setAlive(true);
				grid.getCurrent().get(15).get(15).setAlive(true);
				grid.getCurrent().get(15).get(19).setAlive(true);
				grid.getCurrent().get(15).get(21).setAlive(true);
				grid.getCurrent().get(15).get(22).setAlive(true);
				grid.getCurrent().get(15).get(27).setAlive(true);
				grid.getCurrent().get(15).get(29).setAlive(true);
				grid.getCurrent().get(16).get(15).setAlive(true);
				grid.getCurrent().get(16).get(21).setAlive(true);
				grid.getCurrent().get(16).get(29).setAlive(true);
				grid.getCurrent().get(17).get(16).setAlive(true);
				grid.getCurrent().get(17).get(20).setAlive(true);
				grid.getCurrent().get(18)ß.get(17).setAlive(true);
				grid.getCurrent().get(18).get(18).setAlive(true);
				break;
			default:
				// empty board
		}
		return Boolean.FALSE;
	}
	
}
