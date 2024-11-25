package io.github.akanksha23056.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Bird extends Actor {
    private final Texture normalTexture;
    private final Texture hoverTexture;
    private final Rectangle bounds;
    private float scale = 0.1f; // Initial scale
    private boolean isClicked = false;

    public Bird(String normalTexturePath, String hoverTexturePath, float initialX, float initialY) {
        this.normalTexture = new Texture(normalTexturePath);
        this.hoverTexture = new Texture(hoverTexturePath);

        // Initialize bounds at the provided position
        this.bounds = new Rectangle(initialX, initialY, normalTexture.getWidth(), normalTexture.getHeight());
        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Determine which texture to use
        Texture currentTexture = isClicked ? hoverTexture : normalTexture;

        // Draw the bird with its current scale
        float width = currentTexture.getWidth() * scale;
        float height = currentTexture.getHeight() * scale;
        batch.draw(currentTexture, bounds.x, bounds.y, width, height);
    }

    @Override
    public void act(float delta) {
        // Check for bird click
        if (Gdx.input.justTouched() && bounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
            isClicked = true;
        }

        // Scale the bird up when clicked
        if (isClicked) {
            scale += 0.008f;

            // Reset or hide when it reaches the maximum scale
            if (scale >= 5.0f) {
                scale = 1.0f;
                isClicked = false;
            }
        }
    }

    public void dispose() {
        normalTexture.dispose();
        hoverTexture.dispose();
    }
}
