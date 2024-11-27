package io.github.akanksha23056.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;

public class Bird extends Actor {
    private final Texture birdTexture;
    private final Body birdBody;
    private boolean isDragging = false;
    private final Vector2 initialPosition; // Initial position of the bird
    private boolean launched = false;     // Tracks if the bird has been launched

    public Bird(World world, String texturePath, float x, float y) {
        this.birdTexture = new Texture(texturePath);

        // Save the initial position
        this.initialPosition = new Vector2(x / 100f, y / 100f);

        // Define the bird body in the Box2D world
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(initialPosition);
        birdBody = world.createBody(bodyDef);

        // Define the bird shape
        CircleShape circle = new CircleShape();
        circle.setRadius(0.5f); // Radius in meters
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.6f; // Bounciness
        birdBody.createFixture(fixtureDef);
        circle.dispose();

        // Set bird size and position for rendering
        setSize(50, 50); // Pixel size
        setPosition(x, y);

        // Initially deactivate physics simulation
        birdBody.setAwake(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Draw the bird at its physics position
        Vector2 position = birdBody.getPosition();
        float x = position.x * 100 - getWidth() / 2;
        float y = position.y * 100 - getHeight() / 2;
        batch.draw(birdTexture, x, y, getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Handle dragging and launching logic
        if (isDragging) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX() / 100f, (Gdx.graphics.getHeight() - Gdx.input.getY()) / 100f);
            birdBody.setTransform(touchPosition, 0);
        } else if (!launched) {
            // Reset position to initial point if not launched
            birdBody.setTransform(initialPosition, 0);
        }
    }

    public void handleInput() {
        // Handle touch events for dragging
        if (Gdx.input.isTouched()) {
            isDragging = true;
        } else if (isDragging) {
            // Release bird when drag ends
            isDragging = false;
            launch(new Vector2(-15f, 10f)); // Example launch velocity
        }
    }

    public void launch(Vector2 velocity) {
        birdBody.setAwake(true); // Activate physics
        birdBody.setLinearVelocity(velocity); // Apply velocity to the bird
        launched = true; // Mark the bird as launched
    }

    public void reset() {
        // Reset bird to initial position and state
        birdBody.setTransform(initialPosition, 0);
        birdBody.setLinearVelocity(Vector2.Zero);
        birdBody.setAwake(false);
        launched = false;
    }

    public void dispose() {
        birdTexture.dispose();
    }
}
