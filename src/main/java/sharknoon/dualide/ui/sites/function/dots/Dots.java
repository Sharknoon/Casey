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
package sharknoon.dualide.ui.sites.function.dots;

import java.util.Optional;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;

/**
 *
 * @author Josua Frank
 */
public class Dots {


    public static Optional<Dot> isOverDot(FunctionSite functionSite, double x, double y) {
        return Blocks.getAllBlocks(functionSite)
                .map(b -> b.getAllDots().stream())
                .flatMap(s -> s)
                .filter(d -> d.getCenterX() == x && d.getCenterY() == y)
                .findFirst();
    }

}
