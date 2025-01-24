package mod.maxbogomol.sinta.option;

import mod.maxbogomol.sinta.RuntimeError;
import mod.maxbogomol.sinta.Sinta;
import mod.maxbogomol.sinta.token.Token;

public class ErrorOption {

    public void error(int line, String string) {
        Sinta.output.println("[Line: " + line + "] Error: " + string);
    }

    public void error(int line, int column, String string) {
        Sinta.output.println("[Line: " + line + ", Column: " + column + "] Error: " + string);
    }

    public void error(Token token, String string) {
        error(token.getLine(), token.getColumn(), "at '" + token.getLexeme() + "'" + " " + string);
    }

    public void runtimeError(RuntimeError error) {
        Sinta.output.println(error.getMessage() + "\n[Line: " + error.token.getLine() + ", Column: " + error.token.getColumn() + "]");
    }
}
