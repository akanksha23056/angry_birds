package io.github.akanksha23056.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class Bird extends Image {
    private final World world;
    private final Body body;
    private boolean isDragging;

    public Bird(World world, String texturePath, float x, float y, float scaleFactor) {
        super(new Texture(texturePath));
        this.world = world;
        this.setPosition(x, y);
        this.setSize(getWidth() * scaleFactor, getHeight() * scaleFactor);

        // Create Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / 100, y / 100);
        this.body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / 200, getHeight() / 200);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.friction = 0.3f;

        body.createFixture(fixtureDef);
        shape.dispose();

        // Add drag listener
        addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                Bird.this.setPosition(Bird.this.getX() + x - getWidth() / 2, Bird.this.getY() + y - getHeight() / 2);
                isDragging = true;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                isDragging = false;
            }
        });
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void reset() {
        body.setLinearVelocity(0, 0);
        body.setAngularVelocity(0);
        body.setTransform(getX() / 100, getY() / 100, 0);
    }

    @Override
    public void act(float delta) {
        if (!isDragging) {
            setPosition(body.getPosition().x * 100 - getWidth() / 2, body.getPosition().y * 100 - getHeight() / 2);
        }
        super.act(delta);
    }

    public void dispose() {
        getTexture().dispose();
    }

    private Texture getTexture() {
        return (Texture) getDrawable();
    }
}
