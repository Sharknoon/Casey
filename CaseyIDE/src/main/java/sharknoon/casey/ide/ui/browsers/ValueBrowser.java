package sharknoon.casey.ide.ui.browsers;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import sharknoon.casey.ide.logic.items.Class.ObjectType;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.statements.operators.OperatorType;
import sharknoon.casey.ide.logic.statements.values.ObjectValue;
import sharknoon.casey.ide.logic.statements.values.Value;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.bodies.PopUpUtils;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Josua Frank
 */
class ValueBrowser extends GridPane {
    
    private final VBox vBoxLeft = new VBox();
    private final Type allowedType;
    private final Statement parent;
    private final Consumer<Statement> statementConsumer;
    private VBox vBoxRight = new VBox();
    private GridPane gp = new GridPane();
    private int row = 0;
    private int size = -1;
    
    
    ValueBrowser(Consumer<Statement> statementConsumer, Statement parent, Type allowedType) {
        super();
        this.parent = parent;
        this.statementConsumer = statementConsumer;
        this.allowedType = allowedType;
        init();
        //null no types allowed
        if (allowedType == null) {
            addNoTypeLabel();
        } else {
            addNewValueSelectors();
            addExistingValueSelectors();
        }
    }
    
    private void init() {
        setHgap(10);
        setPadding(new Insets(25));
        setPrefWidth(1300);
        setMaxSize(1500, 500);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(60);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        getColumnConstraints().addAll(col1, col2);
        add(vBoxLeft, 0, 0);
        vBoxLeft.setSpacing(10);
    }
    
    private void addNoTypeLabel() {
        var text = Language.get(Word.TYPE_SELECTION_POPUP_NO_TYPES);
        var labelNoType = new Label(text);
        labelNoType.setFont(Font.font(20));
        labelNoType.setMaxWidth(Double.MAX_VALUE);
        labelNoType.setAlignment(Pos.CENTER);
        add(labelNoType, 0, 0, 2, 1);
    }
    
    private void addNewValueSelectors() {
        addNewValueSparator();
        //primitives
        for (Type type : allowedType == Type.UNDEFINED ? PrimitiveType.getAll() : List.of(allowedType)) {
            if (type != null && type.isPrimitive()) {
                addNewPrimitiveValueSelector(type.getPrimitiveType());
            }
        }
        //objects
        if (allowedType == Type.UNDEFINED || allowedType.isObject()) {
            addNewObjectValueSelectors();
        }
    }
    
    private void addNewValueSparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_NEW_VALUES);
        Node separator = PopUpUtils.getSeparator(text, HPos.CENTER);
        vBoxLeft.getChildren().add(separator);
    }
    
    private void addNewPrimitiveValueSelector(PrimitiveType type) {
        checkIfGridpaneContainsLeftVBox();
        if (size < 0) {
            size = allowedType == Type.UNDEFINED ? PrimitiveType.getAll().size() : 1;
        }
        
        String stringValues = Language.get(Word.VALUE_SELECTION_POPUP_VALUES_EXTENSION);
        
        Node icon = Icons.get(type.getIcon());
        Label text = PopUpUtils.createLabel(type.getLanguageDependentName().get() + stringValues);
        gp.add(new HBox(10, icon, text), 0, row);
        
        FlowPane flowPaneButtons = new FlowPane();
        flowPaneButtons.setHgap(10);
        flowPaneButtons.setVgap(10);
        Button buttonCreation = new Button(type.creationTextProperty().get(), Icons.get(type.getCreationIcon()));
        flowPaneButtons.getChildren().add(buttonCreation);
        
        buttonCreation.setOnAction((event) -> {
            Optional<Value> createdValue = type.createValue(parent);
            createdValue.ifPresent(statementConsumer::accept);
        });
        Set<OperatorType> ots = OperatorType.forType(type);
        ots.forEach(ot -> {
            Button buttonOperation = new Button(ot.getName(), Icons.get(ot.getIcon()));
            buttonOperation.setOnAction((event) -> statementConsumer.accept(ot.create(parent)));
            flowPaneButtons.getChildren().add(buttonOperation);
        });
        
        gp.add(flowPaneButtons, 1, row);
        
        if ((row / 2) + 1 < size) {
            Separator separator = new Separator();
            gp.add(separator, 1, row + 1);
        }
        row += 2;
    }
    
    private void addNewObjectValueSelectors() {
        checkIfGridpaneContainsLeftVBox();
        if (row > 0) {
            Separator separator = new Separator();
            gp.add(separator, 1, row - 1);
        }
        
        String stringValues = Language.get(Word.VALUE_SELECTION_POPUP_VALUES_EXTENSION);
        Node icon = Icons.get(ObjectType.GENERAL.getIcon());
        Label text = PopUpUtils.createLabel(ObjectType.GENERAL.getLanguageDependentName().get() + stringValues);
        gp.add(new HBox(10, icon, text), 0, row);
        
        Button buttonCreation = new Button(ObjectType.GENERAL.creationTextProperty().get(), Icons.get(ObjectType.GENERAL.getCreationIcon()));
        buttonCreation.setOnAction((event) -> {
            Optional<ObjectValue> t;
            if (allowedType == Type.UNDEFINED) {
                t = ObjectType.GENERAL.createValue(parent);
            } else if (allowedType.isObject()) {
                t = ((ObjectType) allowedType).createValue(parent);
            } else {
                t = Optional.empty();
            }
            t.ifPresent(statementConsumer::accept);
        });
        gp.add(buttonCreation, 1, row);
    }
    
    private void checkIfGridpaneContainsLeftVBox() {
        if (!vBoxLeft.getChildren().contains(gp)) {
            gp.setHgap(10);
            gp.setVgap(10);
            gp.setAlignment(Pos.TOP_LEFT);
            ColumnConstraints col0 = new ColumnConstraints();
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHgrow(Priority.ALWAYS);
            gp.getColumnConstraints().addAll(col0, col1);
            vBoxLeft.getChildren().add(gp);
        }
    }
    
    
    private void addExistingValueSelectors() {
        vBoxRight = BrowserUtils.getStatementSelector(allowedType, statementConsumer, parent, List.of(ItemType.FUNCTION, ItemType.VARIABLE, ItemType.PARAMETER));
        String text = Language.get(Word.VALUE_SELECTION_POPUP_EXISTING_VALUES);
        Node separator = PopUpUtils.getSeparator(text, HPos.CENTER);
        vBoxRight.getChildren().add(0, separator);
        add(vBoxRight, 1, 0);
    }
    
}
