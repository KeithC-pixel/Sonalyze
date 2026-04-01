package org.sonalyze.ui.pages

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import org.sonalyze.audio.AudioDictator
import org.sonalyze.audio.Dictation
import org.sonalyze.audio.SonaSynth
import org.sonalyze.data.WorkspaceSave
import org.sonalyze.ui.KeyBoundButton
import org.sonalyze.ui.Workspace

/**
 * MainPage is the page that handles key navigation points
 */
class MainPage : Page(15.0, {
    // On MainPage open...
    AudioDictator.queueDictation("Welcome to Sonalyze!")
    AudioDictator.queueDictation("Press 1 to import a file")
    AudioDictator.queueDictation("Press 2 to open most recent workspace")
    // AudioDictator.queueDictation("Press 3 for guides")

    AudioDictator.queueDictation("Press Escape at any time to return to this page")
}) {
    init {
        // MainPage content...
        alignment = Pos.CENTER

        children.addAll(HBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                // FILE IMPORTING
                KeyBoundButton("[1] Import File", KeyCode.DIGIT1) {
                    Workspace.instance?.updatePage(Workspace.instance?.fileSelectionPage!!)
                },

                // RESTORE PREVIOUS WORKSPACE
                KeyBoundButton("[2] Restore Workspace", KeyCode.DIGIT2) {
                    val dataset = WorkspaceSave.loadRecent()
                    if (dataset != null) {
                        Workspace.instance?.updatePage(Workspace.instance?.analysisPage!!, Dictation("Opening recent workspace,"))
                    } else {
                        AudioDictator.queueDictation("Unable to find previous workspace.")
                    }
                }

                // GUIDES
                //Button("[3] Guide"),
            )
        })
    }
}