package org.sonalyze.audio

/**
 * Dictateable is an interface used to allow audio players to be passed through the AudioDictator
 */
interface Dictateable {
    fun play(callback: (() -> Unit)? = null)

    fun stop()
}