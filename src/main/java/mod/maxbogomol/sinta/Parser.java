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

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Statement declaration() {
        try {
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }

    private Statement statement() {
        if (match(TokenTypes.IF)) return ifStatement();
        if (match(TokenTypes.LEFT_BRACE)) return new Statement.Block(block());
        return expressionStatement();
    }

    private Statement expressionStatement() {
        Expression expr = expression();
        consume(TokenTypes.SEMICOLON, "Expect ';' after expression.");
        return new Statement.Expression(expr);
    }

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        Expression expr = or();

        if (match(TokenTypes.EQUAL)) {
            Token equals = previous();
            Expression value = assignment();
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Expression or() {
        Expression expression = and();
        while (match(TokenTypes.OR)) {
            Token operator = previous();
            Expression right = and();
            expression = new Expression.Logical(expression, operator, right);
        }
        return expression;
    }

    private Expression and() {
        Expression expression = equality();
        while (match(TokenTypes.AND)) {
            Token operator = previous();
            Expression right = equality();
            expression = new Expression.Logical(expression,operator,right);
        }
        return expression;
    }

    private Expression equality() {
        Expression expression = comparison();
        while (match(TokenTypes.BANG_EQUAL, TokenTypes.EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expression = new Expression.Binary(expression, operator, right);
        }
        return expression;
    }

    private Expression comparison() {
        Expression expression = addition();
        while (match(TokenTypes.GREATER, TokenTypes.GREATER_EQUAL, TokenTypes.LESS, TokenTypes.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = addition();
            expression = new Expression.Binary(expression, operator, right);
        }
        return expression;
    }

    private Expression addition() {
        Expression expression = multiplication();
        while (match(TokenTypes.MINUS, TokenTypes.PLUS)) {
            Token operator = previous();
            Expression right = multiplication();
            expression = new Expression.Binary(expression, operator, right);
        }
        return expression;
    }

    private Expression multiplication() {
        Expression expression = unary();
        while (match(TokenTypes.SLASH, TokenTypes.STAR)) {
            Token operator = previous();
            Expression right = unary();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression unary() {
        if (match(TokenTypes.BANG, TokenTypes.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return call();
    }

    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(TokenTypes.RIGHT_PAREN)) {
            do {
                if(arguments.size() >= 100) {
                    error(peek(), "Cannot have more than 100 arguments.");
                }
                arguments.add(expression());
            } while (match(TokenTypes.COMMA));
        }
        Token paren = consume(TokenTypes.RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expression.Call(callee, paren, arguments);
    }

    private Expression call() {
        Expression expression = primary();
        while (true) {
            if (match(TokenTypes.LEFT_PAREN)) {
                expression = finishCall(expression);
            } else if (match(TokenTypes.DOT)) {
                Token name = consume(TokenTypes.IDENTIFIER, "Expect property name after '.' .");
                expression = new Expression.Get(expression, name);
            } else {
                break;
            }
        }
        return expression;
    }

    private Expression primary() {
        if (match(TokenTypes.FALSE)) return new Expression.Literal(false);
        if (match(TokenTypes.TRUE)) return new Expression.Literal(true);

        if(match(TokenTypes.LEFT_PAREN)) {
            Expression expr = expression();
            consume(TokenTypes.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().getType() == TokenTypes.SEMICOLON) return;
            if (peek().getType() == TokenTypes.IF) return;
            advance();
        }
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        sinta.getErrorOption().error(token, message);
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
