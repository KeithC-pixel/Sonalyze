package org.sonalyze.ui.pages

import javafx.scene.layout.VBox

/**
 * The Page class template allows different pages to be put into the workspace, and inherits a VBox
 * children and objects should be added on init to stay in sync.
 *
 * @param spacing The VBox spacing
 * @property onOpen A lambda to be called when the page is opened
 */
abstract class Page(spacing: Double, val onOpen: () -> Unit) : VBox(spacing) {}