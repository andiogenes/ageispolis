package app.ui.display

/**
 * Отображаемый объект, который может иметь дочерние объекты.
 */
abstract class DisplayObjectContainer(parent: DisplayObject? = null, layer: Int = 0) : DisplayObject(parent, layer) {
    private val children: ArrayList<DisplayObject> = arrayListOf()

    /**
     * Добавляет дочерний объект.
     */
    fun addChild(child: DisplayObject): Boolean {
        child.assignParent(this)
        return children.add(child)
    }

    /**
     * Удаляет дочерний объект.
     */
    fun removeChild(child: DisplayObject): Boolean {
        child.assignParent(null)
        return children.remove(child)
    }

    override fun dispose(): Boolean {
        // Удаляем дочерние из системы обработки;
        // затем очищаем список дочерних объектов,
        // чтобы не было висячих указателей, провоцирующих memory leak
        children.onEach { it.dispose() }.clear()
        return super.dispose()
    }
}