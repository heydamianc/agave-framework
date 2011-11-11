package co.cdev.agave.sample;

import co.cdev.agave.conversion.StringParamConverter;
import java.util.Locale;

public class FavoritePopStarConverter implements StringParamConverter<FavoritePopStar> {

    public FavoritePopStar convert(String input, Locale locale) {
        return new FavoritePopStar(input);
    }

}
