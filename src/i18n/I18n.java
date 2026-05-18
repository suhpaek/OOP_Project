package i18n;

import enums.Language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class I18n {
    private static ResourceBundle bundle;
    private static Language currentLanguage = Language.EN;

    private I18n() {}

    public static void setLanguage(Language language) {
        if (language == null) language = Language.EN;
        currentLanguage = language;

        String suffix;
        switch (language) {
            case RU:
                suffix = "ru";
                break;
            case KZ:
                suffix = "kz";
                break;
            default:
                suffix = "en";
                break;
        }

        String resourcePath = "i18n/properties/" + suffix + ".properties";

        try {
            InputStream is = I18n.class.getClassLoader().getResourceAsStream(resourcePath);
            if (is != null) {
                bundle = new PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
            } else {
                Locale locale;
                switch (language) {
                    case RU:
                        locale = new Locale("ru", "RU");
                        break;
                    case KZ:
                        locale = new Locale("kk", "KZ");
                        break;
                    default:
                        locale = new Locale("en", "US");
                        break;
                }
                bundle = ResourceBundle.getBundle("resources.messages", locale);
            }
        } catch (IOException | MissingResourceException e) {
            loadDefaultBundle();
        }
    }

    public static String t(String key, Object... args) {
        if (bundle == null) setLanguage(Language.EN);

        String pattern;
        try {
            pattern = bundle.getString(key);
        } catch (Exception e) {
            return key;
        }

        if (args == null || args.length == 0) return pattern;

        try {
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return pattern;
        }
    }

    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    private static void loadDefaultBundle() {
        try {
            InputStream is = I18n.class.getClassLoader().getResourceAsStream("i18n/properties/en.properties");
            if (is != null) {
                bundle = new PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        } catch (IOException ignored) {
        }
    }
}
