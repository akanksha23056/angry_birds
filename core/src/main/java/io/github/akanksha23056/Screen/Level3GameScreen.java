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

public class Level3GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;

    // Textures
    private final Texture levelImage;
    private final Texture slingshotTexture;
    private final Texture birdTexture;
    private final Texture pigTexture;
    private final Texture pigHurtTexture;
    private final Texture crateTexture;
    private final Texture glassTexture;
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

    // Pigs, Crates, and Glass Slabs
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

    private static class Glass {
        Rectangle bounds;
        Vector2 velocity;

        Glass(Rectangle bounds) {
            this.bounds = bounds;
            this.velocity = new Vector2(0, 0);
        }
    }

    private final ArrayList<Pig> pigs = new ArrayList<>();
    private final ArrayList<Crate> crates = new ArrayList<>();
    private final ArrayList<Glass> glassSlabs = new ArrayList<>();

    // Ground height (customizable for Level 3)
    private final float groundY = 100;

    // Pause state
    private boolean isPaused = false;

    public Level3GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Load textures
        this.levelImage = new Texture("level3game.png");
        this.slingshotTexture = new Texture("sling.png");
        this.birdTexture = new Texture("redbird.png");
        this.pigTexture = new Texture("pig.png");
        this.pigHurtTexture = new Texture("pighurt.png");
        this.crateTexture = new Texture("crate.png");
        this.glassTexture = new Texture("glass.png");
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");

        // Pause Button
        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);

        // Slingshot position (aligned with the new ground level)
        this.slingshotPosition = new Vector2(200, groundY + 40);

        // Bird properties (adjusted to be behind the sling)
        this.birdPosition = new Vector2(slingshotPosition.x - 30, slingshotPosition.y);
        this.birdVelocity = new Vector2(0, 0);
        this.isDragging = false;

        // Initialize crates
        for (int i = 0; i < 3; i++) {
            crates.add(new Crate(new Rectangle(350 + i * 100, groundY, 50, 50))); // Ground level crates
        }

        // Initialize vertical glass slabs (positioned above crates)
        for (Crate crate : crates) {
            glassSlabs.add(new Glass(new Rectangle(crate.bounds.x + 15, crate.bounds.y + crate.bounds.height, 20, 100))); // Thin vertical slabs
        }

        // Initialize pigs (positioned above vertical glass slabs)
        for (Glass glass : glassSlabs) {
            pigs.add(new Pig(new Rectangle(glass.bounds.x - 15, glass.bounds.y + glass.bounds.height, 50, 50))); // Pigs on top
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
        if (isPaused) {
            return; // Skip rendering while paused
        }

        // Clear screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1.0f);

        // Update game mechanics
        updateBirdPosition();
        updateCrates();
        updateGlassSlabs();
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

        // Draw glass slabs
        for (Glass glass : glassSlabs) {
            batch.draw(glassTexture, glass.bounds.x, glass.bounds.y, glass.bounds.width, glass.bounds.height);
        }

        // Draw the pause button
        drawPauseButton();

        batch.end();
    }

    private void updateGlassSlabs() {
        for (Glass glass : glassSlabs) {
            glass.velocity.add(gravity);
            glass.bounds.x += glass.velocity.x;
            glass.bounds.y += glass.velocity.y;

            if (glass.bounds.y < groundY + crates.get(0).bounds.height) { // Stop at crate height
                glass.bounds.y = groundY + crates.get(0).bounds.height;
                glass.velocity.y = 0;
            }
        }
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
    }

    private void updateCrates() {
        for (Crate crate : crates) {
            crate.velocity.add(gravity);
            crate.bounds.x += crate.velocity.x;
            crate.bounds.y += crate.velocity.y;

            if (crate.bounds.y < groundY) {
                crate.bounds.y = groundY;
                crate.velocity.y = 0;
            }
        }
    }

    private void updatePigs() {
        for (Pig pig : pigs) {
            boolean isSupported = false;

            for (Glass glass : glassSlabs) {
                if (glass.bounds.overlaps(new Rectangle(pig.bounds.x, pig.bounds.y - 1, pig.bounds.width, 1))) {
                    isSupported = true;
                    break;
                }
            }

            if (!isSupported) {
                pig.velocity.add(gravity);
            }

            pig.bounds.x += pig.velocity.x;
            pig.bounds.y += pig.velocity.y;

            if (pig.bounds.y < groundY) {
                pig.bounds.y = groundY;
                pig.velocity.y = 0;
            }
        }
    }

    private void checkCollisions() {
        for (Pig pig : pigs) {
            if (!pig.isHurt && pig.bounds.contains(birdPosition.x, birdPosition.y)) {
                pig.isHurt = true; // Change texture to hurt
            }
        }

        for (Crate crate : crates) {
            if (crate.bounds.contains(birdPosition.x, birdPosition.y)) {
                crate.velocity.add(birdVelocity.cpy().scl(0.5f)); // Apply velocity to the crate
            }
        }

        for (Glass glass : glassSlabs) {
            if (glass.bounds.contains(birdPosition.x, birdPosition.y)) {
                glass.velocity.add(birdVelocity.cpy().scl(0.5f)); // Apply velocity to the glass
            }
        }
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
        glassTexture.dispose();
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
