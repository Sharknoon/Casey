package sharknoon.dualide.ui.flowchart;

import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author Josua Frank
 */
public class UISettings {

    //***Workspace***
    //Zoomfactor of the main zoom of the workspace
    public static double zoomFactor = 1.5;
    //The duration of the zoom mentioned above
    public static Duration zoomDuration = Duration.millis(100);
    //The max width of the workspace
    public static double maxWorkSpaceX = 5000;
    //The max height of the workspace
    public static double maxWorkSpaceY = 3000;
    //The grid snapping of the x axis, typically the same as the y axis
    public static double gridSnappingX = 100;
    //The grid spapping of the y axis
    public static double gridSnappingY = 100;
    //The padding inside the workspace, just for optics
    public static double paddingInsideWorkSpace = 50;
    //The line width of the workspace, just for optics
    public static double workspaceLineWidth = 3;
    //The color of of the line around the workspace, just for optics
    public static Color workspaceLineColor = Color.BLACK;
    //The threshold in which a moving mouse is counted as a movement or a click for the context menue
    public static double contextMenuThreshold = 2;
    //The threshold in which a moving mouse is counted as a movement or a click for the selection of a block
    public static double blockSelectionThreshold = 2;
    //
    //***Block***
    //The duration of the dots of a block to move in and out
    public static Duration DOTS_MOVING_DURATION = Duration.millis(50);
    //The distance the dots travel in and out
    public static double DOTS_MOVING_DISTANCE = 5;
    //The duration of the selection shadow to appear
    public static Duration BLOCK_SHADOW_SELECTION_DURATION = Duration.millis(50);
    //The radius of the selection shadow
    public static double BLOCK_SHADOW_SELECTION_RADIUS = 50;
    //The color of the selection shadow
    public static Color BLOCK_SHADOW_SELECTION_COLOR = Color.CORNFLOWERBLUE;
    //The duration of the moving shadow to appear
    public static Duration BLOCK_SHADOW_MOVING_DURATION = Duration.millis(150);
    //The radius of the moving shadow
    public static double BLOCK_SHADOW_MOVING_RADIUS = 100;
    //The color of the moving shadow
    public static Color BLOCK_SHADOW_MOVING_COLOR = Color.valueOf("0095ed");
    //The duration of the block snap to its correct position
    public static Duration BLOCK_MOVING_DURATION = Duration.millis(50);
    //The color of the shadow stroke of the block
    public static Color SHADOW_STROKE_COLOR = Color.GREY;
    //The width of the shadow stroke of the block
    public static double SHADOW_STROKE_WIDTH = 5;

    public static void setZoomFactor(double zoomFactor) {
        UISettings.zoomFactor = zoomFactor;
    }

    public static void setZoomDuration(Duration zoomDuration) {
        UISettings.zoomDuration = zoomDuration;
    }

    public static void setMaxWorkSpaceX(double maxWorkSpaceX) {
        UISettings.maxWorkSpaceX = maxWorkSpaceX;
    }

    public static void setMaxWorkSpaceY(double maxWorkSpaceY) {
        UISettings.maxWorkSpaceY = maxWorkSpaceY;
    }

    public static void setGridSnappingX(double gridSnappingX) {
        UISettings.gridSnappingX = gridSnappingX;
    }

    public static void setGridSnappingY(double gridSnappingY) {
        UISettings.gridSnappingY = gridSnappingY;
    }

    public static void setPaddingInsideWorkSpace(double paddingInsideWorkSpace) {
        UISettings.paddingInsideWorkSpace = paddingInsideWorkSpace;
    }

    public static void setWorkspaceLineWidth(double workspaceLineWidth) {
        UISettings.workspaceLineWidth = workspaceLineWidth;
    }

    public static void setWorkspaceLineColor(Color workspaceLineColor) {
        UISettings.workspaceLineColor = workspaceLineColor;
    }

    public static void setContextMenuThreshold(double contextMenuThreshold) {
        UISettings.contextMenuThreshold = contextMenuThreshold;
    }

    public static void setBlockSelectionThreshold(double blockSelectionThreshold) {
        UISettings.blockSelectionThreshold = blockSelectionThreshold;
    }

    public static void setDOTS_MOVING_DURATION(Duration DOTS_MOVING_DURATION) {
        UISettings.DOTS_MOVING_DURATION = DOTS_MOVING_DURATION;
    }

    public static void setDOTS_MOVING_DISTANCE(double DOTS_MOVING_DISTANCE) {
        UISettings.DOTS_MOVING_DISTANCE = DOTS_MOVING_DISTANCE;
    }

    public static void setBLOCK_SHADOW_SELECTION_DURATION(Duration BLOCK_SHADOW_SELECTION_DURATION) {
        UISettings.BLOCK_SHADOW_SELECTION_DURATION = BLOCK_SHADOW_SELECTION_DURATION;
    }

    public static void setBLOCK_SHADOW_SELECTION_RADIUS(double BLOCK_SHADOW_SELECTION_RADIUS) {
        UISettings.BLOCK_SHADOW_SELECTION_RADIUS = BLOCK_SHADOW_SELECTION_RADIUS;
    }

    public static void setBLOCK_SHADOW_SELECTION_COLOR(Color BLOCK_SHADOW_SELECTION_COLOR) {
        UISettings.BLOCK_SHADOW_SELECTION_COLOR = BLOCK_SHADOW_SELECTION_COLOR;
    }

    public static void setBLOCK_SHADOW_MOVING_DURATION(Duration BLOCK_SHADOW_MOVING_DURATION) {
        UISettings.BLOCK_SHADOW_MOVING_DURATION = BLOCK_SHADOW_MOVING_DURATION;
    }

    public static void setBLOCK_SHADOW_MOVING_RADIUS(double BLOCK_SHADOW_MOVING_RADIUS) {
        UISettings.BLOCK_SHADOW_MOVING_RADIUS = BLOCK_SHADOW_MOVING_RADIUS;
    }

    public static void setBLOCK_SHADOW_MOVING_COLOR(Color BLOCK_SHADOW_MOVING_COLOR) {
        UISettings.BLOCK_SHADOW_MOVING_COLOR = BLOCK_SHADOW_MOVING_COLOR;
    }

    public static void setBLOCK_MOVING_DURATION(Duration BLOCK_MOVING_DURATION) {
        UISettings.BLOCK_MOVING_DURATION = BLOCK_MOVING_DURATION;
    }

    public static void setSHADOW_STROKE_COLOR(Color SHADOW_STROKE_COLOR) {
        UISettings.SHADOW_STROKE_COLOR = SHADOW_STROKE_COLOR;
    }

    public static void setSHADOW_STROKE_WIDTH(double SHADOW_STROKE_WIDTH) {
        UISettings.SHADOW_STROKE_WIDTH = SHADOW_STROKE_WIDTH;
    }
    
    
}
