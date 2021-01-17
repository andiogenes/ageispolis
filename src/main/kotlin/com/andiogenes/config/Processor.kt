package com.andiogenes.config

import com.andiogenes.effects.AudioEffectConstructor

/**
 * Конфигурация обработчика.
 */
data class Processor(
    /**
     * Имя обработчика.
     */
    val name: String,

    /**
     * Входящие порты.
     */
    val inPorts: Int,

    /**
     * Исходящие порты.
     */
    val outPorts: Int,

    /**
     * Цвет заливки компонента.
     */
    val fillColor: Int,

    /**
     * Параметры обработчика.
     */
    val parameters: List<Pair<String, Float>>,

    /**
     * Эффект обработчика.
     */
    val effect: AudioEffectConstructor,
)