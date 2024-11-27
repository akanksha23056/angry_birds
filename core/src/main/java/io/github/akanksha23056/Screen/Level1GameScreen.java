package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.akanksha23056.Main;
import io.github.akanksha23056.Objects.Bird;
import io.github.akanksha23056.Objects.Catapult;

public class Level1GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture levelImage;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;
    private final Rectangle pauseButtonBounds;

    private final Stage stage;
    private final Catapult catapult;
    private final Array<Bird> birds; // Array to manage multiple birds

    private Bird currentBird; // Bird currently on the catapult
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

        // Initialize Birds
        this.birds = new Array<>();
        initializeBirds();

        // Set the first bird on the catapult
        if (birds.size > 0) {
            setCurrentBird(birds.first());
        }

        this.isPaused = false; // Initialize pause state as not paused
    }

    private void initializeBirds() {
        float birdStartX = 50; // Starting X position for extra birds
        float birdStartY = 100; // Y position (below the catapult)

        // Create three birds as an example
        for (int i = 0; i < 3; i++) {
            Bird bird = new Bird(world, "redbird.png", birdStartX + i * 50, birdStartY);
            birds.add(bird);
            stage.addActor(bird);
        }
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
        birdBodyDef.position.set(currentBird.getX(), currentBird.getY());

        PolygonShape birdShape = new PolygonShape();
        birdShape.setAsBox(currentBird.getWidth() / 2, currentBird.getHeight() / 2);

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
                Vector2 launchVelocity = new Vector2(10, 10); // Example launch velocity
                launchBird(launchVelocity); // Launch the bird
                isBirdLaunched = true;

                // Move to next bird if available
                Gdx.app.postRunnable(() -> {
                    birds.removeValue(currentBird, true); // Remove launched bird
                    if (birds.size > 0) {
                        setCurrentBird(birds.first());
                    }
                });
            }
        }
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
                game.playButtonClickSound(); // Play button click sound
                pauseGame(); // Pause the game and go to PauseScreen
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
        for (Bird bird : birds) {
            bird.dispose();
        }
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
}
