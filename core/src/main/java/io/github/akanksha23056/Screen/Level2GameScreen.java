package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

import java.util.ArrayList;

public class Level2GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;

    // Textures
    private final Texture levelImage;
    private final Texture slingshotTexture;
    private final Texture birdTexture;
    private final Texture pigTexture;
    private final Texture crateTexture;

    // Bird properties
    private Vector2 birdPosition;
    private Vector2 birdVelocity;
    private boolean isDragging;

    // Gravity and damping
    private final Vector2 gravity = new Vector2(0, -0.05f); // Simulated gravity
    private final float damping = 0.98f; // Slow down velocity over time

    // Sling properties
    private final Vector2 slingshotPosition;
    private final float slingshotRadius = 1.5f;

    // Pigs and Crates
    private final ArrayList<Rectangle> pigs = new ArrayList<>();
    private final ArrayList<Rectangle> crates = new ArrayList<>();

    // Ground height
    private final float groundY = 100;

    public Level2GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Load textures
        this.levelImage = new Texture("level2game.jpg");
        this.slingshotTexture = new Texture("sling.png");
        this.birdTexture = new Texture("redbird.png");
        this.pigTexture = new Texture("pig.png");
        this.crateTexture = new Texture("crate.png");

        // Slingshot position
        this.slingshotPosition = new Vector2(100, 200);

        // Bird properties
        this.birdPosition = new Vector2(slingshotPosition.x, slingshotPosition.y);
        this.birdVelocity = new Vector2(0, 0);
        this.isDragging = false;

        // Initialize pigs
        for (int i = 0; i < 3; i++) {
            pigs.add(new Rectangle(400 + i * 100, 150, 50, 50)); // X, Y, Width, Height
        }

        // Initialize crates
        for (int i = 0; i < 3; i++) {
            crates.add(new Rectangle(400 + i * 100, 100, 50, 50)); // X, Y, Width, Height
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
        // Clear screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1.0f);

        // Update game mechanics
        updateBirdPosition();

        // Check collisions
        checkCollisions();

        // Draw everything
        batch.begin();

        // Draw background
        batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw slingshot
        batch.draw(slingshotTexture, slingshotPosition.x - 25, slingshotPosition.y - 50, 50, 100);

        // Draw bird
        batch.draw(birdTexture, birdPosition.x - 25, birdPosition.y - 25, 50, 50);

        // Draw pigs
        for (Rectangle pig : pigs) {
            batch.draw(pigTexture, pig.x, pig.y, pig.width, pig.height);
        }

        // Draw crates
        for (Rectangle crate : crates) {
            batch.draw(crateTexture, crate.x, crate.y, crate.width, crate.height);
        }

        batch.end();
    }

    private void updateBirdPosition() {
        // Dragging mechanics
        if (Gdx.input.isTouched()) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

            if (isDragging || touchPosition.dst(slingshotPosition) <= slingshotRadius * 100) {
                isDragging = true;

                // Constrain bird within slingshot radius
                if (touchPosition.dst(slingshotPosition) > slingshotRadius * 100) {
                    birdPosition.set(slingshotPosition.cpy().add(touchPosition.sub(slingshotPosition).nor().scl(slingshotRadius * 100)));
                } else {
                    birdPosition.set(touchPosition);
                }
            }
        } else if (isDragging) {
            // Release the bird
            isDragging = false;

            // Launch bird
            birdVelocity.set(slingshotPosition.cpy().sub(birdPosition).scl(0.1f)); // Adjust scaling factor for speed
        }

        // Apply velocity and gravity
        if (!isDragging) {
            birdVelocity.add(gravity);
            birdVelocity.scl(damping);
            birdPosition.add(birdVelocity);

            // Keep bird above ground
            if (birdPosition.y < groundY) {
                birdPosition.y = groundY;
                birdVelocity.setZero();
            }
        }
    }

    private void checkCollisions() {
        // Check if bird hits any pigs
        for (int i = pigs.size() - 1; i >= 0; i--) {
            Rectangle pig = pigs.get(i);
            if (pig.contains(birdPosition.x, birdPosition.y)) {
                pigs.remove(i); // Remove pig
            }
        }

        // Check if bird hits any crates
        for (int i = crates.size() - 1; i >= 0; i--) {
            Rectangle crate = crates.get(i);
            if (crate.contains(birdPosition.x, birdPosition.y)) {
                crates.remove(i); // Remove crate
            }
        }
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        slingshotTexture.dispose();
        birdTexture.dispose();
        pigTexture.dispose();
        crateTexture.dispose();
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
