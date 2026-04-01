package org.sonalyze.ui.tabs

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.Tab
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import org.sonalyze.Sonalyze
import org.sonalyze.Workspace
import org.sonalyze.ui.tabs.SonalyzeTab
import org.sonalyze.ui.TabCompanion

/**
 * The home tab controls the direct paths to open workspaces and guides
 */
@Deprecated(
    "Class contains unfinished functionality and is disabled — do not use!",
    DeprecationLevel.ERROR
)
class HomeTab: SonalyzeTab() {
    private var newWorkSpaceButton: Button? = null
    private var viewWorkSpacesButton: Button? = null
    private var guideButton: Button? = null

    init {
        text = "Home"
        isClosable = false
    }

    companion object : TabCompanion {
        override var builtContent: Node? = null
    }

    override fun buildContent() {
        builtContent = VBox(15.0).apply {
            newWorkSpaceButton = Button("1: + New Workspace")

            registerMap(KeyCode.DIGIT1, newWorkSpaceButton) {
                Platform.runLater {

                    //Sonalyze.app.addTab(WorkspaceTab(), true)
                }
            }
            viewWorkSpacesButton = Button("2: View Workspaces")
            guideButton = Button("3: Guide")

            padding = Insets(20.0)
            alignment = Pos.CENTER

            children.addAll(
                Label("Sonalyze").apply {
                    style = "-fx-font-size: 20px; -fx-font-weight: bold;"
                },
                Separator(),
                newWorkSpaceButton,
                viewWorkSpacesButton,
                guideButton?.apply {
                    setOnAction {
                        Sonalyze.app.addTab(WorkspaceTab())
                    }
                }
            )
        }
    }

    override fun getBuiltContent(): Node? = builtContent

    override fun onOpen(firstOpen: Boolean) {
    }
}