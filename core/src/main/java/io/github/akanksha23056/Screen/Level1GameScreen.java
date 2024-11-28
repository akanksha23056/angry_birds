package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.akanksha23056.Main;
import io.github.akanksha23056.Objects.Bird;
import io.github.akanksha23056.Objects.Block;
import io.github.akanksha23056.Objects.Catapult;
import io.github.akanksha23056.Objects.Pig;

public class Level1GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture levelImage;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;
    private final Rectangle pauseButtonBounds;

    private final Stage stage;
    private final Catapult catapult;
    private Bird currentBird; // Single bird on the screen
    private boolean isBirdLaunched; // Tracks if the current bird is launched

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private boolean isPaused; // Tracks if the game is paused

    private Body currentBirdBody; // Box2D body for the current bird

    public Level1GameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch;
        this.levelImage = new Texture(levelImagePath);
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");
        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);

        // Initialize Box2D World
        world = new World(new Vector2(0, -9.8f), true); // Gravity points downward
        debugRenderer = new Box2DDebugRenderer();

        // Initialize Stage
        this.stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        // Initialize Catapult
        this.catapult = new Catapult("sling.png", 100, 100);
        stage.addActor(catapult);

        // Initialize Bird
        initializeBird();

        // Initialize Blocks
        initializeBlocks();

        // Create screen boundaries
        createScreenBoundaries();

        // Create ground
        createGround();

        this.isPaused = false; // Initialize pause state as not paused
    }

    private void initializeBird() {
        float birdStartX = 50; // Starting X position for the bird
        float birdStartY = 100; // Y position (below the catapult)

        // Create one bird
        currentBird = new Bird(world, "redbird.png", birdStartX, birdStartY, 0.1f);
        stage.addActor(currentBird);
        setCurrentBird(currentBird);
    }

    private void setCurrentBird(Bird bird) {
        currentBird = bird;
        currentBird.setPosition(catapult.getX() + 20, catapult.getY() + 50); // Adjust bird position on catapult
        isBirdLaunched = false;

        // Remove any previous Box2D body if present
        if (currentBirdBody != null) {
            world.destroyBody(currentBirdBody);
        }

        // Initialize Box2D physics for the current bird
        BodyDef birdBodyDef = new BodyDef();
        birdBodyDef.type = BodyDef.BodyType.DynamicBody;
        birdBodyDef.position.set(currentBird.getX() / 100, currentBird.getY() / 100);

        PolygonShape birdShape = new PolygonShape();
        birdShape.setAsBox(currentBird.getWidth() / 200, currentBird.getHeight() / 200);

        FixtureDef birdFixtureDef = new FixtureDef();
        birdFixtureDef.shape = birdShape;
        birdFixtureDef.density = 1.0f;
        birdFixtureDef.restitution = 0.5f; // Bounciness
        birdFixtureDef.friction = 0.3f; // Friction

        currentBirdBody = world.createBody(birdBodyDef);
        currentBirdBody.createFixture(birdFixtureDef);

        birdShape.dispose(); // Clean up shape after use

        currentBird.reset(); // Reset bird physics and state
    }

    private void initializeBlocks() {
        float scaleFactor = 0.1f; // 30% smaller than the previous 0.2f scale factor
        float startX1 = Gdx.graphics.getWidth() - 200; // Start position for the first stack
        float startX2 = Gdx.graphics.getWidth() - 100; // Start position for the second stack
        float startY = 100; // Y position same as the bird and sling

        // Add a pig on top of the second stack
        Pig pig = new Pig(world, "pig.png", startX2, startY, scaleFactor);
        stage.addActor(pig);

        // Create first stack of two blocks
        for (int i = 0; i < 2; i++) {
            Block block = new Block(world, "crate.png", startX1, startY, scaleFactor);
            stage.addActor(block);
            startY -= block.getHeight() + 10; // Arrange blocks with a gap
        }

        // Reset startY for the second stack
        startY = 100; // Y position same as the bird and sling

        // Create second stack of two blocks
        for (int i = 0; i < 2; i++) {
            Block block = new Block(world, "crate.png", startX2, startY, scaleFactor);
            stage.addActor(block);
            startY -= block.getHeight() + 10; // Arrange blocks with a gap
        }
    }

    private void createScreenBoundaries() {
        float screenWidth = Gdx.graphics.getWidth() / 100f;
        float screenHeight = Gdx.graphics.getHeight() / 100f;

        // Create ground
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(screenWidth / 2, 0);
        Body groundBody = world.createBody(groundBodyDef);
        EdgeShape groundShape = new EdgeShape();
        groundShape.set(-screenWidth / 2, 0, screenWidth / 2, 0);
        groundBody.createFixture(groundShape, 0);
        groundShape.dispose();

        // Create left wall
        BodyDef leftWallBodyDef = new BodyDef();
        leftWallBodyDef.position.set(0, screenHeight / 2);
        Body leftWallBody = world.createBody(leftWallBodyDef);
        EdgeShape leftWallShape = new EdgeShape();
        leftWallShape.set(0, -screenHeight / 2, 0, screenHeight / 2);
        leftWallBody.createFixture(leftWallShape, 0);
        leftWallShape.dispose();

        // Create right wall
        BodyDef rightWallBodyDef = new BodyDef();
        rightWallBodyDef.position.set(screenWidth, screenHeight / 2);
        Body rightWallBody = world.createBody(rightWallBodyDef);
        EdgeShape rightWallShape = new EdgeShape();
        rightWallShape.set(0, -screenHeight / 2, 0, screenHeight / 2);
        rightWallBody.createFixture(rightWallShape, 0);
        rightWallShape.dispose();

        // Create ceiling
        BodyDef ceilingBodyDef = new BodyDef();
        ceilingBodyDef.position.set(screenWidth / 2, screenHeight);
        Body ceilingBody = world.createBody(ceilingBodyDef);
        EdgeShape ceilingShape = new EdgeShape();
        ceilingShape.set(-screenWidth / 2, 0, screenWidth / 2, 0);
        ceilingBody.createFixture(ceilingShape, 0);
        ceilingShape.dispose();
    }

    private void createGround() {
        float groundWidth = Gdx.graphics.getWidth() / 100f;
        float groundHeight = 1f; // Height of the ground

        // Create ground body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(groundWidth / 2, groundHeight / 2);
        Body groundBody = world.createBody(groundBodyDef);

        // Create ground shape
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(groundWidth / 2, groundHeight / 2);

        // Attach shape to the ground body
        groundBody.createFixture(groundShape, 0);
        groundShape.dispose();
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
        ScreenUtils.clear(0.15F, 0.15F, 0.2F, 1.0F);

        // Update physics simulation
        world.step(1 / 60f, 6, 2);

        // Render the game background and UI
        batch.begin();
        batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw the pause button at the end to ensure it is on top
        drawPauseButton();

        // Draw the trajectory if the bird is being dragged
        if (currentBird != null && currentBird.isDragging()) {
            drawTrajectory(currentBird.getX(), currentBird.getY(), new Vector2(10, 10)); // Example velocity
        }

        batch.end();

        // Draw Box2D debug shapes (optional, for visualization)
        debugRenderer.render(world, stage.getCamera().combined);

        // Render stage (actors, including birds and catapult)
        stage.act(delta);
        stage.draw();

        // Handle Bird Launching Logic
        handleBirdLaunch();
    }

    private void handleBirdLaunch() {
        if (currentBird != null && !isBirdLaunched) {
            if (Gdx.input.isButtonJustPressed(0)) { // Left mouse button press to simulate launch
                Vector2 launchVelocity = calculateLaunchVelocity(); // Calculate launch velocity
                launchBird(launchVelocity); // Launch the bird
                isBirdLaunched = true;
            }
        }
    }

    private Vector2 calculateLaunchVelocity() {
        float dragDistanceX = currentBird.getX() - catapult.getX();
        float dragDistanceY = currentBird.getY() - catapult.getY();
        float launchSpeed = 10f; // Adjust this value to control the launch speed
        return new Vector2(dragDistanceX * launchSpeed, dragDistanceY * launchSpeed);
    }

    private void launchBird(Vector2 velocity) {
        if (currentBirdBody != null) {
            currentBirdBody.setLinearVelocity(velocity); // Set the velocity for the bird's movement
        }
    }

    private void drawPauseButton() {
        boolean isHovered = pauseButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        // Draw the pause button with hover effect
        if (isHovered) {
            batch.draw(pauseButtonHoverTexture, pauseButtonBounds.x - 5.0F, pauseButtonBounds.y - 5.0F,
                pauseButtonBounds.width + 10.0F, pauseButtonBounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(0)) { // Check for left mouse button press
                pauseGame();
            }
        } else {
            batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y,
                pauseButtonBounds.width, pauseButtonBounds.height);
        }
    }

    private void pauseGame() {
        isPaused = true; // Set the game to paused state
        game.setScreen(new PauseScreen(game)); // Transition to PauseScreen
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        pauseButtonTexture.dispose();
        pauseButtonHoverTexture.dispose();
        stage.dispose();
        world.dispose();
        debugRenderer.dispose();
        currentBird.dispose();
        catapult.dispose();
    }

    // Empty lifecycle methods
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    private void drawTrajectory(float startX, float startY, Vector2 velocity) {
        float timeStep = 0.1f; // Time step for calculating trajectory points
        int numPoints = 30; // Number of points to draw
        float gravity = -9.8f; // Gravity value

        for (int i = 0; i < numPoints; i++) {
            float t = i * timeStep;
            float x = startX + velocity.x * t;
            float y = startY + velocity.y * t + 0.5f * gravity * t * t;
            batch.draw(new Texture("trajectory.png"), x, y, 5, 5); // Draw trajectory points
        }
    }
}
