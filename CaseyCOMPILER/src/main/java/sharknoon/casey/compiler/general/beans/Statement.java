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

import java.util.List;

public class Statement {
    
    //The type of the statement (required)
    public StatementType type;
    
    //The value of this value (only for number, boolean, text and object)
    public Object value;
    
    //The parameter of this operator (only for operators)
    public List<Statement> parameter = List.of();
    
    //The calls of this call (only for call)
    public List<Call> calls = List.of();
    
    public enum StatementType {
        //Values
        NUMBER,
        BOOLEAN,
        TEXT,
        OBJECT,
        //Operators
        ADD,
        AND,
        CONCAT,
        DIVIDE,
        EQUALS,
        GREATEROREQUALTHAN,
        GREATERTHAN,
        LENGTH,
        LESSOREQUALTHAN,
        LESSTHAN,
        MODULO,
        MULTIPLY,
        NOTEQUALS,
        NOT,
        OR,
        SUBTRACT,
        //Call
        CALL
    }
    
    public static class Call {
        
        //The parameter of this function (only for functions)
        public List<Statement> parameter = List.of();
        
        //The type of this call (variable, parameter or functionname)
        public String type;
        
    }
    
}
