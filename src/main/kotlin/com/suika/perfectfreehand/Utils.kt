@file:Suppress("SpellCheckingInspection")

package com.suika.perfectfreehand

import com.suika.perfectfreehand.data.Point
import com.suika.perfectfreehand.data.PressurePoint
import kotlin.math.*

var PI2 = Math.PI * 2

fun lerp(y1: Double, y2: Double, mu: Double): Double {
    return y1 * (1 - mu) + y2 * mu
}

fun projectPoint(x: Double, y: Double, angle: Double, distance: Double): Point {
    return Point(cos(angle) * distance + x, sin(angle) * distance + y)
}

fun shortAngleDist(angle0: Double, angle1: Double): Double {
    val max = PI2
    val da = (angle1 - angle0) % max
    return 2 * da % max - da
}

fun getAngleDelta(a0: Double, a1: Double): Double {
    return shortAngleDist(a0, a1)
}

fun lerpAngles(a0: Double, a1: Double, t: Double): Double {
    return a0 + shortAngleDist(a0, a1) * t
}

fun getPointBetween(x0: Double,y0: Double,x1: Double,y1: Double, d: Double = 0.5): Point {
    return Point(lerp(x0, x1, d), lerp(y0, y1, d))
}

fun getAngle(x0: Double, y0: Double, x1: Double, y1: Double): Double {
    return atan2(y1 - y0, x1 - x0)
}

fun getDistance(x0: Double, y0: Double, x1: Double, y1: Double): Double {
    return hypot(y1 - y0, x1 - x0)
}

fun clamp(n: Double, a: Double, b: Double): Double {
    return max(a, min(b, n))
}

/*
export function toPointsArray<
T extends number[],
K extends { x: number; y: number; pressure?: number }
>(points: (T | K)[]): number[][] {
    if (Array.isArray(points[0])) {
        return (points as number[][]).map(([x, y, pressure = 0.5]) => [
        x,
        y,
        pressure,
        ])
    } else {
        return (points as {
            x: number
            y: number
            pressure?: number
        }[]).map(({ x, y, pressure = 0.5 }) => [x, y, pressure])
    }
}
*/

fun toPointsArray(points: List<PressurePoint>): List<List<Double>> {
    return points.map { listOf(it.x, it.y, it.pressure) }
}

infix fun Double.default(default: Double): Double = if (this == 0.0) default else this