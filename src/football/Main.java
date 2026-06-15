package football;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path dataDirectory = args.length > 0 ? Paths.get(args[0]) : Paths.get("data");
        new ConsoleApp(dataDirectory).run();
    }
}
