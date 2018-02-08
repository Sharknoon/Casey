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
package sharknoon.dualide.logic.statements.operations;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public enum OperatorType {
    ADD(AddOperator::new, AddOperator.class, Word.ADD, Icon.PLUS, "+"),
    SUBTRACT(SubtractOperator::new, SubtractOperator.class, Word.SUBTRACT, Icon.MINUS, "-"),
    MULTIPLY(MultiplyOperator::new, MultiplyOperator.class, Word.MULTIPLY, Icon.MULTIPLY, "×"),
    DIVIDE(DivideOperator::new, DivideOperator.class, Word.DIVIDE, Icon.DIVIDE, "÷"),
    MODULO(ModuloOperator::new, ModuloOperator.class, Word.MODULO, Icon.MODULO, "mod"),
    EQUALS(EqualsOperator::new, EqualsOperator.class, Word.EQUALS, Icon.EQUAL, "="),
    NOT_EQUALS(NotEqualsOperator::new, NotEqualsOperator.class, Word.NOT_EQUALS, Icon.NOTEQUAL, "≠"),
    GREATER_THAN(GreaterThanOperator::new, GreaterThanOperator.class, Word.GREATER_THAN, Icon.GREATERTHAN, ">"),
    LESS_THAN(LessThanOperator::new, LessThanOperator.class, Word.LESS_THAN, Icon.LESSTHAN, "<"),
    GREATER_OR_EQUAL_THAN(GreaterOrEqualThanOperator::new, GreaterOrEqualThanOperator.class, Word.GREATER_OR_EQUAL_THAN, Icon.GREATEROREQUALTHAN, "≥"),
    LESS_OR_EQUAL_THAN(LessOrEqualThanOperator::new, LessOrEqualThanOperator.class, Word.LESS_OR_EQUAL_THAN, Icon.LESSOREQUALTHAN, "≤"),
    AND(AndOperator::new, AndOperator.class, Word.AND, Icon.AND, "∧"),
    OR(OrOperator::new, OrOperator.class, Word.OR, Icon.OR, "∨"),
    NOT(NotOperator::new, NotOperator.class, Word.NOT, Icon.NOT, "¬"),
    CONCAT(ConcatOperator::new, ConcatOperator.class, Word.CONCAT, Icon.CONCAT, "∪");

    private static Map<Class<? extends Operator>, OperatorType> OPERATOR_TYPES;
    private final Function<Statement, Operator> creator;
    private final Word name;
    private final Icon icon;
    private final String stringRep;

    private OperatorType(Function<Statement, Operator> creator, Class<? extends Operator> type, Word name, Icon icon, String stringRep) {
        init(type);
        this.creator = creator;
        this.name = name;
        this.icon = icon;
        this.stringRep = stringRep;
    }

    private void init(Class<? extends Operator> type) {
        if (OPERATOR_TYPES == null) {
            OPERATOR_TYPES = new HashMap<>();
        }
        OPERATOR_TYPES.put(type, this);
    }

    public String getName() {
        return Language.get(name);
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return stringRep;
    }

    public Operator create(Statement parent) {
        return creator.apply(parent);
    }

    public static Set<OperatorType> getAll() {
        return EnumSet.allOf(OperatorType.class);
    }

    public static OperatorType valueOf(Operator operator) {
        return OPERATOR_TYPES.get(operator.getClass());
    }
}
