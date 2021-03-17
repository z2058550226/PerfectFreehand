package com.suika.perfectfreehand

import com.suika.perfectfreehand.data.Point
import com.suika.perfectfreehand.data.PressurePoint
import com.suika.perfectfreehand.data.StreamlinedPoint
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min

const val TAU = PI / 2 // 90°
const val SHARP = TAU // 90°
const val DULL = SHARP / 2 // 45°

fun getStroke(points: List<PressurePoint>, options: StrokeOptions = StrokeOptions()): List<Point> {
    return getStrokeOutlinePoints(getStrokePoints(points), options)
}

fun getStrokePoints(points: List<PressurePoint>, streamline: Double = 0.5): List<StreamlinedPoint> {
    val result = mutableListOf<StreamlinedPoint>()

    if (points.isEmpty()) return result

    val p0 = points[0]
    result += StreamlinedPoint(p0.x, p0.y, p0.pressure default .5, .0, .0, .0)

    for (i in 1 until points.size) {
        val curr: PressurePoint = points[i]
        val prev: StreamlinedPoint = result[i - 1]

        val x = lerp(prev.x, curr.x, 1 - streamline)
        val y = lerp(prev.y, curr.y, 1 - streamline)
        val pressure = curr.pressure
        val angle = getAngle(x, y, prev.x, prev.y)
        val distance = getDistance(x, y, prev.x, prev.y)
        val lengthAtPoint = prev.lengthAtPoint + distance

        result += StreamlinedPoint(x, y, pressure, angle, distance, lengthAtPoint)
    }

    return result
}

fun getStrokeOutlinePoints(points: List<StreamlinedPoint>, options: StrokeOptions = StrokeOptions()): List<Point> {
    if (points.isEmpty()) return emptyList()
    val len = points.size
    val totalLength = points[len - 1].lengthAtPoint
    val minDist = options.size * options.smoothing
    val leftPts = mutableListOf<Point>()
    val rightPts = mutableListOf<Point>()

    var pl = points[0] // Previous left and right points
    var pr = points[0]
    var tl = Point(pl.x, pl.y) // Points to test distance from
    var tr = Point(pr.x, pr.y)
    var pa = pr.angle
    var pp = .0 // Previous (maybe simulated) pressure
    var r = options.size / 2 // The current point radius
    var short = true // Whether the line is drawn far enough

    // If the point is only one point long, draw two caps at either end.
    if (len == 1 || totalLength <= 4) {
        val first = points.first()
        val last = points.last()
        val angle = getAngle(first.x, first.y, last.x, last.y)

        if (options.thinning != 0.0) {
            r = getStrokeRadius(options.size, options.thinning, options.easing, last.pressure)
        }

        var t = .0
        val step = .1
        while (t <= 1.0) {
            tl = projectPoint(first.x, first.y, angle + PI + TAU - t * PI, r)
            tr = projectPoint(last.x, last.y, angle + PI - t * PI, r)
            leftPts += tl
            rightPts += tr

            t += step
        }

        return leftPts + rightPts
    }

    // For a point with more than one point, create an outline shape.
    for (i in 1 until len) {
        val prev = points[i - 1]

        var (x, y, pressure, angle, distance, clen) = points[i]

        // 1.
        // Calculate the size of the current point
        if (options.thinning != 0.0) {
            if (options.simulatePressure) {
                // Simulate pressure by accellerating the reported pressure.
                val rp = min(1 - distance / options.size, 1.0)
                val sp = min(distance / options.size, 1.0)
                pressure = min(1.0, pp + (rp - pp) * (sp / 2))
            }

            // Compute the stroke radius based on the pressure, easing and thinning.
            r = getStrokeRadius(options.size, options.thinning, options.easing, pressure)
        }

        // 2.
        // Draw a cap once we've reached the minimum length.
        if (short) {
            if (clen < options.size / 4) continue

            // The first point after we've reached the minimum length.
            // Draw a cap at the first point angled toward the current point.

            short = false

            var t = .0
            val step = .1
            while (t <= 1.0) {
                println("point$i, angle: ${angle.toDegree()}, projectAngle: ${(angle + TAU - t * PI).toDegree()}")
                tl = projectPoint(points[0].x, points[0].y, angle + TAU - t * PI, r)
                leftPts += tl

                t += step
            }

            tr = projectPoint(points[0].x, points[0].y, angle + TAU, r)
            rightPts += tr
        }

        angle = lerpAngles(pa, angle, 0.75)

        // 3.
        // Add points for the current point.
        if (i == len - 1) {
            var t = .0
            val step = .1
            while (t <= 1.0) {
                rightPts += projectPoint(x, y, angle + TAU + t * PI, r)

                t += step
            }
        } else {
            // Find the delta between the current and previous angle.
            val delta = getAngleDelta(prev.angle, angle)
            val absDelta = abs(delta)

            if (absDelta > SHARP && clen > r) {
                // A sharp corner.
                // Project points (left and right) for a cap.
                val mid = getPointBetween(prev.x, prev.y, x, y)

                var t = .0
                val step = .25

                while (t <= 1) {
                    tl = projectPoint(mid.x, mid.y, pa - TAU - t * PI, r)
                    tr = projectPoint(mid.x, mid.y, pa + TAU + t * PI, r)

                    leftPts += tl
                    rightPts += tr

                    t += step
                }
            } else {
                // A regular point.
                // Add projected points left and right, if far enough away.
                val (xpl, ypl) = projectPoint(x, y, angle - TAU, r)
                pl = StreamlinedPoint(xpl, ypl, pl.pressure, pl.angle, pl.distance, pl.lengthAtPoint)
                val (xpr, ypr) = projectPoint(x, y, angle + TAU, r)
                pr = StreamlinedPoint(xpr, ypr, pr.pressure, pr.angle, pr.distance, pr.lengthAtPoint)

                if (absDelta > DULL || getDistance(pl.x, pl.y, tl.x, tl.y) > minDist) {
                    leftPts += getPointBetween(tl.x, tl.y, pl.x, pl.y)
                    tl = Point(pl.x, pl.y)
                }

                if (absDelta > DULL || getDistance(pr.x, pr.y, tr.x, tr.y) > minDist) {
                    rightPts += getPointBetween(tr.x, tr.y, pr.x, pr.y)
                    tr = Point(pr.x, pr.y)
                }
            }

            pp = pressure
            pa = angle
        }
    }

    rightPts.reverse()
    return leftPts + rightPts
}

fun getStrokeRadius(
    size: Double,
    thinning: Double?,
    easing: (Double) -> Double,
    pressure: Double = 0.5,
): Double {
    if (thinning == null) return size / 2
    val pressure2 = clamp(easing(pressure), 0.0, 1.0)
    return if (thinning < 0) {
        lerp(size, size + size * clamp(thinning, -0.95, -0.05), pressure2)
    } else {
        lerp(size - size * clamp(thinning, 0.05, 0.95), size, pressure2)
    } / 2
}

fun Double.toDegree(): Double {
    return 180 * this / PI
}
