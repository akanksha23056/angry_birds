package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

import java.util.ArrayList;

public class Level2GameScreen implements Screen {
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

    private Vector2 slingshotPosition;
    private float slingRadius = 1.5f; // Maximum drag distance for the bird
    private boolean isDragging = false;

    private Texture pauseButtonTexture;
    private Texture pauseButtonHoverTexture;
    private final Rectangle pauseButtonBounds;

    private Body birdBody;
    private MouseJoint mouseJoint;
    private Body groundBody;
    private final ArrayList<Body> pigs = new ArrayList<>();
    private final ArrayList<Body> crates = new ArrayList<>();

    private boolean isPaused = false; // Tracks if the game is paused

    public Level2GameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch;

        // Load textures
        this.levelImage = new Texture(Gdx.files.internal("level2game.jpg"));
        this.slingshotTexture = new Texture(Gdx.files.internal("sling.png"));
        this.birdTexture = new Texture(Gdx.files.internal("redbird.png"));
        this.pigTexture = new Texture(Gdx.files.internal("pig.png"));
        this.crateTexture = new Texture(Gdx.files.internal("crate.png"));

        this.slingshotPosition = new Vector2(3.5f, 2.4f);

        // Load pause button textures
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");

        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);

        // Initialize Box2D world and renderer
        world = new World(new Vector2(0, -9.8f), true); // Gravity
        debugRenderer = new Box2DDebugRenderer();

        // Initialize Camera
        camera = new OrthographicCamera(16, 9); // World units (adjust for your aspect ratio)
        camera.position.set(8, 5, 0); // Center camera
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
        groundBodyDef.position.set(8, 1.39f); // Centered at the bottom
        groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(8, 0.5f); // Ground spans the screen width
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
    }

    private void createSlingshot() {
        // Visual only (no physics for slingshot base)
    }

    private void createBird() {
        BodyDef birdDef = new BodyDef();
        birdDef.type = BodyDef.BodyType.DynamicBody;
        birdDef.position.set(2, 2.2f); // Positioned near the slingshot
        birdBody = world.createBody(birdDef);

        CircleShape birdShape = new CircleShape();
        birdShape.setRadius(0.4f); // Smaller bird size
        birdBody.createFixture(birdShape, 1.0f).setUserData("bird");
        birdShape.dispose();
    }

    private void createPigs() {
        for (int i = 0; i < 3; i++) {
            BodyDef pigDef = new BodyDef();
            pigDef.type = BodyDef.BodyType.DynamicBody;
            pigDef.position.set(6 + i * 2, 2.7f); // Place pigs higher above crates
            Body pigBody = world.createBody(pigDef);

            CircleShape pigShape = new CircleShape();
            pigShape.setRadius(0.4f); // Smaller pig size
            pigBody.createFixture(pigShape, 0.5f).setUserData("pig");
            pigShape.dispose();

            pigs.add(pigBody);
        }
    }

    private void createCrates() {
        for (int i = 0; i < 3; i++) {
            BodyDef crateDef = new BodyDef();
            crateDef.type = BodyDef.BodyType.DynamicBody;
            crateDef.position.set(6 + i * 2, 1.7f); // Place crates lower on the screen
            Body crateBody = world.createBody(crateDef);

            PolygonShape crateShape = new PolygonShape();
            crateShape.setAsBox(0.4f, 0.4f); // Smaller crate size
            crateBody.createFixture(crateShape, 0.5f).setUserData("crate");
            crateShape.dispose();

            crates.add(crateBody);
        }
    }

    @Override
    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1.0f);

        // Render the background and pause button
        batch.begin();
        batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw and draw the pause button at the end to ensure it is on top
        drawPauseButton();

        batch.end();


        // Update the physics world
        world.step(1 / 60f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Render game elements
        batch.begin();


        // Draw the bird
        Vector2 birdPosition = birdBody.getPosition();
        batch.draw(birdTexture, birdPosition.x - 0.4f, birdPosition.y - 0.4f, 0.8f, 0.8f);

        // Draw pigs
        for (Body pig : pigs) {
            Vector2 pigPosition = pig.getPosition();
            batch.draw(pigTexture, pigPosition.x - 0.4f, pigPosition.y - 0.4f, 0.8f, 0.8f);
        }

        // Draw crates
        for (Body crate : crates) {
            Vector2 cratePosition = crate.getPosition();
            batch.draw(crateTexture, cratePosition.x - 0.4f, cratePosition.y - 0.4f, 0.8f, 0.8f);
        }

        batch.end();

        // Render Box2D debug information
        debugRenderer.render(world, camera.combined);

        // Reset bird if it goes out of bounds
        if (birdPosition.x < 0 || birdPosition.x > 16 || birdPosition.y < 0 || birdPosition.y > 9) {
            resetBird();
        }
    }
    private void resetBird() {
        // Reset the bird's position to its starting location
        birdBody.setTransform(2, 2.2f, 0); // Replace (2, 2.2f) with your slingshot's default position if needed

        // Reset the bird's velocity and rotation
        birdBody.setLinearVelocity(0, 0);
        birdBody.setAngularVelocity(0);
        birdBody.setAwake(true); // Ensure the bird is active in the physics world
    }



    private void drawPauseButton() {
        boolean isHovered = pauseButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        if (isHovered) {
            batch.draw(pauseButtonHoverTexture, pauseButtonBounds.x - 5, pauseButtonBounds.y - 5,
                pauseButtonBounds.width + 10, pauseButtonBounds.height + 10);
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                pauseGame();
            }
        } else {
            batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y,
                pauseButtonBounds.width, pauseButtonBounds.height);
        }
    }

    private void pauseGame() {
        isPaused = true;
        game.setScreen(new PauseScreen(game));
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
