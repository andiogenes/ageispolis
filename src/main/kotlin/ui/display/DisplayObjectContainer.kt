package ui.display

/**
 * Отображаемый объект, который может иметь дочерние объекты.
 */
abstract class DisplayObjectContainer(parent: DisplayObject? = null) : DisplayObject(parent) {
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
}