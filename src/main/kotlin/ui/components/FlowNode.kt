package ui.components

import org.jetbrains.skija.*
import ui.display.DisplayObject

class FlowNode(title: String, width: Int, height: Int, fillColor: Int) : DisplayObject() {
    private val fillShader = Shader.makeLinearGradient(0f, 0f, 0f, 480f, intArrayOf(fillColor, 0xFF000000.toInt()))
    private val fillPaint = Paint().setShader(fillShader).setMode(PaintMode.FILL).setImageFilter(fillShadow)

    private val titleLine = TextLine.make(title, font)
    private val titleLineWidth = titleLine.width
    private val titleLineHeight = titleLine.height

    // TODO: вычислять из агрегируемого узла
    private val inPortsCount = 3
    private val outPortsCount = 1

    private val inPortsHeight = portDiameter * inPortsCount + (inPortsCount - 1) * (portDistance - portDiameter)
    private val outPortsHeight = portDiameter * outPortsCount + (outPortsCount - 1) * (portDistance - portDiameter)

    private val width = maxOf(width, (titleLineWidth * horizontalPaddingScale).toInt())
    private val height = maxOf(
        height,
        (headerHeight * 2).toInt(),
        (inPortsHeight * verticalPaddingScale).toInt(),
        (outPortsHeight * verticalPaddingScale).toInt()
    )

    override fun draw(canvas: Canvas) {
        val absoluteX = absoluteX.toFloat()
        val absoluteY = absoluteY.toFloat()

        val mainRRect = RRect.makeXYWH(
            absoluteX,
            absoluteY,
            width.toFloat(),
            height.toFloat(),
            10f
        )

        val headerRRect = RRect.makeComplexXYWH(
            absoluteX,
            absoluteY,
            width.toFloat(),
            headerHeight,
            floatArrayOf(10f, 10f, 0f, 0f)
        )

        canvas.save()
        canvas.drawRRect(headerRRect, headerPaint)
        canvas.drawRRect(mainRRect, fillPaint)
        canvas.drawLine(absoluteX, absoluteY + headerHeight, absoluteX + width, absoluteY + headerHeight, strokePaint)
        canvas.drawRRect(mainRRect, strokePaint)
        drawPorts(canvas, absoluteX, absoluteY)
        canvas.drawTextLine(titleLine, absoluteX + (width - titleLineWidth) / 2, absoluteY + headerHeight - titleLineHeight / 4, textPaint)
        canvas.restore()
    }

    private val inPortsStartY = (headerHeight + this.height - inPortsHeight) / 2
    private val outPortsStartY = (headerHeight + this.height - outPortsHeight) / 2

    private fun drawPorts(canvas: Canvas, absoluteX: Float, absoluteY: Float) {
        for (i in 0 until inPortsCount) {
            val portY = absoluteY + inPortsStartY + i * portDistance + portRadius
            canvas.drawCircle(absoluteX, portY, portRadius, portPaint)
            canvas.drawCircle(absoluteX, portY, portRadius, portStrokePaint)
        }

        val outPortX = absoluteX + width
        for (i in 0 until outPortsCount) {
            val portY = absoluteY + outPortsStartY + i * portDistance + portRadius
            canvas.drawCircle(outPortX, portY, portRadius, portPaint)
            canvas.drawCircle(outPortX, portY, portRadius, portStrokePaint)
        }
    }

    companion object {
        private val fillShadow = ImageFilter.makeDropShadow(0f, 10f, 5f, 10f, 0xFF000000.toInt())
        private val strokePaint = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(0.8f)
        private val headerPaint = Paint().setBlendMode(BlendMode.CLEAR)

        private val portPaint = Paint().setColor(0xFFDBE11E.toInt()).setMode(PaintMode.FILL)
        private val portStrokePaint = Paint().setColor(0xFF000000.toInt()).setMode(PaintMode.STROKE).setStrokeWidth(1.25f)

        private val typeface = Typeface.makeDefault()
        private val font = Font(typeface, 16f)
        private val textPaint = Paint().setColor(0xFF000000.toInt())

        private const val headerHeight = 24f
        private const val portRadius = 7.5f
        private const val portDiameter = portRadius * 2
        private const val portDistance = 30f

        private const val horizontalPaddingScale = 1.25
        private const val verticalPaddingScale = 1.75
    }
}
