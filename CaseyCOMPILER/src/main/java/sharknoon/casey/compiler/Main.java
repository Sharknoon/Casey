package sharknoon.casey.compiler;

import sharknoon.casey.compiler.general.CLIParser;
import sharknoon.casey.compiler.general.CaseyParser;

public class Main {
    public static void main(String[] args) {
        var cliArgs = CLIParser.parseCommandLine(args);
        if (!cliArgs.isPresent()) {
            return;
        }
        var item = CaseyParser.parseCasey(cliArgs.get().getPath());
        if (!item.isPresent()) {
            return;
        }
        
    }
    
}
