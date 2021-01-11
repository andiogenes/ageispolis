package utils

/**
 * Проверяет, находится ли точка (x0, y0) внутри прямоугольника (left, top, width, height)
 */
fun pointInBox(x0: Int, y0: Int, left: Int, top: Int, width: Int, height: Int): Boolean {
    return x0 >= left && y0 >= top && x0 < left + width && y0 < top + height
}