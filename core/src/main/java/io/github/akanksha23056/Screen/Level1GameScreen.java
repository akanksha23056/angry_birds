package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

import java.util.ArrayList;

public class Level1GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera camera;

    private Texture levelImage;
    private Texture slingshotTexture;
    private Texture birdTexture;
    private Texture pigTexture;
    private Texture crateTexture;

    private Texture pauseButtonTexture;
    private Texture pauseButtonHoverTexture;
    private final Rectangle pauseButtonBounds;

    private Body birdBody;
    private MouseJoint mouseJoint;
    private Body groundBody;
    private final ArrayList<Body> pigs = new ArrayList<>();
    private final ArrayList<Body> crates = new ArrayList<>();

    public Level1GameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch;

        // Load textures
        this.levelImage = new Texture(Gdx.files.internal(levelImagePath));
//        this.slingshotTexture = new Texture("sling.png");
//        this.birdTexture = new Texture("redbird.png");
        this.slingshotTexture = new Texture(Gdx.files.internal("sling.png"));
        this.birdTexture = new Texture(Gdx.files.internal("redbird.png"));
        this.pigTexture = new Texture("pig.png");
        this.crateTexture = new Texture("crate.png");

        // Load pause button textures
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");
        this.pauseButtonBounds = new Rectangle(10.0F, Gdx.graphics.getHeight() - 110.0F, 100.0F, 100.0F);

        // Initialize Box2D world and renderer
        world = new World(new Vector2(0, -9.8f), true); // Gravity
        debugRenderer = new Box2DDebugRenderer();

        // Initialize Camera
        camera = new OrthographicCamera(16, 9); // World units (adjust for your aspect ratio)
        camera.position.set(8, 4.5f, 0); // Center camera
        camera.update();

        // Create game objects
        createGround();
        createSlingshot();
        createBird();
        createPigs();
        createCrates();

        // Add contact listener
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                // Handle collisions
            }

            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }

    private void createGround() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(8, -1); // Centered at the bottom
        groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(8, 1); // Ground spans the screen width
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
    }

    private void createSlingshot() {
        // Visual only (no physics for slingshot base)
    }

    private void createBird() {
        BodyDef birdDef = new BodyDef();
        birdDef.type = BodyDef.BodyType.DynamicBody;
        birdDef.position.set(2, 1); // Positioned near the slingshot
        birdBody = world.createBody(birdDef);

        CircleShape birdShape = new CircleShape();
        birdShape.setRadius(0.3f); // Smaller bird size
        birdBody.createFixture(birdShape, 1.0f).setUserData("bird");
        birdShape.dispose();
    }

    private void createPigs() {
        for (int i = 0; i < 3; i++) {
            BodyDef pigDef = new BodyDef();
            pigDef.type = BodyDef.BodyType.DynamicBody;
            pigDef.position.set(6 + i * 2, 1); // Positioned across the screen
            Body pigBody = world.createBody(pigDef);

            CircleShape pigShape = new CircleShape();
            pigShape.setRadius(0.3f); // Smaller pig size
            pigBody.createFixture(pigShape, 0.5f).setUserData("pig");
            pigShape.dispose();

            pigs.add(pigBody);
        }
    }

    private void createCrates() {
        for (int i = 0; i < 3; i++) {
            BodyDef crateDef = new BodyDef();
            crateDef.type = BodyDef.BodyType.DynamicBody;
            crateDef.position.set(6 + i * 2, 2); // Stacked on pigs
            Body crateBody = world.createBody(crateDef);

            PolygonShape crateShape = new PolygonShape();
            crateShape.setAsBox(0.4f, 0.4f); // Smaller crate size
            crateBody.createFixture(crateShape, 0.5f).setUserData("crate");
            crateShape.dispose();

            crates.add(crateBody);
        }
    }

    private void createMouseJoint(Vector2 target) {
        if (groundBody == null || birdBody == null) {
            Gdx.app.error("Level1GameScreen", "Ground or bird body is null!");
            return; // Prevent the crash
        }

        if (mouseJoint != null) {
            world.destroyJoint(mouseJoint); // Destroy existing joint
        }

        MouseJointDef jointDef = new MouseJointDef();
        jointDef.bodyA = groundBody; // Static ground body
        jointDef.bodyB = birdBody; // Bird body
        jointDef.collideConnected = true;
        jointDef.maxForce = 1000.0f * birdBody.getMass();
        jointDef.target.set(target); // Set target

        mouseJoint = (MouseJoint) world.createJoint(jointDef);
    }



    @Override
    public void show() {
        // Called when this screen becomes the current screen for the game.
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }


    @Override
    public void render(float delta) {
        try {
            ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1.0f);

            // Update physics world
            world.step(1 / 60f, 6, 2);

            // Update camera
            camera.update();
            batch.setProjectionMatrix(camera.combined);

            // Draw all objects
            batch.begin();
            batch.draw(levelImage, 0, 0, 16, 9);
            batch.draw(birdTexture, birdBody.getPosition().x - 0.3f, birdBody.getPosition().y - 0.3f, 0.6f, 0.6f);
            batch.end();

            // Render Box2D Debug Information
            debugRenderer.render(world, camera.combined);

            // Handle mouse dragging
            if (Gdx.input.isTouched()) {
                Vector3 target3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(target3);
                Vector2 target2 = new Vector2(target3.x, target3.y);

                if (mouseJoint == null) {
                    createMouseJoint(target2);
                } else {
                    mouseJoint.setTarget(target2);
                }
            } else if (mouseJoint != null) {
                world.destroyJoint(mouseJoint);
                mouseJoint = null;
            }
        } catch (Exception e) {
            Gdx.app.error("Level1GameScreen", "Error in render(): " + e.getMessage(), e);
        }
    }


    private void drawPauseButton() {
        boolean isHovered = pauseButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        batch.begin();
        if (isHovered) {
            batch.draw(pauseButtonHoverTexture, pauseButtonBounds.x - 5.0F, pauseButtonBounds.y - 5.0F,
                pauseButtonBounds.width + 10.0F, pauseButtonBounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                game.playButtonClickSound();
                Gdx.app.postRunnable(() -> game.setScreen(new PauseScreen(game)));
            }
        } else {
            batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y,
                pauseButtonBounds.width, pauseButtonBounds.height);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        slingshotTexture.dispose();
        birdTexture.dispose();
        pigTexture.dispose();
        crateTexture.dispose();
        pauseButtonTexture.dispose();
        pauseButtonHoverTexture.dispose();
        debugRenderer.dispose();
        world.dispose();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
