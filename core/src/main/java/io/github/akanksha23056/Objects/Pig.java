package io.github.akanksha23056.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Pig extends Actor {
    private final Texture pigTexture;
    private final List<Vector2> pigPositions;
    private final List<Vector2> pigVelocities;
    private float elapsedTime = 0;

    public Pig() {
        this.pigTexture = new Texture("pig.png");
        this.pigPositions = new ArrayList<>();
        this.pigVelocities = new ArrayList<>();

        // Initialize 20 pigs with random positions and velocities
        for (int i = 0; i < 20; i++) {
            float x = (float) Math.random() * Gdx.graphics.getWidth();
            float y = (float) Math.random() * Gdx.graphics.getHeight();

            pigPositions.add(new Vector2(x, y));

            // Set higher random velocities for each pig to simulate faster movement
            float velocityX = (float) Math.random() * 200 - 100; // Speed from -100 to 100 pixels/second
            float velocityY = -150; // Constant downward velocity for the rain effect
            pigVelocities.add(new Vector2(velocityX, velocityY));
        }
    }

    @Override
    public void act(float delta) {
        elapsedTime += delta;
        // Total display duration in seconds
        float duration = 2.0f;
        if (elapsedTime >= duration) {
            // Remove pigs after 3 seconds
            remove();
        } else {
            // Update each pig's position based on its velocity
            for (int i = 0; i < pigPositions.size(); i++) {
                Vector2 position = pigPositions.get(i);
                Vector2 velocity = pigVelocities.get(i);
                position.x += velocity.x * delta; // Move horizontally
                position.y += velocity.y * delta; // Move vertically (falling)

                // Reset position if pig moves off screen (to wrap around)
                if (position.x < 0) position.x = Gdx.graphics.getWidth();
                if (position.x > Gdx.graphics.getWidth()) position.x = 0;
                if (position.y < 0) position.y = Gdx.graphics.getHeight();
                if (position.y > Gdx.graphics.getHeight()) position.y = 0; // Wrap vertically
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float scaleFactor = 0.5f; // Scale factor to reduce size

        for (Vector2 position : pigPositions) {
            batch.draw(pigTexture, position.x - (float) pigTexture.getWidth() / 2 * scaleFactor,
                position.y - (float) pigTexture.getHeight() / 2 * scaleFactor,
                pigTexture.getWidth() * scaleFactor, // Scaled width
                pigTexture.getHeight() * scaleFactor); // Scaled height
        }
    }

    public void dispose() {
        pigTexture.dispose();
    }
}
