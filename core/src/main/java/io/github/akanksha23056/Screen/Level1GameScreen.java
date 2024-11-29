package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

import java.util.ArrayList;

public class Level1GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;

    // Textures
    private final Texture levelImage;
    private final Texture slingshotTexture;
    private final Texture birdTexture;
    private final Texture pigTexture;
    private final Texture pigHurtTexture;
    private final Texture crateTexture;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;

    // Pause Button
    private final Rectangle pauseButtonBounds;

    // Bird properties
    private Vector2 birdPosition;
    private Vector2 birdVelocity;
    private boolean isDragging;

    // Gravity and damping
    private final Vector2 gravity = new Vector2(0, -0.05f);
    private final float damping = 0.98f;

    // Sling properties
    private final Vector2 slingshotPosition;
    private final float slingshotRadius = 1.5f;

    // Pigs and Crates
    private static class Pig {
        Rectangle bounds;
        Vector2 velocity;
        boolean isHurt;

        Pig(Rectangle bounds) {
            this.bounds = bounds;
            this.velocity = new Vector2(0, 0);
            this.isHurt = false;
        }
    }

    private static class Crate {
        Rectangle bounds;
        Vector2 velocity;

        Crate(Rectangle bounds) {
            this.bounds = bounds;
            this.velocity = new Vector2(0, 0);
        }
    }

    private final ArrayList<Pig> pigs = new ArrayList<>();
    private final ArrayList<Crate> crates = new ArrayList<>();

    // Ground height (lowered slightly)
    private final float groundY = 130;

    // Pause state
    private boolean isPaused = false;

    // Try counter
    private int tryCounter = 0;
    private final int maxTries = 3;

    public Level1GameScreen(Main game, String image) {
        this.game = game;
        this.batch = game.batch;

        // Load textures
        this.levelImage = new Texture("level1game.jpg");
        this.slingshotTexture = new Texture("sling.png");
        this.birdTexture = new Texture("redbird.png");
        this.pigTexture = new Texture("pig.png");
        this.pigHurtTexture = new Texture("pighurt.png");
        this.crateTexture = new Texture("crate.png");
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");

        // Pause Button
        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);

        // Slingshot position (lowered slightly)
        this.slingshotPosition = new Vector2(200, groundY + 40);

        // Bird properties (adjusted to be behind the lowered sling)
        this.birdPosition = new Vector2(slingshotPosition.x - 30, slingshotPosition.y);
        this.birdVelocity = new Vector2(0, 0);
        this.isDragging = false;

        // Initialize one crate (lowered slightly)
        // Initialize one crate
        Crate crate = new Crate(new Rectangle(400, groundY, 50, 50)); // Place crate slightly higher
        crates.add(crate);

// Initialize one pig (placed above the crate)
        pigs.add(new Pig(new Rectangle(crate.bounds.x, crate.bounds.y + crate.bounds.height, 50, 50)));
        // X, Y, Width, Height

        // Initialize one pig (positioned above the lowered crate)
        pigs.add(new Pig(new Rectangle(crate.bounds.x, crate.bounds.y + crate.bounds.height, 50, 50)));
    }

    @Override
    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        if (isPaused) {
            return; // Skip rendering while paused
        }

        // Clear screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1.0f);

        // Update game mechanics
        updateBirdPosition();
        updateCrates();
        updatePigs();
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
        for (Pig pig : pigs) {
            Texture textureToDraw = pig.isHurt ? pigHurtTexture : pigTexture;
            batch.draw(textureToDraw, pig.bounds.x, pig.bounds.y, pig.bounds.width, pig.bounds.height);
        }

        // Draw crates
        for (Crate crate : crates) {
            batch.draw(crateTexture, crate.bounds.x, crate.bounds.y, crate.bounds.width, crate.bounds.height);
        }

        // Draw the pause button
        drawPauseButton();

        batch.end();
    }

    private void updateBirdPosition() {
        if (Gdx.input.isTouched()) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

            if (isDragging || touchPosition.dst(slingshotPosition) <= slingshotRadius * 100) {
                isDragging = true;

                if (touchPosition.dst(slingshotPosition) > slingshotRadius * 100) {
                    birdPosition.set(slingshotPosition.cpy().add(touchPosition.sub(slingshotPosition).nor().scl(slingshotRadius * 100)));
                } else {
                    birdPosition.set(touchPosition);
                }
            }
        } else if (isDragging) {
            isDragging = false;
            tryCounter++; // Increment try counter

            birdVelocity.set(slingshotPosition.cpy().sub(birdPosition).scl(0.1f));
        }

        if (!isDragging) {
            birdVelocity.add(gravity);
            birdVelocity.scl(damping);
            birdPosition.add(birdVelocity);

            if (birdPosition.y < groundY) {
                birdPosition.y = groundY;
                birdVelocity.setZero();
            }
        }

        // Check if max tries exceeded
        if (tryCounter >= maxTries) {
            game.setScreen(new LoseScreen(game,1));
        }
    }

    private void updateCrates() {
        for (Crate crate : crates) {
            crate.velocity.add(gravity);
            crate.bounds.x += crate.velocity.x;
            crate.bounds.y += crate.velocity.y;

            if (crate.bounds.y < groundY) { // Match the raised position
                crate.bounds.y = groundY;
                crate.velocity.y = 0;
            }
        }
    }

    private void updatePigs() {
        for (Pig pig : pigs) {
            boolean isSupported = false;

            for (Crate crate : crates) {
                // Check if the pig is directly above a crate
                if (crate.bounds.overlaps(new Rectangle(pig.bounds.x, pig.bounds.y - 1, pig.bounds.width, 1))) {
                    isSupported = true;
                    break;
                }
            }

            if (!isSupported) {
                pig.velocity.add(gravity); // Apply gravity if not supported
            }

            pig.bounds.x += pig.velocity.x; // Update horizontal movement
            pig.bounds.y += pig.velocity.y; // Update vertical movement

            // Check if pig has reached the ground
            if (pig.bounds.y < groundY) {
                pig.bounds.y = groundY;
                pig.velocity.y = 0; // Stop vertical movement
            }
        }
    }


    private void checkCollisions() {
        for (Pig pig : pigs) {
            if (!pig.isHurt && pig.bounds.contains(birdPosition.x, birdPosition.y)) {
                pig.isHurt = true; // Change texture to hurt
                unlockLevel2AndRedirect();
            }
        }

        for (Crate crate : crates) {
            if (crate.bounds.contains(birdPosition.x, birdPosition.y)) {
                crate.velocity.add(birdVelocity.cpy().scl(0.5f)); // Apply velocity to the crate
            }
        }
    }

    private void unlockLevel2AndRedirect() {
        // Unlock level 2

        // Redirect to win screen
        game.setScreen(new WinScreen(game,1));
    }


    private void drawPauseButton() {
        boolean isHovered = pauseButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        if (isHovered) {
            batch.draw(pauseButtonHoverTexture, pauseButtonBounds.x - 5, pauseButtonBounds.y - 5,
                pauseButtonBounds.width + 10, pauseButtonBounds.height + 10);
            if (Gdx.input.isButtonJustPressed(0)) {
                pauseGame();
            }
        } else {
            batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y,
                pauseButtonBounds.width, pauseButtonBounds.height);
        }
    }

    private void pauseGame() {
        game.setScreen(new PauseScreen(game, this));
        // Transition to pause menu
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        slingshotTexture.dispose();
        birdTexture.dispose();
        pigTexture.dispose();
        pigHurtTexture.dispose();
        crateTexture.dispose();
        pauseButtonTexture.dispose();
        pauseButtonHoverTexture.dispose();
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
