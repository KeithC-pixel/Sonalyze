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

/**
 * ColumnSelectionPage allows the user to select which columns they wish to compare in the dataset
 */
class ColumnSelectionPage : Page(15.0, {
    // On ColumSelectionPage open...
    AudioDictator.queueDictation("Checking columns in CSV file...")

    // Check for selected data file
    if (Workspace.instance?.columnSelectionPage?.currentDataSet == null) {
        Workspace.instance?.updatePage(Workspace.instance?.mainPage!!, Dictation(("Data file not supported, going to main menu.")))
    } else Workspace.instance?.columnSelectionPage?.currentDataSet?.dataFrame?.columns?.size?.let {
        if (it <= 1) {
            // Not enough valid columns
            Workspace.instance?.updatePage(Workspace.instance?.mainPage!!, Dictation("Data file doesn't have enough columns, going to main menu."))
        } else {
            // Update page
            Workspace.instance?.columnSelectionPage?.updatePage()
        }
    }
}) {
    // The dataset that manages how the page is displayed
    var currentDataSet: DataSet? = null

    init {
        // ColumSelectionPage content...
        alignment = Pos.CENTER
        children.add(Label("Please select a independent column,"))
        children.addAll(
            VBox(10.0).apply {
                alignment = Pos.CENTER
                children.addAll(

                )
            }
        )
    }

    /**
     * Adaptively modifies ColumSelection page based on how much information is still needed from
     * the DataSet
     */
    fun updatePage() {
        AudioDictator.clearQueue()
        val label = children.find { it is Label } as Label
        val pane = children.find { it is VBox } as VBox

        val independentValue = currentDataSet?.independentColumn

        if (independentValue != null && currentDataSet?.dependentColumn != null) {
            // Continue on...
            AudioDictator.queueDictation("Moving on...")
            // Transfer DataSet
            Workspace.instance?.analysisPage?.dataset = currentDataSet
            Workspace.instance?.updatePage(Workspace.instance?.analysisPage!!)
            return
        }

        var columnNames = currentDataSet?.dataFrame?.columns()?.filter { it.dtype().isNumeric}?.map { it.name() }

        // Unable to get column names...
        if (columnNames == null) {
            AudioDictator.queueDictation("Unable to access column names, going back to main menu.")
            Workspace.instance?.updatePage(Workspace.instance?.mainPage!!)
            return
        }

        if (independentValue == null) {
            label.text = "Please select an independent column,"
            AudioDictator.queueDictation("Select an independent column,")
        } else {
            label.text = "Please select a dependent column,"
            AudioDictator.queueDictation("Select a dependent column,")
            columnNames = columnNames.minus(independentValue)
        }

        pane.children.clear()

        var i = 0
        columnNames.forEach {
            i++
            AudioDictator.queueDictation("Press $i for the $it column")
            pane.children.add(KeyBoundButton("[$i] $it", KeyCode.getKeyCode("$i")) {
                if (independentValue != null) {
                    currentDataSet?.dependentColumn = it
                } else {
                    currentDataSet?.independentColumn = it
                }
                updatePage()
            })
        }

        Workspace.instance?.registerFromPane(this)
    }
}
