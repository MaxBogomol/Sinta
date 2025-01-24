package mod.maxbogomol.sinta.option;

public class ExecuteOption {
    private boolean cancelableScanner = true;
    private boolean cancelableParser = true;
    private String filePath = "";
    private boolean showTokens = true;
    private boolean showStatements = true;

    public ExecuteOption setCancelableScanner(boolean cancelableScanner) {
        this.cancelableScanner = cancelableScanner;
        return this;
    }

    public ExecuteOption setCancelableParser(boolean cancelableParser) {
        this.cancelableParser = cancelableParser;
        return this;
    }

    public ExecuteOption setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public ExecuteOption setShowTokens(boolean showTokens) {
        this.showTokens = showTokens;
        return this;
    }

    public ExecuteOption setShowStatements(boolean showStatements) {
        this.showStatements = showStatements;
        return this;
    }

    public boolean cancelableScanner() {
        return cancelableScanner;
    }

    public boolean cancelableParser() {
        return cancelableParser;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isFile() {
        return !filePath.isEmpty();
    }

    public boolean showTokens() {
        return showTokens;
    }

    public boolean showStatements() {
        return showStatements;
    }
}
