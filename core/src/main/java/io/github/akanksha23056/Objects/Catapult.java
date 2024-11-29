package io.github.akanksha23056.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.io.Serializable;

public class Catapult extends Actor implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient final Texture texture;

    public Catapult(String texturePath, float x, float y) {
        this.texture = new Texture(texturePath);
        setBounds(x, y, 170, 170); // Adjust size as needed
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        texture.dispose();
    }
}
