package mod.maxbogomol.sinta.token;

public class Token {
    private final TokenType type;
    private final Object literal;
    final String lexeme;
    final int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public Object getLiteral() {
        return literal;
    }

    public String toString() {
        if (getLiteral() == null) {
            return "(\"" + type.getName() + "\", " + lexeme +  ")";
        }
        return "(\"" + type.getName() + "\", " + lexeme + ", " + getLiteral() + ")";
    }
}
