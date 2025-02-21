package mod.maxbogomol.sinta;

import mod.maxbogomol.sinta.option.ErrorOption;
import mod.maxbogomol.sinta.option.ExecuteOption;
import mod.maxbogomol.sinta.option.InputOption;
import mod.maxbogomol.sinta.option.OutputOption;
import mod.maxbogomol.sinta.token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Sinta {
    public static final Sinta INSTANCE = new Sinta();

    public static BufferedReader input;
    public static PrintStream output;

    private final Interpreter interpreter;

    private ExecuteOption executeOption;
    private InputOption inputOption;
    private OutputOption outputOption;
    private ErrorOption errorOption;

    private boolean hadError = false;
    private boolean hadRuntimeError = false;

    static {
        InputStreamReader input = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        Sinta.input = new BufferedReader(input);
        Sinta.output = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }

    public Sinta() {
        this.setExecuteOption(new ExecuteOption());
        this.setInputOption(new InputOption());
        this.setOutputOption(new OutputOption());
        this.setErrorOption(new ErrorOption());
        interpreter = new Interpreter(this);
    }

    public Sinta setExecuteOption(ExecuteOption executeOption) {
        this.executeOption = executeOption;
        return this;
    }

    public Sinta setInputOption(InputOption inputOption) {
        this.inputOption = inputOption;
        return this;
    }

    public Sinta setOutputOption(OutputOption outputOption) {
        this.outputOption = outputOption;
        return this;
    }

    public Sinta setErrorOption(ErrorOption errorOption) {
        this.errorOption = errorOption;
        return this;
    }

    public ExecuteOption getExecuteOption() {
        return executeOption;
    }

    public InputOption getInputOption() {
        return inputOption;
    }

    public OutputOption getOutputOption() {
        return outputOption;
    }

    public ErrorOption getErrorOption() {
        return errorOption;
    }

    public void error() {
        this.hadError = true;
    }

    public void runtimeError() {
        this.hadRuntimeError = true;
    }

    public boolean hadError() {
        return hadError;
    }

    public boolean hadRuntimeError() {
        return hadRuntimeError;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            INSTANCE.setArgs(INSTANCE.getArgs());
        } else {
            INSTANCE.setArgs(args);
        }
        INSTANCE.runMain();
    }

    private String[] getArgs() {
        output.print("Execute arguments: ");
        String line;
        try {
            line = input.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (line == null) return getArgs("");
        return getArgs(line);
    }

    private String[] getArgs(String string) {
        return string.split(" ");
    }

    private boolean getBooleanArg(String string) {
        return !string.contains("false");
    }

    private String getStringArg(String string) {
        int i = string.indexOf('"');
        string = string.substring(i + 1, string.length() - 1);
        return string;
    }

    private void setArgs(String[] args) {
        for (String string : args) {
            if (string.contains("-cscanner=") || string.contains("-cscan=")) executeOption.setCancelableScanner(getBooleanArg(string));
            if (string.contains("-cparser=") || string.contains("-cpars=")) executeOption.setCancelableParser(getBooleanArg(string));
            if (string.contains("-showtokens=") || string.contains("-stokens=")) executeOption.setShowTokens(getBooleanArg(string));
            if (string.contains("-showstatements=") || string.contains("-sstatements=")) executeOption.setShowStatements(getBooleanArg(string));
            if (string.contains("-file=")) executeOption.setFilePath(getStringArg(string));
        }
    }

    private void runMain() {
        if (executeOption.isFile()) {
            runFile(executeOption.getFilePath());
        } else {
            runLines();
        }
    }

    private void runFile(String path) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (bytes.length > 0) run(new String (bytes, Charset.defaultCharset()));
    }

    public void runLines() {
        while (true) {
            this.hadError = false;
            output.print("> ");
            String line;
            try {
                line = input.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (line == null) break;
            if (line.isEmpty()) break;
            run(line);
        }
    }

    public void run(String source) {
        Scanner scanner = new Scanner(this, source);
        List<Token> tokens = scanner.scanTokens();
        if (hadError()) return;
        if (executeOption.showTokens()) output.println("Tokens: " + tokens);

        Parser parser = new Parser(this, tokens);
        List<Stmt> statements = parser.parse();
        if (hadError()) return;
        if (executeOption.showStatements()) output.println("Statements: " + statements);

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        if (hadError()) return;

        interpreter.interpret(statements);
    }
}
