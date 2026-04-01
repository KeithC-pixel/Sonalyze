package org.sonalyze.ui

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import org.sonalyze.audio.AudioDictator
import org.sonalyze.audio.Dictation
import org.sonalyze.ui.pages.AnalysisPage
import org.sonalyze.ui.pages.ColumnSelectionPage
import org.sonalyze.ui.pages.FileSelectionPage
import org.sonalyze.ui.pages.MainPage
import org.sonalyze.ui.pages.Page

/**
 * Workspace is a singleton class that should only ever be created on the start-up of SonalyzeApp.
 * It manages the pages of the workspace, file import, column selection, data viewing — and is built
 * to possibly later support multiple instances.
 */
class Workspace : VBox() {
    companion object {
        /*
        | JavaFX is very finicky in Kotlin when managing values and can cause freeze-ups when fetching
        | properties or using methods off of the JavaFX thread particularly from the Application class
        | (in this project be SonalyzeApp). That's why the instance reference should be used to get the
        | Workspace instead of getting it through SonalyzeApp.
        |
        | Instance is set on initialization of the class.
         */
        var instance: Workspace? = null
            private set
    }

    // Allows for keys to be bound to buttons
    private val boundKeys: MutableMap<KeyCode, KeyBoundButton> = mutableMapOf()

    // Header object
    private val header: VBox = VBox(10.0).apply {
        alignment = Pos.CENTER

        children.addAll(
            Separator(),
            Label("Sonalyze").apply {
                style = "-fx-font-size: 20px; -fx-font-weight: bold;"
            },
            Separator()
        )
    }

    // Declare pages sync with app thread
    val mainPage: Page = MainPage()
    val fileSelectionPage: Page = FileSelectionPage()
    val columnSelectionPage: ColumnSelectionPage = ColumnSelectionPage()
    val analysisPage: AnalysisPage = AnalysisPage()

    /**
     * A function the triggers a key press, and whatever button that's bound to it.
     *
     * @param event The event of the key press
     */
    fun onKeyPress(event: KeyEvent) {
        if (event.code == KeyCode.ESCAPE) {
            updatePage(mainPage)
            return
        }

        if (event.code == KeyCode.RIGHT) {
            AudioDictator.skip()
            return
        }

        boundKeys[event.code]?.function()
    }

    /**
     * Changes the root Pane which is displayed
     *
     * @param pageElement The pane to use as a page, by default it will open main page
     */
    fun updatePage(pageElement: Page = mainPage, accompanyingDictation: Dictation? = null) {
        AudioDictator.clearQueue()
        if (accompanyingDictation != null) AudioDictator.queueDictation(accompanyingDictation)

        if (children.size >= 2) {
            children.removeAt(1)
        }

        children.add(1, pageElement)
        pageElement.onOpen()

        registerFromPane(pageElement)
    }

    /**
     * Check the descendants (recursive children) for KeyBoundButtons and register
     *
     * @param pane Pane to check descendants of...
     * @param clear Used inside of recursion to remove old bindings or not
     */
    fun registerFromPane(pane: Pane, clear: Boolean = true) {
        if (clear) boundKeys.clear()
        for (child in pane.children) {
            if (child is KeyBoundButton) {
                boundKeys[child.key] = child
                continue
            }
            if (child is Pane) registerFromPane(child, false)
        }
    }

    init {
        // Set instance
        instance = this

        // Setup page
        children.add(0, header)
        updatePage()
    }
}