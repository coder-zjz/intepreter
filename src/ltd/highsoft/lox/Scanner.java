package ltd.highsoft.lox;

import java.util.ArrayList;
import java.util.List;

import static ltd.highsoft.lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            //we are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN, null);
                break;
            case ')':
                addToken(RIGHT_PAREN, null);
                break;
            case '{':
                addToken(LEFT_BRACE, null);
                break;
            case '}':
                addToken(RIGHT_BRACE, null);
                break;
            case ',':
                addToken(COMMA, null);
                break;
            case '.':
                addToken(DOT, null);
                break;
            case '-':
                addToken(MINUS, null);
                break;
            case '+':
                addToken(PLUS, null);
                break;
            case ';':
                addToken(SEMICOLON, null);
                break;
            case '*':
                addToken(STAR, null);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG, null);
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL, null);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS, null);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER, null);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH, null);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                //ignore whitespace
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else {
                    Lox.error(line, "unexpected character.");
                }
                break;
        }
    }

    private void number() {
        while (isDigit(peek())) advance();

        //Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            //Consume the "."
            advance();
            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance();

        //Trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;

        if (expected != source.charAt(current)) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private char advance() {
        return source.charAt(current++);
    }

}
