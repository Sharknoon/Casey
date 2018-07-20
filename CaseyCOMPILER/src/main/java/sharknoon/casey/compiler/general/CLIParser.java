package sharknoon.casey.compiler.general;/*
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
import sharknoon.casey.compiler.Language;
import sharknoon.casey.compiler.general.beans.CLIArgs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CLIParser {
    
    private static Options options;
    private static String HEADER = "\n" +
            "-----------------------\n" +
            "-    CaseyCOMPILER    -\n" +
            "-----------------------\n" +
            "\n";
    
    
    public static Optional<CLIArgs> parseCommandLine(String[] args) {
        if (options == null) {
            options = initOptions();
        }
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String path = cmd.getOptionValue("p");
            String function = cmd.getOptionValue("f");
            String language = cmd.getOptionValue("l");
            String[] parameter = cmd.getOptionValues("pa");
            Map<String, String> parameterMap = new HashMap<>();
            if (parameter != null) {
                for (int i = 0; i + 1 < parameter.length; i += 2) {
                    parameterMap.put(parameter[i], parameter[i + 1]);
                }
            }
            return Optional.of(new CLIArgs(function, path, language, parameterMap));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(
                    100,
                    "caseyc",
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
        
        Option path = Option.builder("p")
                .longOpt("path")
                .hasArg()
                .argName("../../xyz.casey")
                .required()
                .desc("The path to the .casey file to be compiled")
                .build();
        
        Option function = Option.builder("f")
                .longOpt("function")
                .hasArg()
                .argName("ABCProject.defpackage.XYZFunction")
                .required()
                .desc("The main function to be started with")
                .build();
    
        Option language = Option.builder("l")
                .longOpt("language")
                .hasArg()
                .argName("name")
                .required()
                .desc("The language this project should be compiled to (" +
                        Arrays.stream(Language.values())
                                .map(Enum::name)
                                .collect(Collectors.joining(", ")) + ")"
                )
                .build();
        
        Option parameters = Option.builder("pa")
                .longOpt("parameter")
                .hasArgs()
                .argName("parameter=value")
                .valueSeparator()
                .desc("The parameters of the main function, if the function has some")
                .build();
        
        options.addOption(path);
        options.addOption(function);
        options.addOption(language);
        options.addOption(parameters);
        
        return options;
    }
    
}
