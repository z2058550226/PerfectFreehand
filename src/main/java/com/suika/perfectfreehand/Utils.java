package com.suika.perfectfreehand;

import static java.lang.Math.*;

interface Utils {
    double PI2 = PI * 2;

    static double lerp(double y1, double y2, double mu) {
        return y1 * (1 - mu) + y2 * mu;
    }

    static double[] projectPoint(double[] point0, double angle, double distance) {
        return new double[]{cos(angle) * distance + point0[0], sin(angle) * distance + point0[1]};
    }

    static double shortAngleDist(double angle0, double angle1) {
        final double max = PI2;
        final double da = (angle1 - angle0) % max;
        return ((2 * da) % max) - da;
    }

    static double getAngleDelta(double a0, double a1){
        return shortAngleDist(a0, a1);
    }

//
//    export function lerpAngles(a0: number, a1: number, t: number) {
//        return a0 + shortAngleDist(a0, a1) * t
//    }

    static double lerpAngles(float a0, float a1, double t){
        return a0 + shortAngleDist(a0, a1) * t;
    }

//    export function getPointBetween(p0: number[], p1: number[], d = 0.5) {
//        return [p0[0] + (p1[0] - p0[0]) * d, p0[1] + (p1[1] - p0[1]) * d]
//    }


//
//    export function getAngle(p0: number[], p1: number[]) {
//        return atan2(p1[1] - p0[1], p1[0] - p0[0])
//    }
//
//    export function getDistance(p0: number[], p1: number[]) {
//        return hypot(p1[1] - p0[1], p1[0] - p0[0])
//    }
//
//    export function clamp(n: number, a: number, b: number) {
//        return max(a, min(b, n))
//    }

}
