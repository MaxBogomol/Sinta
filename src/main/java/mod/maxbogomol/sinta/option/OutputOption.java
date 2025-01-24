package mod.maxbogomol.sinta.option;

import mod.maxbogomol.sinta.Sinta;

public class OutputOption {

    public void print(String string) {
        Sinta.output.print(string);
    }

    public void println(String string) {
        Sinta.output.println(string);
    }

    public void println() {
        Sinta.output.println();
    }
}
