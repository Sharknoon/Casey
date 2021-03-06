package sharknoon.casey.ide.ui.browsers;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import sharknoon.casey.ide.logic.items.Class;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.statements.calls.CallItem;

import java.util.function.Consumer;

/**
 * @author Josua Frank
 */
class CallBrowser extends VBox {
    
    
    private final Class allowedItems;
    private final Statement parent;
    private final Consumer<Statement> statementConsumer;
    
    
    CallBrowser(Consumer<Statement> statementConsumer, Statement parent, Class allowedItems) {
        super();
        this.parent = parent;
        this.statementConsumer = statementConsumer;
        this.allowedItems = allowedItems;
        init();
        //null no types allowed
        if (allowedItems == null) {
            addNoTypeLabel();
        } else {
            setContent();
        }
    }
    
    private void init() {
        setPadding(new Insets(25));
        setPrefWidth(300);
        setMaxSize(500, 300);
        setSpacing(10);
    }
    
    private void addNoTypeLabel() {
        getChildren().add(BrowserUtils.getNoTypeLabel());
    }
    
    
    private void setContent() {
        ScrollPane scrollPaneFunctionsAndVariables = new ScrollPane();
        scrollPaneFunctionsAndVariables.setFitToHeight(true);
        scrollPaneFunctionsAndVariables.setFitToWidth(true);
        VBox vBoxFunctionsAndVariables = new VBox(10);
        
        allowedItems
                .getChildren()
                .stream()
                .forEach((Item<? extends Item, Class, ? extends Item> i) -> vBoxFunctionsAndVariables.getChildren().add(BrowserUtils.getEntries(i, event -> {
                    if (i.getType() == ItemType.FUNCTION || i.getType() == ItemType.VARIABLE) {
                        statementConsumer.accept(new CallItem(parent, i));
                    }
                })));
        scrollPaneFunctionsAndVariables.setContent(vBoxFunctionsAndVariables);
        
        getChildren().add(scrollPaneFunctionsAndVariables);
    }
    
    
}
