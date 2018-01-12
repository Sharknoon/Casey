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
package sharknoon.dualide.ui.sites.function;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.input.KeyEvent;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.sites.function.blocks.block.Start;
import sharknoon.dualide.ui.sites.function.lines.Line;
import sharknoon.dualide.ui.sites.function.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public class Keyboard {

    private final FunctionSite flowchart;

    public Keyboard(FunctionSite flowchart) {
        this.flowchart = flowchart;
    }

    public void init() {
    }

    public void onKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case DELETE:
                onDELETE();
                break;
            case ESCAPE:
                onESCAPE();
                break;
        }
    }

    private void onDELETE() {
        List<Block> blocksToDelete = Blocks
                .getAllBlocks(flowchart)
                .stream()
                .filter(Block::isSelected)
                .filter(b -> b.getClass() != Start.class)
                .collect(Collectors.toList());
        blocksToDelete.forEach(Block::remove);//Maybe a concurrentmodificationexception
        List<Line> linesToDelete = Lines.getAllLines(flowchart)
                .stream()
                .filter(Line::isSelected)
                .collect(Collectors.toList());
        linesToDelete.forEach(Line::remove);

    }

    private void onESCAPE() {
        if (Lines.isLineDrawing()) {
            Lines.getDrawingLine().remove();
        }
    }
}
