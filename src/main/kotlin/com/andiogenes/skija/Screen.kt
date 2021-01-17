package com.andiogenes.skija

import org.jetbrains.skija.Canvas

/**
 * Набор обработчиков оконных событий.
 */
interface Screen {
    /**
     * Обработка события отрисовки.
     */
    fun onDraw(canvas: Canvas)

    /**
     * Обработка нажатия кнопки мыши.
     */
    fun onMouseButton(button: Int, action: Int, mods: Int)

    /**
     * Обработка изменения позиции курсора.
     */
    fun onCursor(x: Int, y: Int)

    /**
     * Обработка изменения размера окна.
     */
    fun onWindowSize(width: Int, height: Int)

    /**
     * Обработка нажатия кнопки на клавиатуре.
     */
    fun onKey(key: Int, scancode: Int, action: Int, mods: Int)
}