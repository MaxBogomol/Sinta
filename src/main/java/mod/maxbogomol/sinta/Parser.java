package mod.maxbogomol.sinta;

import mod.maxbogomol.sinta.token.Token;
import mod.maxbogomol.sinta.token.TokenType;
import mod.maxbogomol.sinta.token.TokenTypes;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Sinta sinta;
    private final List<Token> tokens;

    private static class ParseError extends RuntimeException{};

    private int current = 0;

    public Parser(Sinta sinta, List<Token> tokens) {
        this.sinta = sinta;
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
            if (sinta.hadError()) break;
        }
        return statements;
    }

    private Stmt declaration() {
        try {
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }

    private Stmt statement() {
        if (match(TokenTypes.IF)) return ifStatement();
        if (match(TokenTypes.PRINT)) return printStatement();
        if (match(TokenTypes.PRINTLN)) return printlnStatement();
        if (match(TokenTypes.LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt ifStatement() {
        consume(TokenTypes.LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(TokenTypes.RIGHT_PAREN, "Expect ')' after if condition,");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenTypes.ELSE)) {
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        consume(TokenTypes.LEFT_PAREN, "Expect '(' after 'print'.");
        Expr value = expression();
        consume(TokenTypes.RIGHT_PAREN, "Expect ')' after 'print'.");
        consume(TokenTypes.SEMICOLON, "Expect ';' after print.");
        return new Stmt.Print(value);
    }

    private Stmt printlnStatement() {
        consume(TokenTypes.LEFT_PAREN, "Expect '(' after 'print'.");
        Expr value = expression();
        consume(TokenTypes.RIGHT_PAREN, "Expect ')' after 'print'.");
        consume(TokenTypes.SEMICOLON, "Expect ';' after print.");
        return new Stmt.Println(value);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenTypes.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(TokenTypes.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenTypes.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();
        //
        return expr;
    }

    private Expr or() {
        Expr expr = and();
        while (match(TokenTypes.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and() {
        Expr expr = equality();
        while (match(TokenTypes.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr,operator,right);
        }
        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();
        while (match(TokenTypes.BANG_EQUAL, TokenTypes.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();
        while (match(TokenTypes.GREATER, TokenTypes.GREATER_EQUAL, TokenTypes.LESS, TokenTypes.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();
        while (match(TokenTypes.MINUS, TokenTypes.PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();
        while (match(TokenTypes.SLASH, TokenTypes.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(TokenTypes.BANG, TokenTypes.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return call();
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(TokenTypes.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 100) {
                    error(peek(), "Cannot have more than 100 arguments.");
                }
                arguments.add(expression());
            } while (match(TokenTypes.COMMA));
        }
        Token paren = consume(TokenTypes.RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr call() {
        Expr expr = primary();
        while (true) {
            if (match(TokenTypes.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(TokenTypes.DOT)) {
                Token name = consume(TokenTypes.IDENTIFIER, "Expect property name after '.' .");
                expr = new Expr.Get(expr, name);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr primary() {
        if (match(TokenTypes.FALSE)) return new Expr.Literal(false);
        if (match(TokenTypes.TRUE)) return new Expr.Literal(true);

        if (match(TokenTypes.STRING)) return new Expr.Literal(previous().getLiteral());

        if (match(TokenTypes.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenTypes.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().getType() == TokenTypes.SEMICOLON) return;
            if (peek().getType() == TokenTypes.IF) return;
            if (peek().getType() == TokenTypes.PRINT) return;
            advance();
        }
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        sinta.getErrorOption().error(token, message);
        if (sinta.getExecuteOption().cancelableParser()) sinta.error();
        return new ParseError();
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenTypes.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
