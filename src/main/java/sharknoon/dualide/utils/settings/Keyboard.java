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
package sharknoon.dualide.utils.settings;

import javafx.scene.input.KeyEvent;
import sharknoon.dualide.logic.blocks.Block;
import sharknoon.dualide.logic.blocks.BlockType;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.ui.MainApplication;
import sharknoon.dualide.ui.frames.Frame;
import sharknoon.dualide.ui.frames.Frames;
import sharknoon.dualide.ui.lines.Line;
import sharknoon.dualide.ui.lines.Lines;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.function.FunctionSite;

import java.util.stream.Collectors;

/**
 *
 * @author Josua Frank
 */
public class Keyboard {

    public static void init() {
        MainApplication.registerInitializable((scene) -> {
            scene.setOnKeyPressed(Keyboard::onKeyReleased);
        });
    }

    private static void onKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case DELETE:
                onDELETE();
                break;
            case ESCAPE:
                onESCAPE();
                break;
        }
    }

    private static void onDELETE() {
        var currentItem = Site.currentSelectedProperty().get();
        if (currentItem != null) {
            if (currentItem.getType().equals(ItemType.FUNCTION)) {
                var functionSite = (FunctionSite) currentItem.getSite();
                var framesToDelete = Frames
                        .getAllFrames(functionSite)
                        .filter(Frame::isSelected)
                        .map(Frame::getBlock)
                        .filter(f -> f.getType() != BlockType.START)
                        .collect(Collectors.toList());
                framesToDelete.forEach(Block::remove);
                var linesToDelete = Lines.getAllLines(functionSite)
                        .filter(Line::isSelected)
                        .collect(Collectors.toList());
                linesToDelete.forEach(Line::remove);
            }
        }
    }

    private static void onESCAPE() {
        var currentItem = Site.currentSelectedProperty().get();
        if (currentItem != null) {
            if (currentItem.getType().equals(ItemType.FUNCTION)) {
                var functionSite = (FunctionSite) currentItem.getSite();
                if (Lines.isLineDrawing(functionSite)) {
                    Lines.getDrawingLine(functionSite).remove();
                }
            }
        }
    }
}
