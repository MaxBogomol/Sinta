package mod.maxbogomol.sinta;

import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitExpressionStmt(Expression stmt);
        R visitIfStmt(If stmt);
        R visitPrintStmt(Print stmt);
        R visitPrintlnStmt(Println stmt);
    }

    public static class Block extends Stmt {
        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        final List<Stmt> statements;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        String getName() {
            return "block";
        }
    }

    public static class Expression extends Stmt {
        public Expression(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        String getName() {
            return "expression";
        }
    }

    public static class If extends Stmt {
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        String getName() {
            return "if";
        }
    }

    public static class Print extends Stmt {
        public Print(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        String getName() {
            return "print";
        }
    }

    public static class Println extends Stmt {
        public Println(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintlnStmt(this);
        }

        String getName() {
            return "println";
        }
    }

    abstract <R> R accept(Visitor<R> visitor);

    abstract String getName();

    public String toString() {
        return getName();
    }
}
