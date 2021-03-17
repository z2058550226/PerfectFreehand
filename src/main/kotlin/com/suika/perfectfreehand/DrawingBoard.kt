package com.suika.perfectfreehand

import com.suika.perfectfreehand.data.PressurePoint
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.Path2D
import javax.swing.JPanel


class DrawingBoard : JPanel(), MouseMotionListener, MouseListener {
    private val strokePath: Path2D.Double = Path2D.Double()
    private val mousePath: Path2D.Double = Path2D.Double()
    private val pointList = mutableListOf<PressurePoint>()

    init {
        addMouseMotionListener(this)
        addMouseListener(this)
    }

    override fun paint(g: Graphics) {
        super.paint(g)

        val g2 = g as Graphics2D
        g2.color = Color.BLUE
        g2.fill(strokePath)
//        g2.draw(strokePath)

        g2.color = Color.GREEN

        g2.draw(mousePath)
    }

    override fun mousePressed(e: MouseEvent) {
        mousePath.reset()
        mousePath.moveTo(e.x.toDouble(), e.y.toDouble())
        pointList.clear()
        pointList.add(PressurePoint(e.x.toDouble(), e.y.toDouble()))
        repaint()
    }

    override fun mouseDragged(e: MouseEvent) {
        pointList.add(PressurePoint(e.x.toDouble(), e.y.toDouble()))
        mousePath.lineTo(e.x.toDouble(), e.y.toDouble())
        getSvgPathFromStroke()
        repaint()
    }

    override fun mouseReleased(e: MouseEvent) {
        mousePath.lineTo(e.x.toDouble(), e.y.toDouble())
//        mousePath.closePath()
        pointList.add(PressurePoint(e.x.toDouble(), e.y.toDouble()))
        getSvgPathFromStroke()
        repaint()
    }

    private fun getSvgPathFromStroke() {
//        println("\npointList: $pointList\n")
        val stroke = getStroke(
            pointList, StrokeOptions(
                size = 16.0,
                thinning = 0.75,
                smoothing = 0.5,
                streamline = 0.5,
                easing = { it }
            )
        )
//        println("\nstoke: $stroke\n")
        if (stroke.isEmpty()) return
        strokePath.reset()
        var prevPoint = stroke[0]

        strokePath.moveTo(prevPoint.x, prevPoint.y)
        for (i in 1 until stroke.size) {
            val currPoint = stroke[i]
//            strokePath.quadTo(prevPoint.x, prevPoint.y, prevPoint.x mid currPoint.x, prevPoint.y mid currPoint.y)

            strokePath.lineTo(currPoint.x, currPoint.y)
            prevPoint = currPoint
        }

        //strokePath.closePath()
    }

    private infix fun Double.mid(num: Double): Double = (this + num) / 2

    override fun mouseClicked(e: MouseEvent) = Unit
    override fun mouseMoved(e: MouseEvent) = Unit
    override fun mouseEntered(e: MouseEvent) = Unit
    override fun mouseExited(e: MouseEvent) = Unit
}