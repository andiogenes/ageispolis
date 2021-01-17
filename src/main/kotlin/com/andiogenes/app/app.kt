package com.andiogenes.app

import com.andiogenes.skija.SkijaWindow
import org.jetbrains.skija.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import com.andiogenes.app.ui.core.CoreScreen
import com.andiogenes.app.ui.display.DisplayObject

/**
 * Запускает приложение.
 *
 * @param title Заголовок окна
 * @param bounds Размеры окна. Выставляется автоматически, если не задан
 * @param root Корневой компонент представления
 */
fun runApp(title: String, root: DisplayObject, bounds: IRect? = null) {
    prepareGLFW()

    val screen = CoreScreen(root)

    SkijaWindow(
        title = title,
        windowSizeCallback = { w, h -> screen.onWindowSize(w, h) },
        cursorPosCallback = { x, y -> screen.onCursor(x, y) },
        mouseButtonCallback = { button, action, mods -> screen.onMouseButton(button, action, mods) },
        keyCallback = { key, scancode, action, mods -> screen.onKey(key, scancode, action, mods) }
    ).run(bounds ?: prepareBounds()) {
        clear(0xFFFFFFFF.toInt())
        screen.onDraw(this)
    }
}

fun prepareGLFW() {
    GLFWErrorCallback.createPrint(System.err).set()
    if (!glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }
}

fun prepareBounds(): IRect {
    val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!

    val width = (vidMode.width() * 0.75).toInt()
    val height = (vidMode.height() * 0.75).toInt()

    return IRect.makeXYWH(
        maxOf(0, (vidMode.width() - width) / 2),
        maxOf(0, (vidMode.height() - height) / 2),
        width,
        height
    )
}