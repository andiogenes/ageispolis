package effects

import be.tarsos.dsp.AudioProcessor

/**
 * Математически-алгоритмическое описание звукового эффекта.
 *
 * @param params Внешние регулируемые параметры эффекта
 */
abstract class AudioEffect(val params: FloatArray) : AudioProcessor

/**
 * Правило, по которому создается звуковой эффект.
 */
typealias AudioEffectConstructor = (FloatArray) -> AudioEffect