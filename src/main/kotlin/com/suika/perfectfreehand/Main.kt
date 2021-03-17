package com.suika.perfectfreehand

import javax.swing.JFrame

const val SCREEN_HEIGHT = 900
const val SCREEN_WIDTH = SCREEN_HEIGHT

fun main() {
    JFrame().apply {
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT) //400 width and 500 height
        layout = null //using no layout managers
        isVisible = true //making the frame visible

        val panel = DrawingBoard()
        add(panel)
        panel.setSize(SCREEN_WIDTH, SCREEN_HEIGHT)
    }//creating instance of JFrame

}