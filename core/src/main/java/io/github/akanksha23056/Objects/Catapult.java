package io.github.akanksha23056.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Catapult extends Actor {
    private final Texture texture;

    public Catapult(String texturePath, float x, float y) {
        this.texture = new Texture(texturePath);
        setBounds(x, y, 150, 150); // Adjust size as needed
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        texture.dispose();
    }
}
