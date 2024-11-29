package io.github.akanksha23056.Objects;

import java.io.Serializable;

public class Vector2 implements Serializable {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2{" +
            "x=" + x +
            ", y=" + y +
            '}';
    }
}
