package org.sonalyze.ui.pages

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import org.sonalyze.audio.AudioDictator
import org.sonalyze.audio.Dictation
import org.sonalyze.data.DataSet
import org.sonalyze.ui.KeyBoundButton
import org.sonalyze.ui.Workspace
import java.io.File

/**
 * FileSelectionPage is a utility class that acts as a page that allows the user to select a CSV file
 * to view the data of.
 */
class FileSelectionPage : Page(15.0, {
    // On FileSelectionPage open...

    // Get display pane for CSV files
    val pane = Workspace.instance?.fileSelectionPage?.children?.find { it is VBox } as? VBox
    pane?.let {
        // Get Desktop CSV files
        val homePath = System.getProperty("user.home")
        val desktop = File(homePath, "Desktop")

        // Clear old elements
        pane.children.clear()

        AudioDictator.queueDictation("Reading desktop for dot CSV files...")

        // Confirm desktop is available
        if (desktop.exists() && desktop.isDirectory) {
            // Read desktop for CSV files
            var i = 0
            desktop.listFiles { file ->
                if (file.isFile && file.name.endsWith(".csv", true)) {
                    i++

                    AudioDictator.queueDictation("Press $i for ${file.name.removeSuffix(".csv")} dot CSV")

                    it.children.add(KeyBoundButton("[$i] ${file.name}", KeyCode.getKeyCode("$i")) {
                        // Set-up column selection page for file
                        Workspace.instance?.columnSelectionPage?.currentDataSet = DataSet(file)
                        Workspace.instance?.updatePage(Workspace.instance?.columnSelectionPage!!)
                    })
                }
                false
            }

            // If no CSV file was found, return to main page
            if (i == 0) {
                Workspace.instance?.updatePage(accompanyingDictation = Dictation("No CSV files found on desktop, going back to main menu."))
            }
        }
    }
}) {
    init {
        // FileSelectionPage content...
        alignment = Pos.CENTER
        children.add(Label("Please select a CSV to import from desktop,"))
        children.addAll(
            VBox(10.0).apply {
                alignment = Pos.CENTER
                children.addAll(

                )
            }
        )
    }
}