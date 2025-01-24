package mod.maxbogomol.sinta;

import mod.maxbogomol.sinta.option.ErrorOption;
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

    private OutputOption outputOption;
    private ErrorOption errorOption;

    public boolean hadError = false;

    public Sinta() {
        this.setOutputOption(new OutputOption());
        this.setErrorOption(new ErrorOption());
    }

    public Sinta setOutputOption(OutputOption outputOption) {
        this.outputOption = outputOption;
        return this;
    }

    public Sinta setErrorOption(ErrorOption errorOption) {
        this.errorOption = errorOption;
        return this;
    }

    public OutputOption getOutputOption() {
        return outputOption;
    }

    public ErrorOption getErrorOption() {
        return errorOption;
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {

        } else if(args.length == 1) {
            runFile(args[0]);
        } else {
            runLines();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String (bytes, Charset.defaultCharset()));
    }

    public static void runLines() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(input);

        for(;;) {
            INSTANCE.hadError = false;
            System.out.print("> ");
            String line = reader.readLine();
            if (line.isEmpty()) break;
            run(line);
        }
    }

    public static void run(String source) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Scanner scanner = new Scanner(INSTANCE, source);
        List<Token> tokens = scanner.scanTokens();
        out.println(tokens);
    }
}
