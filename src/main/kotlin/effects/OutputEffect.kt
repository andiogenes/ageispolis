package effects

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.GainProcessor
import be.tarsos.dsp.io.jvm.AudioPlayer
import logic.AudioSystem
import javax.sound.sampled.AudioFormat

/**
 * Эффект, который выводит звук в стандартный звуковой выход.
 */
class OutputEffect(params: FloatArray) : AudioEffect(params) {
    private var gain = params[0]

    private val gainProcessor = GainProcessor(gain * 10.0)
    private val player = AudioPlayer(AudioFormat(AudioSystem.sampleRate.toFloat(), 16, 1, true, true))

    override fun process(p0: AudioEvent?): Boolean {
        if (gain != params[0]) {
            gain = params[0]
            gainProcessor.setGain(gain * 10.0)
        }
        gainProcessor.process(p0)
        player.process(p0)
        return true
    }

    override fun processingFinished() {
        gainProcessor.processingFinished()
        player.processingFinished()
    }
}