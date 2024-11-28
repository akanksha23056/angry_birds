package io.github.akanksha23056.Objects;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.physics.box2d.*;

public class Bird extends Image {
    private Body body;
    private boolean isDragging = false;

    public Bird(World world, String texturePath, float x, float y, float scaleFactor) {
        super(new Texture(texturePath));
        setSize(getWidth() * scaleFactor, getHeight() * scaleFactor);
        setPosition(x, y);

        // Define Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / 100, y / 100); // Scale to Box2D units

        body = world.createBody(bodyDef);

        // Define Box2D shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / 200, getHeight() / 200); // Half-width and half-height

        // Attach shape to the body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.3f;

        body.createFixture(fixtureDef);
        shape.dispose();

        // Add input listener for dragging
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isDragging = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (isDragging) {
                    setPosition(event.getStageX() - getWidth() / 2, event.getStageY() - getHeight() / 2);
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isDragging = false;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isDragging) {
            // Synchronize position with Box2D body
            setPosition(body.getPosition().x * 100 - getWidth() / 2, body.getPosition().y * 100 - getHeight() / 2);
        }
    }

    public void launch(Vector2 velocity) {
        body.setLinearVelocity(velocity);
    }

    public void reset() {
        body.setLinearVelocity(Vector2.Zero);
        body.setAngularVelocity(0);
        body.setTransform(getX() / 100, getY() / 100, 0);
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void dispose() {
        getTexture().dispose();
    }

    private ApplicationListener getTexture() {
        return null;
    }
}
