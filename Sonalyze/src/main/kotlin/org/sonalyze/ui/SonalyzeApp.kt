package org.sonalyze.ui

import atlantafx.base.theme.PrimerLight
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.sonalyze.Sonalyze
import org.sonalyze.audio.AudioDictator

/**
 * SonalyzeApp is the JavaFX application class — it's recommend to NOT modify, set or get or use
 * any values or methods within this class in order to keep the JavaFX application thread going.
 *
 * Use Workspace.instance instead.
 */
class SonalyzeApp : Application() {
    private var workspace: Workspace? = null

    /**
     * Launch the application
     */
    fun initialize() {
        launch(this::class.java)
    }

    override fun start(primaryStage: Stage) {
        // Apply theme
        setUserAgentStylesheet(PrimerLight().userAgentStylesheet)

        // Create tab pane
        workspace = Workspace()

        primaryStage.scene = Scene(workspace, 900.0, 600.0)

        primaryStage.scene.setOnKeyPressed { e ->
            workspace?.onKeyPress(e)
        }

        primaryStage.show()
        primaryStage.title = "Sonalyze"

        primaryStage.setOnCloseRequest {
            AudioDictator.clearQueue()
        }
    }

}