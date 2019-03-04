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

import javafx.application.Application
import javafx.beans.binding.DoubleExpression
import javafx.beans.binding.StringExpression
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.text.Font
import javafx.stage.Stage
import tornadofx.*
import java.util.concurrent.CompletableFuture

class UpdateProgressView : View() {
    override val root = stackpane {
        prefWidth = 400.0
        vbox(10) {
            spacing = 10.0
            padding = Insets(10.0)
            progressbar {
                maxWidth = Double.MAX_VALUE
                prefHeight = 30.0
                progressProperty().bind(progressProperty)
            }
            label {
                font = Font.font(16.0)
                textProperty().bind(descriptionProperty)
            }
        }
    }
}

class ProgressApp : App(UpdateProgressView::class) {

    override fun start(stage: Stage) {
        stage.title = "Casey Updater"
        stage.icons.clear()
        stage.setOnCloseRequest { onClose.run() }
        stage.show()
    }

}

private var progressProperty: DoubleExpression = SimpleDoubleProperty()
private var descriptionProperty: StringExpression = SimpleStringProperty()
private var onClose: Runnable = Runnable { }

fun show(progressPar: DoubleExpression, descriptionPar: StringExpression, onClosePar: Runnable) {
    CompletableFuture.runAsync {
        progressProperty = progressPar
        descriptionProperty = descriptionPar
        onClose = onClosePar
        Application.launch(ProgressApp::class.java)
    }
}

fun main(args: Array<String>) {
    launch<ProgressApp>(args)
}
