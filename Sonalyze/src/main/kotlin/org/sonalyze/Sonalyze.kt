package org.sonalyze
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.sonalyze.audio.SonaSynth
import org.sonalyze.ui.SonalyzeApp
import org.sonalyze.ui.Workspace

/**
 * Sonalyze.kt is Singleton and acts as the entrypoint into Sonalyze — also containing the entry function main
 */
object Sonalyze {
    val app: SonalyzeApp = SonalyzeApp()
    val logger: Logger = LoggerFactory.getLogger("Sonalyze")

    init {
        app.initialize()
    }
}

// Sonalyze entry-point
fun main() {
    Sonalyze.logger.info("Sonalyze has been started up.")
}