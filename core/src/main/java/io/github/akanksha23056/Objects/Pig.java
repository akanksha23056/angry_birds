package io.github.akanksha23056.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Pig extends Image {
    private final World world;
    private final Body body;

    public Pig(World world, String texturePath, float x, float y, float scaleFactor) {
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

        body.setUserData(this); // Set user data to identify the pig
    }

    public void setTexture(Texture texture) {
        super.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    @Override
    public void act(float delta) {
        setPosition(body.getPosition().x * 100 - getWidth() / 2, body.getPosition().y * 100 - getHeight() / 2);
        super.act(delta);
    }

    public void dispose() {
        getTexture().dispose();
    }

    private Texture getTexture() {
        return (Texture) getDrawable();
    }
}
