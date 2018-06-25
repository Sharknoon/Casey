package sharknoon.dualide.ui.bodies;
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

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.statements.calls.Call;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;

import java.util.List;

public class CallBody extends Body<Call<?>> {
    
    private static final int DEFAULT_MARGIN = 5;
    
    private ObservableList<Node> content;
    
    public CallBody(Call<?> statement) {
        super(statement);
        HBox contentNode = createContentNode();
        
        setContent(contentNode);
    }
    
    private HBox createContentNode() {
        Call<?> call = getStatement().get();
        
        HBox hBoxContent = new HBox();
        hBoxContent.setPrefSize(0, 0);
        hBoxContent.setAlignment(Pos.CENTER_LEFT);
        
        //contains nulls for empty parameter
        content = callsToNodes(call);
        
        hBoxContent.setSpacing(DEFAULT_MARGIN);
        Bindings.bindContentBidirectional(hBoxContent.getChildren(), content);
        
        return hBoxContent;
    }
    
    public void extend() {
    
    }
    
    public void reduce() {
    
    }
    
    private ObservableList<Node> callsToNodes(Call<?> o) {
        ObservableList<Node> listNode = FXCollections.observableArrayList();
        List<? extends ValueReturnable> calls = o.getCalls();
        for (int i = 0; i < calls.size(); i++) {
            ValueReturnable call = calls.get(i);
            HBox hBoxCall = new HBox();
            
            ObjectProperty<Image> image = getIcon(call);
            ImageView icon = new ImageView();
            icon.setPreserveRatio(true);
            Image im = image.get();
            if (im.getWidth() > im.getHeight()) {
                icon.setFitWidth(50);
            } else {
                icon.setFitHeight(50);
            }
            icon.imageProperty().bind(image);
            
            StringProperty name = getName(call);
            Label label = new Label();
            label.textProperty().bind(name);
            
            hBoxCall.getChildren().addAll(icon, label);
            listNode.add(hBoxCall);
            if (i < calls.size() - 1) {
                Node arrow = Icons.get(Icon.BACKGROUND, 40);
                listNode.add(arrow);
            }
        }
        return listNode;
    }
    
    private ObjectProperty<Image> getIcon(ValueReturnable vr) {
        ObjectProperty<Icon> icon;
        if (vr instanceof Item) {
            icon = ((Item) vr).getSite().tabIconProperty();
        } else {
            icon = new SimpleObjectProperty<>(Icon.VOID);
        }
        //evtl platform.runlater
        return Icons.iconToImageProperty(icon);
    }
    
    private StringProperty getName(ValueReturnable vr) {
        if (vr instanceof Item) {
            return ((Item) vr).nameProperty();
        }
        return new SimpleStringProperty("ERROR");
    }
    
}
