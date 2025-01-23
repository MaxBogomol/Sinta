package mod.maxbogomol.sinta.token;

public class TokenTypes {
    public static final TokenType LEFT_PAREN = new TokenType("left paren");
    public static final TokenType RIGHT_PAREN = new TokenType("right paren");
    public static final TokenType LEFT_BRACE = new TokenType("left brace");
    public static final TokenType RIGHT_BRACE = new TokenType("right brace");
    public static final TokenType LEFT_SQUARE_BRACE = new TokenType("left square brace");
    public static final TokenType RIGHT_SQUARE_BRACE = new TokenType("right square brace");
    public static final TokenType COMMA = new TokenType("comma");
    public static final TokenType DOT = new TokenType("dot");
    public static final TokenType SEMICOLON = new TokenType("semicolon");
    public static final TokenType PLUS = new TokenType("plus");
    public static final TokenType MINUS = new TokenType("minus");
    public static final TokenType STAR = new TokenType("star");
    public static final TokenType SLASH = new TokenType("slash");
    public static final TokenType POW = new TokenType("pow");

    public static final TokenType BANG = new TokenType("bang");
    public static final TokenType BANG_EQUAL = new TokenType("bang equal");
    public static final TokenType EQUAL = new TokenType("equal");
    public static final TokenType EQUAL_EQUAL = new TokenType("equal equal");
    public static final TokenType GREATER = new TokenType("greater");
    public static final TokenType GREATER_EQUAL = new TokenType("greater equal");
    public static final TokenType LESS = new TokenType("less");
    public static final TokenType LESS_EQUAL = new TokenType("less equal");

    public static final TokenType IDENTIFIER = new TokenType("identifier");
    public static final TokenType STRING = new TokenType("string");
    public static final TokenType INTEGER = new TokenType("integer");
    public static final TokenType FLOAT = new TokenType("float");
    public static final TokenType DOUBLE = new TokenType("double");

    public static final TokenType FALSE = new TokenType("false");
    public static final TokenType TRUE = new TokenType("true");
    public static final TokenType IF = new TokenType("if");
    public static final TokenType ELSE = new TokenType("else");
    public static final TokenType AND = new TokenType("and");
    public static final TokenType OR = new TokenType("or");

    public static final TokenType EOF = new TokenType("eof");
}
