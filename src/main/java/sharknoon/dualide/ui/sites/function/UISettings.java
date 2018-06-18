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

import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * TODO move to an css file in the future
 * @author Josua Frank
 */
public class UISettings {

    //
    //***Workspace***
    //
    //Zoomfactor of the main zoom of the workspace
    public static final double WORKSPACE_ZOOM_FACTOR = 1.5;
    //The duration of the zoom mentioned above
    public static final Duration WORKSPACE_ZOOM_DURATION = Duration.millis(100);
    //The max width of the workspace
    public static final double WORKSPACE_MAX_X = 5000;
    //The max height of the workspace
    public static final double WORKSPACE_MAX_Y = 3000;
    //The padding inside the workspace, just for optics
    public static final double WORKSPACE_PADDING = 50;
    //The line width of the workspace, just for optics
    public static final double WORKSPACE_LINE_WIDTH = 3;
    //The color of the line around the workspace, just for optics
    public static final Color WORKSPACE_LINE_COLOR = Color.BLACK;
    //The color of the background of the workspace
    public static final Color WORKSPACE_BACKGROUND_COLOR = Color.rgb(0, 0, 0, 0.50);
    //The fading Duration between the background images
    public static final Duration WORKSPACE_BACKGROUND_IMAGE_FADING_DURATION = Duration.seconds(1);
    //The period of a image being in the background before being changed in seconds
    public static final int WORKSPACE_BACKGROUND_IMAGE_SHOWING_DURATION = 60;
    //
    //***Block***
    //
    //The width of the stroke of the border
    public static final double BLOCK_BORDER_STROKE_WIDTH = 1;
    //The color of the stroke of the border
    public static final Color BLOCK_BORDER_STROKE_COLOR = Color.WHITE;
    //The grid snapping of the x axis of a block, typically the same as the y axis
    public static final double BLOCK_GRID_SNAPPING_X = 100;
    //The grid spapping of the y axis of a block
    public static final double BLOCK_GRID_SNAPPING_Y = 100;
    //The duration of the selection shadow to appear
    public static final Duration BLOCK_SELECTION_SHADOW_DURATION = Duration.millis(50);
    //The radius of the selection shadow
    public static final double BLOCK_SELECTION_SHADOW_RADIUS = 50;
    //The color of the selection shadow
    public static final Color BLOCK_SELECTION_SHADOW_COLOR = Color.CORNFLOWERBLUE;
    //The duration of the moving shadow to appear
    public static final Duration BLOCK_MOVING_SHADOW_DURATION = Duration.millis(150);
    //The radius of the moving shadow
    public static final double BLOCK_MOVING_SHADOW_RADIUS = 100;
    //The color of the moving shadow
    public static final Color BLOCK_MOVING_SHADOW_COLOR = Color.valueOf("0095ed");
    //The duration of the block snap to its correct position
    public static final Duration BLOCK_MOVING_DURATION = Duration.millis(50);
    //The color of the shadow stroke of the block
    public static final Color BLOCK_PREDICTION_SHADOW_STROKE_COLOR = Color.GREY;
    //The width of the shadow stroke of the block
    public static final double BLOCK_PREDICTION_SHADOW_STROKE_WIDTH = BLOCK_BORDER_STROKE_WIDTH;
    //The threshold in which to toggle between a describing text and the body itself
    public static final double BLOCK_ZOOMING_BODY_THRESHOLD = 5;
    //
    //***Dot***
    //
    //The duration of the dots of a block to fade in and out
    public static final Duration DOTS_MOVING_DURATION = Duration.millis(50);
    //The Color of the input dots around the block
    public static final Color DOT_INPUT_COLOR = Color.ORANGE;
    //The Color of the output dots around the block
    public static final Color DOT_OUTPUT_COLOR = Color.BLUE;
    //The size of the dots around the block
    public static final double DOT_SIZE = 8;
    //
    //***Line***
    //
    //The color of the Line
    public static final Color LINE_COLOR = Color.WHITE;
    //The width of the line
    public static final double LINE_WIDTH = 5;
    //The duration of the selection shadow to appear
    public static final Duration LINE_SELECTION_SHADOW_DURATION = Duration.millis(50);
    //The radius of the selection shadow
    public static final double LINE_SELECTION_SHADOW_RADIUS = 50;
    //The color of the selection shadow
    public static final Color LINE_SELECTION_SHADOW_COLOR = Color.CORNFLOWERBLUE;
    //The value how sharp the curves should be
    public static final double LINE_CONTROL_OFFSET = 150;
    //
    //***General
    //
}
