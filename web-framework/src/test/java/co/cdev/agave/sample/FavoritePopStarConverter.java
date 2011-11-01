package co.cdev.agave.sample;

import co.cdev.agave.conversion.StringConverter;
import java.util.Locale;

public class FavoritePopStarConverter implements StringConverter<FavoritePopStar> {

    public FavoritePopStar convert(String input, Locale locale) {
        return new FavoritePopStar(input);
    }

}
