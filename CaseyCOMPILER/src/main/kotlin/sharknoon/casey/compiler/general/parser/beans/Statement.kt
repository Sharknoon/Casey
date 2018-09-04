package sharknoon.casey.compiler.general.parser.beans

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
 * See the License for the specific languageString governing permissions and
 * limitations under the License.
 */

data class Statement(
        //The type of the statement (required)
        var type: StatementType,
        //The value of this value (only for number, boolean, text and object)
        var value: Any? = null,
        //The parameter of this operator (only for operators)
        var parameter: List<Statement> = listOf(),
        //The calls of this call (only for call)
        var calls: List<Call> = listOf()) {


    enum class StatementType {
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
        GREATER_OR_EQUAL_THAN,
        GREATER_THAN,
        LENGTH,
        LESS_OR_EQUAL_THAN,
        LESS_THAN,
        MODULO,
        MULTIPLY,
        NOT_EQUALS,
        NOT,
        OR,
        SUBTRACT,
        //Call
        CALL
    }

    data class Call(
            //The parameter of this function (only for functions)
            var parameter: List<Statement> = listOf(),
            //The type of this call (variable-, parameter- or functionname)
            var type: String? = null)

}
