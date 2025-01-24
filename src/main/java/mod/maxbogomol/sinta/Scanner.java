package mod.maxbogomol.sinta;

import mod.maxbogomol.sinta.token.Token;
import mod.maxbogomol.sinta.token.TokenType;
import mod.maxbogomol.sinta.token.TokenTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final Sinta sinta;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 0;
    private int startColumn = 0;

    private static final Map<String, TokenType> keywords;
    private static final Map<String, List<TokenType>> multiKeywords;

    static {
        keywords = new HashMap<>();
        keywords.put("false", TokenTypes.FALSE);
        keywords.put("true", TokenTypes.TRUE);
        keywords.put("if", TokenTypes.IF);
        keywords.put("else", TokenTypes.ELSE);
        keywords.put("and", TokenTypes.AND);
        keywords.put("&&", TokenTypes.AND);
        keywords.put("or", TokenTypes.OR);
        keywords.put("||", TokenTypes.OR);
        keywords.put("print", TokenTypes.PRINT);
        keywords.put("println", TokenTypes.PRINTLN);

        multiKeywords = new HashMap<>();
        List<TokenType> elif = new ArrayList<>();
        elif.add(TokenTypes.ELSE);
        elif.add(TokenTypes.IF);
        multiKeywords.put("elif", elif);
    }

    public Scanner(Sinta sinta, String source) {
        this.sinta = sinta;
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
            if (sinta.hadError()) break;
        }
        tokens.add(new Token(TokenTypes.EOF, "", null, line, column++));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        startColumn = column;
        switch (c) {
            case '(' -> addToken(TokenTypes.LEFT_PAREN);
            case ')' -> addToken(TokenTypes.RIGHT_PAREN);
            case '{' -> addToken(TokenTypes.LEFT_BRACE);
            case '}' -> addToken(TokenTypes.RIGHT_BRACE);
            case '[' -> addToken(TokenTypes.LEFT_SQUARE_BRACE);
            case ']' -> addToken(TokenTypes.RIGHT_SQUARE_BRACE);
            case '.' -> addToken(TokenTypes.DOT);
            case ',' -> addToken(TokenTypes.COMMA);
            case ':' -> addToken(TokenTypes.COLON);
            case ';' -> addToken(TokenTypes.SEMICOLON);
            case '+' -> addToken(TokenTypes.PLUS);
            case '-' -> addToken(TokenTypes.MINUS);
            case '*' -> addToken(TokenTypes.STAR);
            case '^' -> addToken(TokenTypes.POW);
            case '?' -> addToken(TokenTypes.QUESTION);
            case '!' -> addToken(match('=') ? TokenTypes.BANG_EQUAL : TokenTypes.BANG);
            case '=' -> addToken(match('=') ? TokenTypes.EQUAL_EQUAL : TokenTypes.EQUAL);
            case '<' -> addToken(match('=') ? TokenTypes.LESS_EQUAL : TokenTypes.LESS);
            case '>' -> addToken(match('=') ? TokenTypes.GREATER_EQUAL : TokenTypes.GREATER);
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(TokenTypes.SLASH);
                }
            }
            case '\n' -> newLine();
            case ' ' -> {}
            case '"' -> string();
            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    sinta.getErrorOption().error(line, column, "Unexpected character " + c);
                    if (sinta.getErrorOption().cancelableScanner()) sinta.error();
                }
            }
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        newColumn();
        current++;
        return source.charAt(current - 1);
    }

    private void newLine() {
        line++;
        column = 0;
    }

    private void newColumn() {
        column++;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, startColumn));
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
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

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            boolean multi = multiKeywords.containsKey(text);
            if (multi) {
                multiKeywords.get(text).forEach(this::addToken);
            } else {
                addToken(TokenTypes.IDENTIFIER);
            }
        } else {
            addToken(type);
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                newLine();
            }
            advance();
        }

        if (isAtEnd()) {
            sinta.getErrorOption().error(line, column, "Unterminated string " + source.substring(start + 1, current));
            if (sinta.getErrorOption().cancelableScanner()) sinta.error();
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenTypes.STRING, value);
    }

    private void number() {
        boolean dot = false;
        boolean add = false;

        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            dot = true;
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }

        String parseString = source.substring(start, current);
        if (match('f')) {
            add = true;
            addToken(TokenTypes.FLOAT, Float.parseFloat(parseString));
        }
        if (match('d')) {
            add = true;
            addToken(TokenTypes.DOUBLE, Double.parseDouble(parseString));
        }
        if (!add) {
            if (dot) {
                addToken(TokenTypes.DOUBLE, Float.parseFloat(parseString));
            } else {
                addToken(TokenTypes.INTEGER, Integer.parseInt(parseString));
            }
        }
    }

    private boolean isAlpha(char c) {
        return (Character.isLetter(c) || c == '_');
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
