package mod.maxbogomol.sinta.token;

public class Token {
    private final TokenType type;
    private final Object literal;
    private final String lexeme;
    private final int line;
    private final int column;

    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public Object getLiteral() {
        return literal;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        if (getLiteral() == null) {
            return "(\"" + type.getName() + "\", " + lexeme +  ")";
        }
        return "(\"" + type.getName() + "\", " + lexeme + ", " + getLiteral() + ")";
    }
}
