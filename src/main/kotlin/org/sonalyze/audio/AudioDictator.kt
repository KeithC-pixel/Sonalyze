package org.sonalyze.audio

/**
 * AudioDictator is a singleton that manages the queued audio
 */
object AudioDictator {
    // Controls how far out the AudioDictator will store old dictations
    const val MAX_DISPOSED_DICTATIONS = 100

    // The locale controls the audio file path; to allow different languages
    var locale: String = "en"

    // Queue to manage the dictations that are cued to play
    private var queue: MutableList<Dictateable> = mutableListOf()
    // Dictations that were previously played
    private var disposedDictations: MutableList<Dictateable> = mutableListOf()

    // Stores the dictation that is currently being played
    var currentDictation: Dictateable? = null

    // Plays the current dictation
    private fun playCurrent() {
        currentDictation?.stop()

        if (queue.isEmpty()) {
            queueDictation("notices/no_previous_message.mp3", 0)
        }

        currentDictation?.play {
            currentDictation?.let { dictationFinished(it) }
        }
    }

    // Continues the queue — connected to the callback function in currentDictation#play
    private fun dictationFinished(dictation: Dictateable) {
        if (dictation == currentDictation) {
            currentDictation?.let {
                disposeDictation(it)
            }

            if (queue.isNotEmpty()) {
                currentDictation = queue[0]
                playCurrent()
            }
        }
    }

    /**
     * Goes back to the previous dictation
     */
    fun back() {
        currentDictation?.stop()

        val prevDict = disposedDictations.lastOrNull()
        if (prevDict == null) {
            queueDictation("/notices/no_previous_message.mp3", 0)
            playCurrent()
            return
        }

        queueDictation(disposedDictations.last(), 0)
        disposedDictations.removeLast()
        playCurrent()
    }

    /**
     * Skips the current dictation
     */
    fun skip() {
        currentDictation?.stop()
        currentDictation?.let {
            it.stop()
            disposeDictation(it)
        }
    }

    /**
     * Queues a dictation
     *
     * @param item The dictation to be queued
     * @param pos The index in the queue to be added at
     */
    fun queueDictation(item: Dictateable, pos: Int? = null) {
        Thread {
            if (pos == null) queue.add(item)
            else queue.add(pos, item)

            if (pos == 0 || queue.size == 1) {
                currentDictation = item
                playCurrent()
            }
        }.start()
    }

    /**
     * Queues a dictation
     *
     * @param item The string to be queued as a dictation
     * @param pos The index in hte queue to be added at
     */
    fun queueDictation(item: String, pos: Int? = null) {
        queueDictation(Dictation(item), pos)
    }

    /**
     * Removes every item in the queue and disposes the current dictation
     */
    fun clearQueue() {
        queue.clear()

        currentDictation?.let {
            it.stop()
            disposeDictation(it)
        }
    }

    /**
     * Removes a given dictation from the queue and places it with disposed dictations
     *
     * @param dictation The dictation to dispose
     */
    fun disposeDictation(dictation: Dictateable) {
        dictation.stop()

        if (!queue.contains(dictation)) return

        while (disposedDictations.size >= MAX_DISPOSED_DICTATIONS) {
            disposedDictations.removeFirst()
        }

        disposedDictations.add(dictation)
        queue.remove(dictation)
    }
}