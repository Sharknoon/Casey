package sharknoon.dualide.ui.bodies;

import javafx.scene.Node;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.Type;

import java.util.function.Consumer;

public class ValuePopUpBuilder {
    
    public static ValuePopUpBuilder create(Node ownerNode, Consumer<Statement> statementConsumer) {
        return new ValuePopUpBuilder(ownerNode, statementConsumer);
    }
    
    private Node ownerNode;
    private Consumer<Statement> statementConsumer;
    private Statement parent;
    private Type allowedType;
    private boolean allowValueCreation = true;
    
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
    
    public ValuePopUp showValuePopUp() {
        return new ValuePopUp(ownerNode, statementConsumer, parent, allowedType, allowValueCreation);
    }
}