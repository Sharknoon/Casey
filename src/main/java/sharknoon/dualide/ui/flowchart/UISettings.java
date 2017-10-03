package sharknoon.dualide.ui.flowchart;

import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author Josua Frank
 */
public class UISettings {

    public static double zoomFactor = 1.5;
    public static Duration zoomDuration = Duration.millis(100);
    public static double maxWorkSpaceX = 5000;
    public static double maxWorkSpaceY = 3000;
    public static double gridSnappingX = 100;
    public static double gridSnappingY = 100;
    public static double paddingInsideWorkSpace = 50;
    public static double workspaceLineWidth = 3;
    public static Color workspaceLineColor = Color.BLACK;
    public static double contextMenuThreshold = 2;
    public static double blockSelectionThreshold = 2;

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
}
