package mod.maxbogomol.sinta;

import mod.maxbogomol.sinta.token.Token;

public class RuntimeError extends RuntimeException {
    public final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
