package org.sonalyze.audio

import org.sonalyze.Sonalyze
import java.io.IOException
import java.util.concurrent.CountDownLatch
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.UnsupportedAudioFileException

/**
 * Dictation is a class that can be fed to AudioDictator to provide an audio series
 *
 * @property messages A list of messages to be said, if the message starts with '/' it will reference an audio file
 */
open class Dictation(vararg var messages: String) : Dictateable {
    // Overall messages
    private val messageSeries: MutableList<String> = mutableListOf()
    // Read audio input streams to be played
    private val messageAudioStreams: MutableMap<String, AudioInputStream?> = mutableMapOf()

    // The audio process — if the current audio is played through a process
    private var audioProcess: Process? = null
    // The audio clip — if the current audio is played through a clip
    private var audioClip: Clip? = null


    // Is the dictation currently being said
    private var active: Boolean = false

    init {
        // Register the messages
        messages.forEach {
            messageSeries.add(it)

            // When an audio file starts with '/' the program assumes it's a file path
            if (it.startsWith("/", true)) {
                createAudioStream(it.removePrefix("/"))
            }
        }
    }

    // Creates audio streams for the audio paths
    private fun createAudioStream(path: String) {
        try {
            val audioURL = object {}.javaClass.getResource("/audio/${AudioDictator.locale}/${path}")
            val stream  = AudioSystem.getAudioInputStream(audioURL)

            if (path.endsWith(".mp3", true)) {
                // MP3 decode format
                val baseFormat = stream.format
                val customFormat = AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
                )
                messageAudioStreams[path] = AudioSystem.getAudioInputStream(customFormat, stream)
            } else {
                // WAV format is supported by kotlin natively
                messageAudioStreams[path] = (stream)
            }
        } catch (e: UnsupportedAudioFileException) {
            Sonalyze.logger.warn("The file type for [/audio/${AudioDictator.locale}/${path}] is unsupported.", e)
        } catch (e: NullPointerException) {
            Sonalyze.logger.warn("The file [/audio/${AudioDictator.locale}/${path}] was not found.", e)
        } catch (e: IOException) {
            Sonalyze.logger.warn("An exception occurred when attempting to get resource [/audio/${AudioDictator.locale}/${path}], invalid permissions?", e)
        }
    }

    /**
     * Play the dictation aloud
     *
     * @param callback Optional lambda function to run after the dictation is completed successfully
     */
    override fun play(callback: (() -> Unit)?) {
        // Run through audioSeries
        active = true

        // Create a thread to run audio
        Thread {
            for (audio in messages) {
                if (!active) break

                // Latch to hold thread until audio finished
                val audioLatch = CountDownLatch(1)

                // Open Audio
                try {
                    audioClip = null
                    audioProcess = null

                    if (messageAudioStreams[audio.removePrefix("/")] != null) {
                        audioClip = AudioSystem.getClip().apply {
                            open(messageAudioStreams[audio.removePrefix("/")])
                            start()
                        }
                        audioClip?.addLineListener {
                            if (it.type == LineEvent.Type.STOP && active) {
                                audioLatch.countDown()
                            }
                        }
                    } else {
                        audioProcess = ProcessBuilder("say", "-v", "Samantha", audio).start()
                        audioProcess?.waitFor()
                        audioLatch.countDown()
                    }
                } catch (e: LineUnavailableException) {
                    Sonalyze.logger.warn(
                        "Attempted to play invalid dictation — continuing to next dictation segment.",
                        e
                    )
                    continue
                }

                // Await the completion of the audio
                audioLatch.await()
            }

            // Invoke completed function if it exists
            callback?.invoke()
        }.start()
    }

    /**
     * Cancel the dictation
     */
    override fun stop() {
        active = false

        audioProcess?.destroyForcibly()
        ProcessBuilder(
            "osascript",
            "-e",
            "tell application \"SpeechSynthesisServer\" to stop speaking"
        ).start()

        audioClip?.stop()
    }
}