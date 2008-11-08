package agave;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

public interface Destination {

	/**
	 * Adds a parameter to the {@code Destination}.  Multi-valued parameters are supported
	 * and will be serialized into a query string with the same parameter name and multiple
	 * values, eg: {@code /somePath?dogs=woof&dogs=bark&cats=meow&cats=purr}
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 */
	public abstract void addParameter(String name, String value);

	public abstract String getPath();

	public abstract void setPath(String path);

	public abstract Map<String, List<String>> getParameters();

	public abstract void setParameters(Map<String, List<String>> parameters);

	public abstract Boolean getRedirect();

	public abstract void setRedirect(Boolean redirect);

	/**
	 * Encodes the {@code Destination} path and available parameters. The parameters have any ampersands
	 * replaced with &{@code &amp;amp;} and parameter names and their associated values are sorted (mainly for
	 * testing purposes).
	 * 
	 * @param context
	 * @return
	 */
	public abstract String encode(ServletContext context);

}