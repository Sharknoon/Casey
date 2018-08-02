package sharknoon.casey.updater.cli;
/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.cli.*;

import java.util.Optional;

public class CLIParser {
    
    private static Options options;
    private static String HEADER = "\n" +
            "-----------------------\n" +
            "-    CaseyUPDATER     -\n" +
            "-----------------------\n" +
            "\n";
    
    
    public static Optional<CLIArgs> parseCommandLine(String[] args) {
        if (options == null) {
            options = initOptions();
        }
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String check = cmd.getOptionValue("c");
            String update = cmd.getOptionValue("u");
            
            var cliargs = new CLIArgs(check, update);
            return Optional.of(cliargs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(
                    100,
                    "caseyu",
                    HEADER,
                    options,
                    "",
                    true
            );
        }
        return Optional.empty();
    }
    
    private static Options initOptions() {
        Options options = new Options();
        
        Option check = Option.builder("c")
                .longOpt("check")
                .hasArg()
                .argName("X.X")
                .desc("Checks for a newer version of Casey. The old version of casey to be checked against")
                .build();
        
        Option update = Option.builder("u")
                .longOpt("update")
                .hasArg()
                .argName("../../Casey.jar")
                .desc("Updates Casey. The path to the Casey.jar to be replaced")
                .build();
        
        options.addOption(check);
        options.addOption(update);
        
        
        return options;
    }
    
}
