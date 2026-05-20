package ui.auth;

import enums.Language;
import i18n.I18n;
import models.users.User;
import services.auth.AuthenticationService;

import java.util.Scanner;

public class AuthConsole {
    private final Scanner scanner;
    private final AuthenticationService authenticationService;

    public AuthConsole(Scanner scanner, AuthenticationService authenticationService) {
        this.scanner = scanner;
        this.authenticationService = authenticationService;
    }

    public User login() {
        try {
            System.out.println();
            System.out.println("===== " + I18n.t("auth.login.prompt") + " =====");
            System.out.print(I18n.t("auth.username") + ": ");
            String username = scanner.nextLine().trim();
            System.out.print(I18n.t("auth.password") + ": ");
            String password = scanner.nextLine();
            Language language = chooseLanguage();
            return authenticationService.login(username, password, language);
        } catch (Exception e) {
            System.out.println(I18n.t("auth.login.fail") + ": " + e.getMessage());
            return null;
        }
    }

    private Language chooseLanguage() {
        System.out.print("Language (EN/RU/KZ, default EN): ");
        String input = scanner.nextLine().trim().toUpperCase();
        if (input.isEmpty()) return Language.EN;
        try {
            return Language.valueOf(input);
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown language. EN selected.");
            return Language.EN;
        }
    }
}
