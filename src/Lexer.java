import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

class Lexer {
    private String accumulator = "";
    private int position = 0;
    private boolean waitForSuccess = true;
    private Lexeme currentLexeme = null;

    List<Token> recognize(String input) {
        List<Token> tokens = new ArrayList<>();

        if (input.length() != 0) {
            while (position < input.length()) {
                accumulator += input.charAt(position++);
                boolean found = find();
                if (!found) {
                    if (!waitForSuccess) {
                        waitForSuccess = true;
                        Token token = new Token(currentLexeme, format(accumulator));
                        tokens.add(token);
                        accumulator = "";
                        back();
                    } else {
                        waitForSuccess = true;
                        System.err.println('\n' + "Can't recognize input '" + accumulator + "' at position:" + position + "!");
                        System.exit(2);
                    }
                } else {
                    waitForSuccess = false;
                }
            }
            tokens.add(new Token(currentLexeme, accumulator));
        }else {
            System.err.println('\n' + "Error: Null input!");
            System.exit(1);
        }
        return tokens;
    }

    private void back() {
        position--;
    }

    private boolean find() {
        for (Lexeme lexeme : Lexeme.values()) {
            Matcher matcher = lexeme.getPattern().matcher(accumulator);
            if (matcher.matches()) {
                currentLexeme = lexeme;
                return true;
            }
        }
        return false;
    }

    private String format(String accumulator) {
        return accumulator.substring(0, accumulator.length() - 1);
    }
}