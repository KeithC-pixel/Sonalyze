package org.sonalyze.ui.tabs

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.Tab
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import org.sonalyze.Workspace
import org.sonalyze.ui.tabs.SonalyzeTab
import org.sonalyze.ui.TabCompanion
import java.util.UUID

/**
 *
 */
@Deprecated(
    "Class contains unfinished functionality and is disabled — do not use!",
    DeprecationLevel.ERROR
)
class WorkspaceTab(uuid: UUID = UUID.randomUUID()) : SonalyzeTab() {
    init {
        text = "Unnamed Workspace"
    }

    companion object : TabCompanion {
        override var builtContent: Node? = null
    }

    private var isSetup = false

    // Workspace Setup
    private var reloadButton: Button? = null

    // Workspace
    private var viewWorkSpacesButton: Button? = null
    private var guideButton: Button? = null

    /**
     * Sets up the workspace based on the given CSV
     *
     * @param filePath The file path of a CSV file
     */
    fun setupFromCVS(filePath: String) {

    }

    //
    private fun setupContent(): Node = VBox(0.0).apply {
            children.add(Label("No Content"))
            alignment = Pos.CENTER
        }

    // Generate the core content of the workspace
    override fun buildContent() {
        builtContent = setupContent()
    }

    override fun getBuiltContent(): Node? = builtContent

    //
    override fun onOpen(firstOpen: Boolean) {
        // New work space functionality

    }
}