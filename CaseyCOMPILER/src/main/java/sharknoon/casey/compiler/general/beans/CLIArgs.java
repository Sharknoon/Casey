package sharknoon.casey.compiler.general.beans;/*
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

import sharknoon.casey.compiler.Language;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class CLIArgs {
    final String function;
    final Path caseyPath;
    final Language language;
    final Map<String, String> parameters;
    
    public CLIArgs(String function, String caseyPath, String language, Map<String, String> parameters) {
        this.function = function;
        this.caseyPath = Paths.get(caseyPath);
        this.language = Language.valueOf(language.toUpperCase());
        this.parameters = parameters;
    }
    
    public String getFunction() {
        return function;
    }
    
    public Path getCaseyPath() {
        return caseyPath;
    }
    
    public Path getBasePath() {
        return caseyPath.getParent();
    }
    
    public Language getLanguage() {
        return language;
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    @Override
    public String toString() {
        return "CLIArgs{" +
                "function='" + function + '\'' +
                ", caseyPath='" + caseyPath + '\'' +
                ", language='" + language + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}