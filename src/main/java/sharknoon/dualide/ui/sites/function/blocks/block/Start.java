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
package sharknoon.dualide.ui.sites.function.blocks.block;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.BlockContent;

/**
 * Creates a Start block, which is the entry point of a module
 *
 * @author Josua Frank
 */
public class Start extends Block {
    
    public Start(FunctionSite functionSite) {
        super(functionSite);
    }
    
    public Start(FunctionSite functionSite, String id) {
        super(functionSite, id);
    }
    
    @Override
    public double initShapeHeight() {
        return 100;
    }
    
    @Override
    public double initShapeWidth() {
        return 200;
    }
    
    @Override
    public Side[] initDotOutputSides() {
        return new Side[]{Side.BOTTOM};
    }
    
    @Override
    public Side[] initDotInputSides() {
        return new Side[]{};
    }
    
    @Override
    public Shape initBlockShape() {
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setArcWidth(getHeight() < getWidth() ? getHeight() : getWidth());
        rectangle.setArcHeight(getHeight() < getWidth() ? getHeight() : getWidth());
        rectangle.setFill(Color.GREEN);
        return rectangle;
    }
    
    @Override
    public BlockContent initBlockContent() {
        return new StartBockContent();
    }
    
    private static class StartBockContent extends BlockContent {
        
        @Override
        public ObservableList<Text> toText() {
            return FXCollections.emptyObservableList();
        }
    }
    
}
