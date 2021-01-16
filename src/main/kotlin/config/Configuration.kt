package config

import effects.Player

/**
 * Объект, содержащий конфигурацию программы.
 */
object Configuration {
    /**
     * Стандартные поддерживаемые обработчики.
     */
    val processors = Dsl.processors {
        processor("Input") {
            inPorts = 0
            outPorts = 1

            parameters {
                "Volume" to 0.5f
            }
        }

        processor("Distortion") {
            inPorts = 1
            outPorts = 1
            fillColor = 0x66FF0000

            parameters {
                "Volume" to 0.5f
                "Tone" to 0.5f
                "Gain" to 0.5f
            }
        }

        processor("Output") {
            inPorts = 1
            outPorts = 0

            parameters {
                "Volume" to 0.1f
            }

            effect {
                Player(it)
            }
        }
    }
}