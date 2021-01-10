package skija

import org.jetbrains.skija.*
import org.jetbrains.skija.impl.*

import org.lwjgl.glfw.*
import org.lwjgl.opengl.*

import org.lwjgl.system.MemoryUtil

/**
 * Окно, в котором можно рисовать, используя Skia API и принимать события GLFW.
 */
class SkijaWindow(
    /**
     * Заголовок окна.
     */
    private val title: String,

    /**
     * Вертикальная синхронизация.
     */
    private val vsync: Boolean = true,

    /**
     * Обратный вызов изменения размера окна.
     */
    private val windowSizeCallback: ((Int, Int) -> Unit)? = null,

    /**
     * Обратный вызов изменения позиции курсора.
     */
    private val cursorPosCallback: ((Int, Int) -> Unit)? = null,

    /**
     * Обратный вызов нажатия на кнопку мыши.
     */
    private val mouseButtonCallback: ((Int, Int, Int) -> Unit)? = null,

    /**
     * Обратный вызов нажатия на кнопку клавиатуры.
     */
    private val keyCallback: ((Int, Int, Int, Int) -> Unit)? = null,
) {
    private var window: Long = 0

    private var width: Int = 0
    private var height: Int = 0

    private var dpi: Float = 1f

    private var xPos: Int = 0
    private var yPos: Int = 0

    private val os = System.getProperty("os.name").toLowerCase()

    private lateinit var concreteDraw: (Canvas) -> Unit

    /**
     * Инициализация окна и запуск цикла событий.
     */
    fun run(bounds: IRect, draw: Canvas.() -> Unit) {
        this.concreteDraw = draw

        createWindow(bounds)
        loop()

        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    private fun updateDimensions() {
        val width = IntArray(1)
        val height = IntArray(1)
        GLFW.glfwGetFramebufferSize(window, width, height)

        val xScale = FloatArray(1)
        val yScale = FloatArray(1)
        GLFW.glfwGetWindowContentScale(window, xScale, yScale)
        assert(xScale[0] == yScale[0])

        this.width = (width[0] / xScale[0]).toInt()
        this.height = (height[0] / yScale[0]).toInt()
        this.dpi = xScale[0]
    }

    private fun createWindow(bounds: IRect) {
        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable

        window = GLFW.glfwCreateWindow(bounds.width, bounds.height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }


        GLFW.glfwSetKeyCallback(window) { window: Long, key: Int, _: Int, action: Int, _: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true)
            }
        }

        GLFW.glfwSetWindowPos(window, bounds.left, bounds.top)
        updateDimensions()
        xPos = width / 2
        yPos = height / 2

        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwSwapInterval(if (vsync) 1 else 0) // Enable v-sync
        GLFW.glfwShowWindow(window)
    }

    private var context: DirectContext? = null
    private var renderTarget: BackendRenderTarget? = null
    private var surface: Surface? = null
    private var canvas: Canvas? = null

    private fun initSkia() {
        canvas = null

        if (surface != null) {
            surface!!.close()
            surface = null
        }

        if (renderTarget != null) {
            renderTarget!!.close()
            renderTarget = null
        }

        val fbId = GL11.glGetInteger(0x8CA6) // GL_FRAMEBUFFER_BINDING
        renderTarget = BackendRenderTarget.makeGL(
            (width * dpi).toInt(),
            (height * dpi).toInt(),
            0, /*samples*/
            8, /*stencil*/
            fbId,
            FramebufferFormat.GR_GL_RGBA8
        )

        surface = Surface.makeFromBackendRenderTarget(
            context,
            renderTarget,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.getDisplayP3()
        )

        canvas = surface!!.canvas
        canvas!!.scale(dpi, dpi)
    }

    private fun draw() {
        concreteDraw(canvas!!)
        context!!.flush()
        GLFW.glfwSwapBuffers(window)
    }

    private fun loop() {
        GL.createCapabilities()
        if ("false" == System.getProperty("skija.staticLoad")) Library.load()
        context = DirectContext.makeGL()

        GLFW.glfwSetWindowSizeCallback(window) { _: Long, _: Int, _: Int ->
            updateDimensions()
            initSkia()
            draw()

            windowSizeCallback?.invoke(this.width, this.height)
        }

        GLFW.glfwSetCursorPosCallback(window) { _: Long, xPos: Double, yPos: Double ->
            if (os.contains("mac") || os.contains("darwin")) {
                this.xPos = xPos.toInt()
                this.yPos = yPos.toInt()
            } else {
                this.xPos = (xPos / dpi).toInt()
                this.yPos = (yPos / dpi).toInt()
            }

            cursorPosCallback?.invoke(this.xPos, this.yPos)
        }

        GLFW.glfwSetMouseButtonCallback(window) { _: Long, button: Int, action: Int, mods: Int ->
            mouseButtonCallback?.invoke(button, action, mods)
        }

        GLFW.glfwSetKeyCallback(window) { _: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            keyCallback?.invoke(key, scancode, action, mods)
        }

        initSkia()

        while (!GLFW.glfwWindowShouldClose(window)) {
            draw()
            GLFW.glfwPollEvents()
        }
    }
}