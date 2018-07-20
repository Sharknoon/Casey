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
package sharknoon.casey.ide.logic.statements.operators;

import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.*;
import java.util.function.Function;

/**
 *
 * @author Josua Frank
 */
public enum OperatorType {
    ADD(AddOperator::new, AddOperator.class, PrimitiveType.NUMBER, Word.ADD, Icon.PLUS, "+"),
    SUBTRACT(SubtractOperator::new, SubtractOperator.class, PrimitiveType.NUMBER, Word.SUBTRACT, Icon.MINUS, "-"),
    MULTIPLY(MultiplyOperator::new, MultiplyOperator.class, PrimitiveType.NUMBER, Word.MULTIPLY, Icon.MULTIPLY, "×"),
    DIVIDE(DivideOperator::new, DivideOperator.class, PrimitiveType.NUMBER, Word.DIVIDE, Icon.DIVIDE, "÷"),
    MODULO(ModuloOperator::new, ModuloOperator.class, PrimitiveType.NUMBER, Word.MODULO, Icon.MODULO, "mod"),
    EQUALS(EqualsOperator::new, EqualsOperator.class, PrimitiveType.BOOLEAN, Word.EQUALS, Icon.EQUAL, "="),
    NOT_EQUALS(NotEqualsOperator::new, NotEqualsOperator.class, PrimitiveType.BOOLEAN, Word.NOT_EQUALS, Icon.NOTEQUAL, "≠"),
    GREATER_THAN(GreaterThanOperator::new, GreaterThanOperator.class, PrimitiveType.BOOLEAN, Word.GREATER_THAN, Icon.GREATERTHAN, ">"),
    LESS_THAN(LessThanOperator::new, LessThanOperator.class, PrimitiveType.BOOLEAN, Word.LESS_THAN, Icon.LESSTHAN, "<"),
    GREATER_OR_EQUAL_THAN(GreaterOrEqualThanOperator::new, GreaterOrEqualThanOperator.class, PrimitiveType.BOOLEAN, Word.GREATER_OR_EQUAL_THAN, Icon.GREATEROREQUALTHAN, "≥"),
    LESS_OR_EQUAL_THAN(LessOrEqualThanOperator::new, LessOrEqualThanOperator.class, PrimitiveType.BOOLEAN, Word.LESS_OR_EQUAL_THAN, Icon.LESSOREQUALTHAN, "≤"),
    AND(AndOperator::new, AndOperator.class, PrimitiveType.BOOLEAN, Word.AND, Icon.AND, "∧"),
    OR(OrOperator::new, OrOperator.class, PrimitiveType.BOOLEAN, Word.OR, Icon.OR, "∨"),
    NOT(NotOperator::new, NotOperator.class, PrimitiveType.BOOLEAN, Word.NOT, Icon.NOT, "¬"),
    CONCAT(ConcatOperator::new, ConcatOperator.class, PrimitiveType.TEXT, Word.CONCAT, Icon.CONCAT, "∪"),
    LENGTH(LengthOperator::new, LengthOperator.class, PrimitiveType.NUMBER, Word.LENGTH, Icon.LENGTH, "↔");

    private static Map<Class<? extends Operator>, OperatorType> OPERATOR_TYPES_BY_CLASS;
    private static Map<PrimitiveType, Set<OperatorType>> OPERATOR_TYPES_BY_RETURN_TYPE;

    private final Function<Statement, Operator> creator;
    private final Word name;
    private final Icon icon;
    private final String stringRep;
    
    public static Set<OperatorType> forType(Type type) {
        if (type instanceof PrimitiveType) {
            return OPERATOR_TYPES_BY_RETURN_TYPE.get(type);
        }
        return new HashSet<>();
    }
    
    OperatorType(Function<Statement, Operator> creator, Class<? extends Operator> type, PrimitiveType returnType, Word name, Icon icon, String stringRep) {
        init(type, returnType);
        this.creator = creator;
        this.name = name;
        this.icon = icon;
        this.stringRep = stringRep;
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
        return OPERATOR_TYPES_BY_CLASS.get(operator.getClass());
    }
    
    private void init(Class<? extends Operator> type, PrimitiveType returnType) {
        if (OPERATOR_TYPES_BY_CLASS == null) {
            OPERATOR_TYPES_BY_CLASS = new HashMap<>();
        }
        OPERATOR_TYPES_BY_CLASS.put(type, this);
        if (OPERATOR_TYPES_BY_RETURN_TYPE == null) {
            OPERATOR_TYPES_BY_RETURN_TYPE = new HashMap<>();
        }
        if (OPERATOR_TYPES_BY_RETURN_TYPE.containsKey(returnType)) {
            OPERATOR_TYPES_BY_RETURN_TYPE.get(returnType).add(this);
        } else {
            Set<OperatorType> OPERATOR_TYPES_FOR_RETURN_TYPE = new LinkedHashSet<>();
            OPERATOR_TYPES_FOR_RETURN_TYPE.add(this);
            OPERATOR_TYPES_BY_RETURN_TYPE.put(returnType, OPERATOR_TYPES_FOR_RETURN_TYPE);
        }
    }

}
