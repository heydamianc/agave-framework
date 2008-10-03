package agave.internal;

import agave.conversion.StringConverter;

public class FavoritePopStarConverter implements StringConverter<FavoritePopStar> {

    public FavoritePopStar convert(String input) {
        return new FavoritePopStar(input);
    }

}
