package ui;

import enums.Language;
import i18n.I18n;
import models.users.User;
import services.AuthenticationService;

import java.util.Scanner;

public final class ConsoleLanguage {
    private ConsoleLanguage() {
    }

    public static void changeLanguage(Scanner scanner, User user) {
        try {
            System.out.print("Language (EN/RU/KZ): ");
            Language language = Language.valueOf(scanner.nextLine().trim().toUpperCase());
            new AuthenticationService().selectInterfaceLanguage(user, language);
        } catch (Exception e) {
            System.out.println(I18n.t("language.change_failed") + ": " + e.getMessage());
        }
    }
}
