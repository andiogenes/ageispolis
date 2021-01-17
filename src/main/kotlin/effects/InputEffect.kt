package effects

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.GainProcessor

/**
 * Эффект, который обрабатывает входной звуковой сигнал.
 */
class InputEffect(params: FloatArray) : AudioEffect(params) {
    private var gain = params[0]

    private val gainProcessor = GainProcessor(gain * 2.0)

    override fun process(p0: AudioEvent?): Boolean {
        if (gain != params[0]) {
            gain = params[0]
            gainProcessor.setGain(gain * 2.0)
        }
        gainProcessor.process(p0)
        return true
    }

    override fun processingFinished() {
        gainProcessor.processingFinished()
    }

}