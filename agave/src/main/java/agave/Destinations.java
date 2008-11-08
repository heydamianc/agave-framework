package agave;

import agave.internal.DestinationImpl;

public final class Destinations {

	public static Destination create(String path) {
		return new DestinationImpl(path);
	}
	
	public static Destination redirect(String path) {
		return new DestinationImpl(path, true);
	}
	
	public static Destination forward(String path) {
		return new DestinationImpl(path, false);
	}
	
}
