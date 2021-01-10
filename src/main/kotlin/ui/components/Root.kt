package ui.components

import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint
import ui.display.DisplayObjectContainer

class Root : DisplayObjectContainer() {
    init {
        addChild(FlowNode("Hello, world!", 140, 164, 0x66AAAAAA).apply {
            x = 10
            y = 10
        })
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.clear(backgroundColor)
        // TODO: строить сетку по размеру окна
        for (x in 1..320) {
            canvas.drawLine(x.toFloat() * gridWidth, 0f, x.toFloat() * gridWidth, 1024f, gridPaint)
        }
        for (y in 1..120) {
            canvas.drawLine(0f, y.toFloat() * gridWidth,  1924f, y.toFloat() * gridWidth, gridPaint)
        }
        canvas.restore()
    }

    companion object {
        private val gridPaint = Paint().setColor(0xFF000000.toInt()).setStrokeWidth(1f).setAntiAlias(false)
        private const val backgroundColor = 0xFF393939.toInt()
        private const val gridWidth = 20
    }
}