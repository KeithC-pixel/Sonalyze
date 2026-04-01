package org.sonalyze.ui.pages

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.sonalyze.audio.AudioDictator
import org.sonalyze.audio.Dictation
import org.sonalyze.audio.SonaSynth
import org.sonalyze.data.DataSet
import org.sonalyze.data.WorkspaceSave
import org.sonalyze.ui.KeyBoundButton
import org.sonalyze.ui.Workspace
import smile.math.MathEx
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * The AnalysisPage enables to user to use Sonalyze analysis utilities
 */
class AnalysisPage : Page(15.0, {
    // On AnalysisPage open...
    Workspace.instance?.analysisPage?.updateTable()
    AudioDictator.queueDictation("Press 1 to hear data statistics")
    AudioDictator.queueDictation("Press 2 to sonalyze linear regression")
    AudioDictator.queueDictation("Press 3 to sonalyze the graph shape")

    WorkspaceSave.saveRecentAs(Workspace.instance?.analysisPage?.dataset!!)
}) {
    // Data set to  be analyzed
    var dataset: DataSet? = null

    // Table view of the data
    var table: TableView<Map<String, Double>>? = null

    // Extension function of double to reduce decimal places
    fun Double.trimDouble(places: Int = 3): String =
        DecimalFormat("#." + "#".repeat(places)).format(this)

    init {
        // AnalysisPage content...
        alignment = Pos.CENTER
        children.addAll(
            HBox(15.0).apply {
                alignment = Pos.CENTER

                // Left — Options on data
                children.add(VBox(10.0).apply {
                    alignment = Pos.CENTER_LEFT

                    children.addAll(
                        // DATA STATISTICS
                        KeyBoundButton("[1] Data Stats", KeyCode.DIGIT1, {
                            AudioDictator.clearQueue()
                            AudioDictator.queueDictation("Use arrow keys to skip or return to dictation")

                            for (string in listOf(dataset?.dependentColumn, dataset?.independentColumn)) {
                                val col = dataset?.dataFrame?.column(string)?.toDoubleArray()!!

                                // Get data from MathEX
                                val median = MathEx.median(col)
                                val q1 = MathEx.q1(col)
                                val q3 = MathEx.q3(col)
                                val variance = MathEx.`var`(col)

                                val sd = sqrt(variance)

                                // Build dictations
                                listOf(
                                    "$string Statistics",
                                    "Max $string is ${col.max().trimDouble()}",
                                    "Min $string is ${col.min().trimDouble()}",
                                    "Mean $string is ${col.average().trimDouble()}",
                                    "Q1 $string is $q1, ${(q1 * 100/col.max()).trimDouble()} percent through",
                                    "Median $string is $median, ${(median * 100/col.max()).trimDouble()} percent through",
                                    "Q3 $string is $q3, ${(q3 * 100/col.max()).trimDouble()} percent through",
                                    "Standard deviation is $sd"
                                ).forEach { AudioDictator.queueDictation(it) }
                            }
                        }),
                        // LINEAR REGRESSION
                        KeyBoundButton("[2] Linear Regression", KeyCode.DIGIT2, {
                            AudioDictator.clearQueue()

                            // Get SMILE linear model
                            val model = dataset?.getLinearModel()!!

                            // Get correlation
                            val r2 = model.RSquared()
                            val correlation = when (r2) {
                                1.0 -> "A Perfect"
                                in .9..1.0 -> "A Great"
                                in .8..0.9 -> "A Good"
                                in .6..0.8 -> "A Decent"
                                in .3..0.6 -> "A Noticeable"
                                in 0.15..0.3 -> "A Weak"
                                else -> "No Significant"
                            }

                            // Inform User
                            AudioDictator.queueDictation("The graph shows $correlation correlation, with an R square of ${r2.trimDouble()}.")
                            AudioDictator.queueDictation(
                                "Playing linear regression sonalyze graph, with a width of ${
                                    dataset?.getWidth()?.trimDouble()
                                } and height of ${dataset?.getHeight()?.trimDouble()}"
                            )
                            var synth = SonaSynth(4.0)
                            synth.setLinearProgressionFunction(
                                dataset?.getHeight()!!,
                                dataset?.getWidth()!!,
                                model.intercept(),
                                model.coefficients()[0]
                            )
                            AudioDictator.queueDictation(synth)
                        }),
                        KeyBoundButton("[3] Sonalyze Shape", KeyCode.DIGIT3, {
                            AudioDictator.clearQueue()


                            AudioDictator.queueDictation("Playing shape sonalyze graph, high pitch means high density, low pitch means low density")
                            val gradient = IntArray(1000)
                            val dependent = dataset?.dataFrame?.column(dataset?.dependentColumn!!)?.toDoubleArray()!!
                            val width = dataset?.getWidth()!!

                            val spread = 1000/dependent.size

                            dependent.forEach { x ->
                                val coreIndex = (x * 1000 / width).toInt().coerceIn(0, 999)
                                for (i in max(0, coreIndex - 150)..min(999, coreIndex + 150)) {
                                    val add = - (1/100) * x.pow(2) + 15
                                    gradient[i] = min((gradient[i] + add).toInt(), 150)
                                }
                            }

                            var synth = SonaSynth(4.0) { t ->
                                gradient[(t * 1000).toInt().coerceIn(0,999)] / 200.0
                            }
                            AudioDictator.queueDictation(synth)
                        })
                    )
                })

                // Data display
                table = TableView<Map<String, Double>>()
                children.add(table)
            })
    }

            /**
             * Updates the table based on the information in the DataSet
             */
            fun updateTable() {
                table?.items?.clear()
                table?.columns?.clear()

                val independent = dataset?.independentColumn
                val dependent = dataset?.dependentColumn

                // Setup Columns
                table.apply {
                    val columnsToMake = listOf(independent, dependent)
                    columnsToMake.forEach {
                        println(it)
                        val column: TableColumn<Map<String, Double>, Double> = TableColumn(it)
                        column.setCellValueFactory { data -> SimpleObjectProperty(data.value[it]) }
                        table?.columns?.add(column)
                    }
                }

                // Setup Rows
                val data = FXCollections.observableArrayList<Map<String, Double>>()
                val col1 = dataset?.dataFrame?.column(independent)?.toDoubleArray()
                val col2 = dataset?.dataFrame?.column(dependent)?.toDoubleArray()

                for ((x, y) in col1?.zip(col2!!)!!)
                    data.add(mutableMapOf(independent!! to x, dependent!! to y))

                table?.items = data

                AudioDictator.queueDictation("Analysis ready, comparing $independent against $dependent.")
            }
}

