package sharknoon.dualide.ui.bodies;

import javafx.scene.Node;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.Type;

import java.util.function.Consumer;

public class ValuePopUpBuilder {
    
    public static ValuePopUpBuilder create(Node ownerNode, Consumer<Statement> statementConsumer) {
        return new ValuePopUpBuilder(ownerNode, statementConsumer);
    }
    
    private final Node ownerNode;
    private final Consumer<Statement> statementConsumer;
    private Statement parent = null;
    private Type allowedType = null;
    private boolean allowValueCreation = true;
    private Function onlyFunction = null;
    private Class onlyClass = null;
    
    public ValuePopUpBuilder(Node ownerNode, Consumer<Statement> statementConsumer) {
        this.ownerNode = ownerNode;
        this.statementConsumer = statementConsumer;
    }
    
    public ValuePopUpBuilder setParent(Statement parent) {
        this.parent = parent;
        return this;
    }
    
    public ValuePopUpBuilder setAllowedType(Type allowedType) {
        this.allowedType = allowedType;
        return this;
    }
    
    public ValuePopUpBuilder setAllowValueCreation(boolean allowValueCreation) {
        this.allowValueCreation = allowValueCreation;
        return this;
    }
    
    public ValuePopUpBuilder showOnlyClassChilds(Class clazz) {
        this.onlyClass = clazz;
        return this;
    }
    
    public ValuePopUpBuilder showOnlyFunctionChilds(Function fun) {
        this.onlyFunction = fun;
        return this;
    }
    
    public ValuePopUp showValuePopUp() {
        return new ValuePopUp(ownerNode, statementConsumer, parent, allowedType, allowValueCreation, onlyClass, onlyFunction);
    }
}