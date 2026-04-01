package org.sonalyze.ui

import javafx.scene.control.Button
import javafx.scene.input.KeyCode

/**
 * KeyBoundButton allows the Workspace to bind a key to a button to support sightless navigation
 */
class KeyBoundButton(name: String, val key: KeyCode, val function: () -> Unit) : Button(name) {
    init {
        setOnAction { function() }
    }
}
