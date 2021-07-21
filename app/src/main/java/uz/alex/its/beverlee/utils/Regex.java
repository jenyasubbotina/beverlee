package uz.alex.its.beverlee.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static boolean isPhoneNumber(final String phone) {
        final Pattern pattern = Pattern.compile(PHONE_PATTERN);
        final Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private static final String PHONE_PATTERN = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
}
