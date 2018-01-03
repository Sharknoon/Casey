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
    //The padding inside the workspace, just for optics
    public static double paddingInsideWorkSpace = 50;
    //The line width of the workspace, just for optics
    public static double workspaceLineWidth = 3;
    //The color of the line around the workspace, just for optics
    public static Color workspaceLineColor = Color.BLACK;
    //The color of the background of the workspace
    public static Color workspaceBackgroundColor = Color.rgb(0, 0, 0, 0.50);
    //The threshold in which a moving mouse is counted as a movement or a click for the context menue
    public static double contextMenuThreshold = 2;
    //The fading Duration between the background images
    public static Duration workspaceBackgroundFadingDuration = Duration.seconds(1);
    //The period of a image being in the background before being changed
    public static Duration workspaceBackgroundImageDuration = Duration.seconds(5);
    //
    //***Block***
    //The width of the stroke of the border
    public static double blockBorderStrokeWidth = 1;
    //The color of the stroke of the border
    public static Color blockBorderStrokeColor = Color.WHITE;
    //The grid snapping of the x axis of a block, typically the same as the y axis
    public static double blockGridSnappingX = 100;
    //The grid spapping of the y axis of a block
    public static double blockGridSnappingY = 100;
    //The duration of the dots of a block to move in and out
    public static Duration dotsMovingDuration = Duration.millis(50);
    //The distance the dots travel in and out
    public static double dotsMovingDistance = 5;
    //The duration of the selection shadow to appear
    public static Duration selectionShadowDuration = Duration.millis(50);
    //The radius of the selection shadow
    public static double selectionShadowRadius = 50;
    //The color of the selection shadow
    public static Color selectionShadowColor = Color.CORNFLOWERBLUE;
    //The duration of the moving shadow to appear
    public static Duration movingShadowDuration = Duration.millis(150);
    //The radius of the moving shadow
    public static double movingShadowRadius = 100;
    //The color of the moving shadow
    public static Color movingShadowColor = Color.valueOf("0095ed");
    //The duration of the block snap to its correct position
    public static Duration blockMovingDuration = Duration.millis(50);
    //The color of the shadow stroke of the block
    public static Color predictionShadowStrokeColor = Color.GREY;
    //The width of the shadow stroke of the block
    public static double predictionShadowStrokeWidth = blockBorderStrokeWidth;
    //The threshold in which a moving mouse is counted as a movement or a click for the selection of a block
    public static double blockSelectionThreshold = 2;
    //The Color of the dots around the block
    public static Color blockDotColor = Color.BLACK;
    //The radius of the dots around the block
    public static double blockDotRadius = 10;
    //
    //***Line***
    //The grid snapping of the x axis of a line, typically the same as the y axis and half of the blocks snapping
    public static double lineGridSnappingX = 50;
    //The grid snapping of the y axis of a line
    public static double lineGridSnappingY = 50;
    
    
}
