package org.sonalyze.audio

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.math.PI
import kotlin.math.sin

/**
 * SonaSynth is a synth tool that generates audio over a period of time, and allows for the pitch to
 * be changed based on a given progression function. Useful to representing the graphs audibly.
 *
 * @property duration The duration the synth will spawn when played
 * @property step How often to call the progression function
 * @property synthRange The HZ values at which to play the synth between
 * @property progressionFunction Gives a value between 0.0 (start) and 1.0 (end), then expects a value
 * between 0.0 (lowest synth sound) and 1.0 (highest synth sound) in return
 */
class SonaSynth(
    var duration: Double = 5.0,
    var synthRange: IntRange = 230..425,
    var progressionFunction: (Double) -> Double = { 0.0 }
) : Dictateable {
    // The synth audio object that holds all the data to play the synth
    private var activeAudio: SynthAudio? = null

    // To control if it is playing
    private var playThread: Thread? = null

    /**
     * Play the synth aloud
     *
     * @param callback Optional lambda function to run after the synth
     */
    override fun play(callback: (() -> Unit)?) {
        activeAudio = generateAudio()
        val line = activeAudio?.line!!
        val buffer = activeAudio?.buffer!!


        playThread = Thread {
            line.open(activeAudio!!.format)
            line.start()
            line.write(buffer, 0, buffer.size)
            line.drain()
            line.close()
            callback?.invoke()
        }

        playThread?.start()
    }

    /**
     * Stops the synth
     */
    override fun stop() {
        activeAudio = generateAudio()
        playThread?.interrupt()
        val line = activeAudio?.line
        line?.drain()
        line?.close()
        activeAudio = null
        playThread = null
    }

    /**
     * Sets the Synth to match a linear function
     *
     * @param height The height of the function window
     * @param width The width of the function window
     * @param yIntercept The point where the function intercepts the y-axis
     * @param slope The slope of the line
     */
    fun setLinearProgressionFunction(height: Double, width: Double, yIntercept: Double, slope: Double) {
        progressionFunction = { t: Double ->
            (yIntercept + (slope * width) * t) / height
        }
    }

    // Generates the synth audio
    private fun generateAudio(sampleRate: Float = 441000f): SynthAudio {
        val samples = (sampleRate * duration).toInt()
        val byteArray = ByteArray(samples)

        // Generate the samples
        for (i in 0 until samples) {
            val freq = (synthRange.first + (synthRange.last - synthRange.first) * progressionFunction(i/samples.toDouble()))
            val angle = 2 * PI * i * freq / sampleRate

            byteArray[i] = (sin(angle) * Byte.MAX_VALUE).toInt().toByte()
        }

        val format = AudioFormat(sampleRate, 8, 1, true, true)
        val line = AudioSystem.getSourceDataLine(format)

        return SynthAudio(byteArray, line, format)
    }

    // The private data class is used as a wrapper to hold all the information needed to play the synth after its generated
    private data class SynthAudio(val buffer: ByteArray?, val line: SourceDataLine?, val format: AudioFormat)
}