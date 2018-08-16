package sharknoon.casey.updater.ui

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

import javafx.application.*
import javafx.beans.binding.*
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.stage.Stage
import java.util.concurrent.*

class ProgressStage : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Casey Updater"
        primaryStage.icons.clear()
        val stackPaneRoot = StackPane()
        stackPaneRoot.prefWidth = 400.0
        val vBoxContent = VBox(10.0)
        vBoxContent.padding = Insets(10.0)

        val progressBar = ProgressBar()
        progressBar.maxWidth = java.lang.Double.MAX_VALUE
        progressBar.prefHeight = 30.0
        progress?.addListener { _, _, newValue -> Platform.runLater { progressBar.progress = newValue.toDouble() } }

        val labelDescription = Label()
        labelDescription.font = Font.font(16.0)
        description?.addListener { _, _, newValue -> Platform.runLater { labelDescription.text = newValue } }

        vBoxContent.children.addAll(progressBar, labelDescription)
        stackPaneRoot.children.add(vBoxContent)
        val scene = Scene(stackPaneRoot)
        primaryStage.scene = scene
        primaryStage.setOnCloseRequest { onClose?.run() }
        primaryStage.show()
    }


}

private var progress: DoubleExpression? = null
private var description: StringExpression? = null
private var onClose: Runnable? = null

fun show(progressPar: DoubleExpression, descriptionPar: StringExpression, onClosePar: Runnable) {
    CompletableFuture.runAsync {
        progress = progressPar
        description = descriptionPar
        onClose = onClosePar
        Application.launch(ProgressStage::class.java)
    }
}
