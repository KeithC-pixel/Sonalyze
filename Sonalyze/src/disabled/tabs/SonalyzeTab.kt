package org.sonalyze.ui.tabs

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.input.KeyCode

/**
 * The SonaylzeTab is an abstract class to hold different tabs in Sonalyze
 */
@Deprecated(
    "Class contains unfinished functionality and is disabled — do not use!",
    DeprecationLevel.ERROR
)
abstract class SonalyzeTab : Tab() {
    private val mappedFunctions: Map<KeyCode, () -> Unit> = mutableMapOf()

    /**
     * Defines the content of the tab — should be ran in SonalyzeApp setup
     */
    abstract fun buildContent()

    /**
     * To get the built content from the assigned companion object in subclass
     */
    abstract fun getBuiltContent(): Node?

    /**
     * Informs the tab that is has been opened
     */
    abstract fun onOpen(firstOpen: Boolean = false)

    /**
     * Maps keys to button functionality
     *
     * @param key The key
     * @param button A button if applicable to bind the function to
     * @param function The function to call on key press
     */
    fun registerMap(key: KeyCode, button: Button? = null, function: () -> Unit) {
        Platform.runLater { button?.setOnAction { function() } }
    }


    /**
     * Informs the tab of a Keyboard input
     *
     * @param key The KeyCode of the pressed key
     */
    fun keyboardInput(key: KeyCode) {
        mappedFunctions[key]?.invoke()
    }
}