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
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;


public class Level2GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private Texture levelImage;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;
    private final Rectangle pauseButtonBounds;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    // Resources for game logic
    private Body groundBody;
    private Body birdBody;
    private MouseJoint mouseJoint;

    public Level2GameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch; // Use the shared batch from the main game
        this.levelImage = new Texture(Gdx.files.internal(levelImagePath)); // Load image here
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");
        this.pauseButtonBounds = new Rectangle(10.0F, Gdx.graphics.getHeight() - 110.0F, 100.0F, 100.0F);
    }

    @Override
    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }

        // Initialize Box2D World
        world = new World(new Vector2(0, -9.8f), true); // Gravity set to -9.8
        debugRenderer = new Box2DDebugRenderer();

        // Initialize Camera
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0.15F, 0.15F, 0.2F, 1.0F);

        // Update physics
        world.step(1 / 60f, 6, 2);

        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Render level background
        batch.begin();
        batch.draw(levelImage, 0.0F, 0.0F, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        drawPauseButton();
        batch.end();

        // Render Box2D Debug Information
        debugRenderer.render(world, camera.combined);
    }

    private void drawPauseButton() {
        boolean isHovered = pauseButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        // Draw the pause button with hover effect
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
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        pauseButtonTexture.dispose();
        pauseButtonHoverTexture.dispose();
        world.dispose();
        debugRenderer.dispose();
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
