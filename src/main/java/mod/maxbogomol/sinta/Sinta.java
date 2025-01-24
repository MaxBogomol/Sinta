package mod.maxbogomol.sinta;

import mod.maxbogomol.sinta.option.ErrorOption;
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
        this.setInputOption(new InputOption());
        this.setOutputOption(new OutputOption());
        this.setErrorOption(new ErrorOption());
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

    public static void main(String[] args) {
        if (args.length == 1) {
            INSTANCE.runFile(args[0]);
        } else {
            INSTANCE.runLines();
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
            if (line.isEmpty()) break;
            run(line);
        }
    }

    public void run(String source) {
        Scanner scanner = new Scanner(INSTANCE, source);
        List<Token> tokens = scanner.scanTokens();
        if (hadError()) return;
        output.println("Tokens: " + tokens);

        Parser parser = new Parser(INSTANCE, tokens);
        List<Stmt> statements = parser.parse();
        if (hadError()) return;
        output.println("Statements: " + statements);
    }
}
