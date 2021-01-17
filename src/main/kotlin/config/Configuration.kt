package config

import effects.*

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

            effect {
                InputEffect(it)
            }
        }

        processor("Delay") {
            inPorts = 1
            outPorts = 1
            fillColor = 0x66AA1E38

            parameters {
                "Time" to 0.1f
                "Decay" to 0.5f
            }

            effect {
                DelayEffect(it)
            }
        }

        processor("Flanger") {
            inPorts = 1
            outPorts = 1
            fillColor = 0x66B25BA2

            parameters {
                "Length" to 0.1f
                "Wet" to 0.5f
                "Frequency" to 0.2f
            }

            effect {
                FlangerEffect(it)
            }
        }

        processor("Distortion") {
            inPorts = 1
            outPorts = 1
            fillColor = 0x66666666

            parameters {
                "Volume" to 0.5f
                "Gain" to 0.5f
            }

            effect {
                DistortionEffect(it)
            }
        }

        processor("Output") {
            inPorts = 1
            outPorts = 0

            parameters {
                "Volume" to 0.1f
            }

            effect {
                OutputEffect(it)
            }
        }
    }
}