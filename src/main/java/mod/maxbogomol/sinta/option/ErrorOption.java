package mod.maxbogomol.sinta.option;

import mod.maxbogomol.sinta.token.Token;

public class ErrorOption {

    public void error(int line, String string) {
        System.out.println("[Line: " + line + "] Error: " + string);
    }

    public void error(int line, int  column, String string) {
        System.out.println("[Line: " + line + ", Column: " + column + "] Error: " + string);
    }

    public void error(Token token, String string) {
        System.out.println(string);
    }

    public boolean cancelableScanner() {
        return true;
    }

    public boolean cancelableParser() {
        return true;
    }
}
