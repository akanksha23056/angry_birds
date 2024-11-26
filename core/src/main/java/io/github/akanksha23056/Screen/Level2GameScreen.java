package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

public class Level2GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private Texture levelImage;
    private Texture slingshotTexture;
    private Texture birdTexture;
    private Texture crateTexture;
    private Texture pauseButtonTexture;
    private Texture pauseButtonHoverTexture;
    private Rectangle pauseButtonBounds;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

//    private Body groundBody;
//    private Body slingshotBaseBody;
//    private Body birdBody;
//    private MouseJoint mouseJoint;
//    private Body selectedBody;

    public Level2GameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch;

        try {
            // Initialize textures
            this.levelImage = new Texture(Gdx.files.internal(levelImagePath));
            this.slingshotTexture = new Texture("sling.png");
            this.birdTexture = new Texture("redbird.png");
            this.crateTexture = new Texture("crate.png");

            this.pauseButtonTexture = new Texture("pause.png");
            this.pauseButtonHoverTexture = new Texture("pause_hover.png");
            this.pauseButtonBounds = new Rectangle(10.0F, Gdx.graphics.getHeight() - 110.0F, 100.0F, 100.0F);

            // Initialize Box2D World and Debug Renderer
            world = new World(new Vector2(0, -9.8f), true);
            debugRenderer = new Box2DDebugRenderer();

            // Initialize Camera
            camera = new OrthographicCamera();
            camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } catch (Exception e) {
            Gdx.app.error("Level2GameScreen", "Initialization error: " + e.getMessage(), e);
        }
    }

    @Override
    public void show() {
        try {
            // Background music setup
            if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
                game.backgroundMusic.play();
            }
        } catch (Exception e) {
            Gdx.app.error("Level2GameScreen", "Error in show(): " + e.getMessage(), e);
        }
    }

    @Override
    public void render(float delta) {
        try {
            // Clear the screen with a specific color
            ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);  // A dark grey background for visibility

            // Update physics world
            world.step(1 / 60f, 6, 2);

            // Update camera and projection matrix
            camera.update();
            batch.setProjectionMatrix(camera.combined);

            // Start drawing elements
            batch.begin();

            // Draw the background image
            batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // Draw the slingshot at a fixed position
            batch.draw(slingshotTexture, 150, 100, 100, 200);  // Adjust position as needed

            // Draw the bird behind the slingshot, adjusted position
            batch.draw(birdTexture, 130, 130, 50, 50);  // Adjusted position to be behind slingshot

            // Draw the crate at a fixed debug position
            batch.draw(crateTexture, 300, 100, 50, 50);  // Ensure crate texture visibility

            // Draw the pause button
            drawPauseButton();

            batch.end();

            // Render Box2D Debug Information
            debugRenderer.render(world, camera.combined);
        } catch (Exception e) {
            Gdx.app.error("Level2GameScreen", "Error in render(): " + e.getMessage(), e);
        }
    }

    private void drawPauseButton() {
        try {
            boolean isHovered = pauseButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

            // Draw the pause button with hover effect
//            batch.begin();
            if (isHovered) {
                batch.draw(pauseButtonHoverTexture, pauseButtonBounds.x - 5.0F, pauseButtonBounds.y - 5.0F,
                    pauseButtonBounds.width + 10.0F, pauseButtonBounds.height + 10.0F);
                if (Gdx.input.isButtonJustPressed(0)) { // Check for left mouse button press
                    game.playButtonClickSound(); // Play button click sound
                    Gdx.app.postRunnable(() -> {
                        game.setScreen(new PauseScreen(game)); // Transition to PauseScreen
                    });
                }
            } else {
                batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y,
                    pauseButtonBounds.width, pauseButtonBounds.height);
            }
//            batch.end();
        } catch (Exception e) {
            Gdx.app.error("Level2GameScreen", "Error in drawPauseButton(): " + e.getMessage(), e);
        }
    }

    @Override
    public void dispose() {
        try {
            // Dispose of resources
            levelImage.dispose();
            slingshotTexture.dispose();
            birdTexture.dispose();
            crateTexture.dispose();
            pauseButtonTexture.dispose();
            pauseButtonHoverTexture.dispose();
            world.dispose();
            debugRenderer.dispose();
        } catch (Exception e) {
            Gdx.app.error("Level2GameScreen", "Error during dispose(): " + e.getMessage(), e);
        }
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
