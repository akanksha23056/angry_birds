package io.github.akanksha23056.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Pig extends Actor {
    private final Texture pigTexture;
    private final Vector2 position;
    private final World world;
    private Body pigBody;
    private final float width = 50;  // Default pig width
    private final float height = 50; // Default pig height

    public Pig(World world, String texturePath, float x, float y) {
        this.world = world;
        this.pigTexture = new Texture(texturePath);
        this.position = new Vector2(x, y);

        // Create the Box2D body
        createPigBody(x, y);
    }

    private void createPigBody(float x, float y) {
        // Define the shape of the pig (a simple rectangle)
        PolygonShape pigShape = new PolygonShape();
        pigShape.setAsBox(width / 2, height / 2); // Half-width and half-height for the box shape

        // Define the body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // The pig should move (dynamic)
        bodyDef.position.set(x, y);  // Set the position to where the pig starts

        // Create the body in the world
        pigBody = world.createBody(bodyDef);

        // Create the fixture for the body using the shape
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = pigShape;
        fixtureDef.density = 1.0f; // Set the density of the pig
        fixtureDef.friction = 0.5f; // Add some friction to the pig
        fixtureDef.restitution = 0.2f; // Add some restitution (bounciness)

        // Attach the fixture to the body
        pigBody.createFixture(fixtureDef);

        // Clean up the shape after use
        pigShape.dispose();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Update the position based on the Box2D body's position
        position.set(pigBody.getPosition().x, pigBody.getPosition().y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float scaleFactor = 0.5f; // Scale the pig texture
        batch.draw(pigTexture, position.x - pigTexture.getWidth() / 2 * scaleFactor,
            position.y - pigTexture.getHeight() / 2 * scaleFactor,
            pigTexture.getWidth() * scaleFactor, pigTexture.getHeight() * scaleFactor);
    }

    public void dispose() {
        pigTexture.dispose();
        world.destroyBody(pigBody); // Don't forget to dispose of the Box2D body
    }

    // Optional getter method for the Box2D body to handle specific interactions if needed
    public Body getPigBody() {
        return pigBody;
    }
}
