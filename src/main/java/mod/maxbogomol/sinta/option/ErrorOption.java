package mod.maxbogomol.sinta.option;

public class ErrorOption {

    public void error(int line, String string) {
        System.out.println("[Line: " + line + "] Error: " + string);
    }

    public void error(int line, int  column, String string) {
        System.out.println("[Line: " + line + ", Column: " + column + "] Error: " + string);
    }

    public boolean cancelable() {
        return true;
    }
}
