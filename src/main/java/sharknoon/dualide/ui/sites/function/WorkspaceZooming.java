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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.ui.misc.ZoomConsumable;

/**
 * @author Josua Frank
 */
public class WorkspaceZooming implements MouseConsumable, ZoomConsumable {

    private final FunctionSite functionSite;
    private final Timeline zoomTimeline = new Timeline();

    WorkspaceZooming(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    private DoubleProperty zoomFactor;

    public double getZoomFactor() {
        if (zoomFactor == null) {
            zoomFactor = functionSite.getLogicSite().getRoot().scaleXProperty();
        }
        return zoomFactor.get();
    }

    public DoubleProperty zoomFactorProperty() {
        if (zoomFactor == null) {
            zoomFactor = functionSite.getLogicSite().getRoot().scaleXProperty();
        }
        return zoomFactor;
    }

    @Override
    public void onScroll(ScrollEvent event) {
        zoom(
                functionSite.getLogicSite().getRoot(),
                event.getDeltaY() < 0
                        ? 1 / UISettings.WORKSPACE_ZOOM_FACTOR
                        : UISettings.WORKSPACE_ZOOM_FACTOR,
                event.getSceneX(),
                event.getSceneY()
        );
    }

    @Override
    public void onZoom(ZoomEvent event) {
        zoom(
                functionSite.getLogicSite().getRoot(),
                event.getZoomFactor(),
                event.getSceneX(),
                event.getSceneY()
        );
    }

    private void zoom(Node node, double factor, double x, double y) {
        var oldAbsoluteScale = node.getScaleX();
        var newAbsoluteScale = oldAbsoluteScale * factor;
        if (newAbsoluteScale < 0.25) {
            newAbsoluteScale = 0.25;
        } else if (newAbsoluteScale > 10) {
            newAbsoluteScale = 10;
        }

        var newRelativeScale = (newAbsoluteScale / oldAbsoluteScale) - 1;
        var bounds = node.localToScene(node.getBoundsInLocal());

        var dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        var dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        var deltaX = node.getTranslateX() - newRelativeScale * dx;
        var deltaY = node.getTranslateY() - newRelativeScale * dy;

        zoomTimeline.getKeyFrames().setAll(
                new KeyFrame(UISettings.WORKSPACE_ZOOM_DURATION,
                        new KeyValue(node.scaleXProperty(), newAbsoluteScale),
                        new KeyValue(node.scaleYProperty(), newAbsoluteScale),
                        new KeyValue(node.translateXProperty(), deltaX),
                        new KeyValue(node.translateYProperty(), deltaY)
                )
        );
        Platform.runLater(() -> {
            zoomTimeline.stop();
            zoomTimeline.play();
        });
    }

}
