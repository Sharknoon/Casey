package sharknoon.dualide.ui.flowchart;

import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author Josua Frank
 */
public class Settings {

    public static double zoomFactor = 1.5;
    public static Duration zoomDuration = Duration.millis(100);
    public static double maxWorkSpaceX = 5000;
    public static double maxWorkSpaceY = 3000;
    public static double gridSnappingX = 100;
    public static double gridSnappingY = 100;
    public static double paddingInsideWorkSpace = 50;
    public static double workspaceLineWidth = 3;
    public static Color workspaceLineColor = Color.BLACK;

    public static void setZoomFactor(double zoomFactor) {
        Settings.zoomFactor = zoomFactor;
    }

    public static void setZoomDuration(Duration zoomDuration) {
        Settings.zoomDuration = zoomDuration;
    }

    public static void setMaxWorkSpaceX(double maxWorkSpaceX) {
        Settings.maxWorkSpaceX = maxWorkSpaceX;
    }

    public static void setMaxWorkSpaceY(double maxWorkSpaceY) {
        Settings.maxWorkSpaceY = maxWorkSpaceY;
    }

    public static void setGridSnappingX(double gridSnappingX) {
        Settings.gridSnappingX = gridSnappingX;
    }

    public static void setGridSnappingY(double gridSnappingY) {
        Settings.gridSnappingY = gridSnappingY;
    }

    public static void setPaddingInsideWorkSpace(double paddingInsideWorkSpace) {
        Settings.paddingInsideWorkSpace = paddingInsideWorkSpace;
    }

    public static void setWorkspaceLineWidth(double workspaceLineWidth) {
        Settings.workspaceLineWidth = workspaceLineWidth;
    }

}
