package mod.maxbogomol.sinta;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String line = scanner.nextLine();
        Sinta.main(line, out);
    }
}