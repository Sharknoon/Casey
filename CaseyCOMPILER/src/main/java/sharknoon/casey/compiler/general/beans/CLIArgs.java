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
    final transient Path basePath;
    final Language language;
    final Map<String, String> parameters;
    final boolean ignoreComments;
    
    public CLIArgs(String function, String caseyPath, String language, Map<String, String> parameters, boolean ignoreComments) {
        this.function = function;
        this.caseyPath = Paths.get(caseyPath);
        this.basePath = this.caseyPath.getParent() != null ? this.caseyPath.getParent() : Paths.get("");
        this.language = Language.valueOf(language.toUpperCase());
        this.parameters = parameters;
        this.ignoreComments = ignoreComments;
    }
    
    public String getFunction() {
        return function;
    }
    
    /**
     * The path to the .casey file, including the .casey file
     *
     * @return
     */
    public Path getCaseyPath() {
        return caseyPath;
    }
    
    /**
     * The base path, the folder in which the .casey file is located
     *
     * @return
     */
    public Path getBasePath() {
        return basePath;
    }
    
    public Language getLanguage() {
        return language;
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public boolean getIgnoreComments() {
        return ignoreComments;
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