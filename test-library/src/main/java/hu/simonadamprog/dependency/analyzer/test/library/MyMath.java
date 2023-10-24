package hu.simonadamprog.dependency.analyzer.test.library;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class MyMath {
    public static String calculate() {
        Vector3D vector = new Vector3D(1.0, 1.0, 1.0);
        Vector3D vector2 = new Vector3D(2.0, 2.0, 2.0);
        return "" + vector.distance(vector2);
    }
}