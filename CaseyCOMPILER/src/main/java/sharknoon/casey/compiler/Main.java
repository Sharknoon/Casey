package sharknoon.casey.compiler;

import sharknoon.casey.compiler.general.CLIParser;

public class Main {
    public static void main(String[] args) {
        var cliArgs = CLIParser.parseCommandLine(args);
        System.out.println(cliArgs);
    }
    
}
