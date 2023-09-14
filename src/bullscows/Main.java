package bullscows;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Symbol[] symbols = generateSymbols();

        System.out.println("Input the length of the secret code: ");
        int length = parseToInt(scanner.nextLine());
        if (length <= 0) {
            System.out.printf("Error: \"%s\" isn't a valid number.%n", length);
            return;
        }

        System.out.println("Input the number of possible symbols in the code: ");
        int numberOfSymbols = parseToInt(scanner.nextLine());
        if (numberOfSymbols <= 0) {
            System.out.printf("Error: \"%s\" isn't a valid number.%n", numberOfSymbols);
            return;
        }

        if (numberOfSymbols < length) {
            System.out.printf("Error: it's not possible to generate a code with a length of %d with %d unique symbols.%n", length, numberOfSymbols);
            return;
        }

        if (numberOfSymbols > 36) {
            System.out.println("Error: maximum number of possible symbols in the code is 36 (0-9, a-z)");
            return;
        }

        var secret = generateSecretCode(length, numberOfSymbols);
        System.out.println(generateSecretPreparedLine(symbols, length, numberOfSymbols));

        System.out.println("Okay, let's start a game!");
        int currentTurn = 0;
        while (true) {
            currentTurn++;
            System.out.printf("Turn %d:%n", currentTurn);
            var input = getInput();
            var grade = grade(input, secret);
            var response = buildResponse(grade);
            System.out.printf("%s%n", response);
            if (grade.bulls() == length) {
                System.out.println("Congratulations! You guessed the secret code.");
                break;
            }
        }
    }

    private static int parseToInt(String line) {
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Symbol[] generateSymbols() {
        Symbol[] symbols = new Symbol[36];
        for (int i = 0; i < 10; i++) {
            symbols[i] = new Symbol((char) ('0' + i));
        }
        for (int i = 10; i < 36; i++) {
            symbols[i] = new Symbol((char) ('a' + i - 10));
        }
        return symbols;
    }

    private static StringBuilder generateSecretPreparedLine(Symbol[] symbols, int length, int numberOfSymbols) {
        String whichDigits = numberOfSymbols <= 10 ? symbols[0].c() + "-" + symbols[numberOfSymbols - 1].c() : "0-9";
        String whichLetters = numberOfSymbols <= 10 ? "" : numberOfSymbols == 11 ? "a" : "a-" + symbols[numberOfSymbols - 1].c();
        var line = new StringBuilder();
        line.append("The secret is prepared: ");
        line.append("*".repeat(length));
        line.append(" (");
        line.append(whichDigits);
        if (numberOfSymbols > 10) {
            line.append(", ");
            line.append(whichLetters);
        }
        line.append(").");
        return line;
    }

    private static String buildResponse(Grade grade) {
        var response = new StringBuilder();
        if (grade.bulls() == 0 && grade.cows() == 0) {
            response.append("Grade: None.");
        } else if (grade.bulls() == 0) {
            response.append("Grade: ").append(grade.cows()).append(" cow(s).");
        } else if (grade.cows() == 0) {
            response.append("Grade: ").append(grade.bulls()).append(" bull(s).");
        } else {
            response.append("Grade: ").append(grade.bulls()).append(" bull(s) and ").append(grade.cows()).append(" cow(s).");
        }
        return response.toString();
    }

    private static Grade grade(String input, String secret) {
        int bulls = 0, cows = 0;
        for (int i = 0; i < input.length(); i++) {
            if (secret.charAt(i) == input.charAt(i)) {
                bulls++;
            } else if (secret.contains(String.valueOf(input.charAt(i)))) {
                cows++;
            }
        }
        return new Grade(bulls, cows);
    }

    private static String getInput() {
        return new Scanner(System.in).nextLine().trim();
    }

    private static String generateSecretCode(int length, int symbols) {
        var secret = new StringBuilder();
        while (secret.length() < length) {
            var random = ThreadLocalRandom.current().nextInt(0, symbols);
            char c = (char) (random < 10 ? '0' + random : 'a' + random - 10);
            if (secret.indexOf(String.valueOf(c)) == -1) {
                secret.append(c);
            }
        }
        return secret.toString();
    }
}

record Grade(int bulls, int cows) {
}

record Symbol(char c) {
}