package sharknoon.dualide.ui.browsers;

import javafx.scene.Node;
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
    
    public ValuePopUp showValuePopUp() {
        return new ValuePopUp(ownerNode, statementConsumer, parent, allowedType);
    }
}