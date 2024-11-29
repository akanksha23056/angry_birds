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

public class Level2GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;

    // Textures
    private final Texture levelImage;
    private final Texture slingshotTexture;
    private final Texture redBirdTexture;
    private final Texture yellowBirdTexture;
    private final Texture pigTexture;
    private final Texture pigHurtTexture;
    private final Texture crateTexture;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;

    // Pause Button
    private final Rectangle pauseButtonBounds;

    // Birds
    private Vector2 redBirdPosition;
    private Vector2 redBirdVelocity;
    private boolean isDraggingRedBird = false;
    private boolean isRedBirdLaunched = false;

    private Vector2 yellowBirdPosition;
    private Vector2 yellowBirdVelocity;
    private boolean isDraggingYellowBird = false;
    private boolean isYellowBirdActive = false;



    // Gravity and damping
    private final Vector2 gravity = new Vector2(0, -0.05f);
    private final float damping = 0.98f;

    // Slingshot properties
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

    // Ground height
    private final float groundY = 130;

    private boolean isPaused = false;

    public Level2GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Load textures
        this.levelImage = new Texture("level2game.jpg");
        this.slingshotTexture = new Texture("sling.png");
        this.redBirdTexture = new Texture("redbird.png");
        this.yellowBirdTexture = new Texture("yellowbird.png");
        this.pigTexture = new Texture("pig.png");
        this.pigHurtTexture = new Texture("pighurt.png");
        this.crateTexture = new Texture("crate.png");
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");

        // Pause Button
        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);

        // Slingshot position
        this.slingshotPosition = new Vector2(200, groundY + 40);

        // Birds' initial positions
        this.redBirdPosition = new Vector2(slingshotPosition.x - 30, slingshotPosition.y);
        this.redBirdVelocity = new Vector2(0, 0);

        this.yellowBirdPosition = new Vector2(slingshotPosition.x - 60, slingshotPosition.y);
        this.yellowBirdVelocity = new Vector2(0, 0);

        // Initialize crates and pigs
        for (int i = 0; i < 3; i++) {
            crates.add(new Crate(new Rectangle(300 + i * 100, groundY, 50, 50))); // X, Y, Width, Height
        }

        for (int i = 0; i < crates.size(); i++) {
            Crate crate = crates.get(i);
            pigs.add(new Pig(new Rectangle(crate.bounds.x, crate.bounds.y + crate.bounds.height, 50, 50)));
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
        // Clear the screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1.0f);

        // Update game state
        if (!isRedBirdLaunched) {
            updateRedBirdPosition();
        } else if (isYellowBirdActive) {
            updateYellowBird();
        }
        updateCrates();
        updatePigs();
        checkCollisions();

        // Draw everything
        batch.begin();
        batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(slingshotTexture, slingshotPosition.x - 25, slingshotPosition.y - 50, 50, 100);

        if (!isRedBirdLaunched) {
            batch.draw(redBirdTexture, redBirdPosition.x - 25, redBirdPosition.y - 25, 50, 50);
        }

        if (isYellowBirdActive || !isRedBirdLaunched) {
            batch.draw(yellowBirdTexture, yellowBirdPosition.x - 25, yellowBirdPosition.y - 25, 50, 50);
        }

        for (Pig pig : pigs) {
            Texture textureToDraw = pig.isHurt ? pigHurtTexture : pigTexture;
            batch.draw(textureToDraw, pig.bounds.x, pig.bounds.y, pig.bounds.width, pig.bounds.height);
        }

        for (Crate crate : crates) {
            batch.draw(crateTexture, crate.bounds.x, crate.bounds.y, crate.bounds.width, crate.bounds.height);
        }

        drawPauseButton();
        batch.end();
    }

    private void updateRedBirdPosition() {
        if (Gdx.input.isTouched()) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

            if (isDraggingRedBird || touchPosition.dst(slingshotPosition) <= slingshotRadius * 100) {
                isDraggingRedBird = true;

                if (touchPosition.dst(slingshotPosition) > slingshotRadius * 100) {
                    redBirdPosition.set(slingshotPosition.cpy().add(touchPosition.sub(slingshotPosition).nor().scl(slingshotRadius * 100)));
                } else {
                    redBirdPosition.set(touchPosition);
                }
            }
        } else if (isDraggingRedBird) {
            isDraggingRedBird = false;

            redBirdVelocity.set(slingshotPosition.cpy().sub(redBirdPosition).scl(0.1f));
        }

        if (!isDraggingRedBird) {
            redBirdVelocity.add(gravity);
            redBirdVelocity.scl(damping);
            redBirdVelocity.add(redBirdVelocity);

            if (redBirdPosition.y < groundY) {
                redBirdPosition.y = groundY;
                redBirdPosition.setZero();
            }
        }
    }

    private void updateYellowBird() {
        handleBirdDraggingAndLaunching(yellowBirdPosition, yellowBirdVelocity, null);
    }

    private void handleBirdDraggingAndLaunching(Vector2 birdPosition, Vector2 birdVelocity, Runnable onLaunch) {
        if (Gdx.input.isTouched()) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

            boolean isDragging = birdPosition.equals(redBirdPosition) ? isDraggingRedBird : isDraggingYellowBird;

            if (isDragging || touchPosition.dst(slingshotPosition) <= slingshotRadius * 100) {
                if (birdPosition.equals(redBirdPosition)) isDraggingRedBird = true;
                if (birdPosition.equals(yellowBirdPosition)) isDraggingYellowBird = true;

                if (touchPosition.dst(slingshotPosition) > slingshotRadius * 100) {
                    birdPosition.set(slingshotPosition.cpy().add(touchPosition.sub(slingshotPosition).nor().scl(slingshotRadius * 100)));
                } else {
                    birdPosition.set(touchPosition);
                }
            }
        } else if (birdPosition.equals(redBirdPosition) ? isDraggingRedBird : isDraggingYellowBird) {
            if (birdPosition.equals(redBirdPosition)) isDraggingRedBird = false;
            if (birdPosition.equals(yellowBirdPosition)) isDraggingYellowBird = false;

            birdVelocity.set(slingshotPosition.cpy().sub(birdPosition).scl(birdPosition.equals(redBirdPosition) ? 0.1f : 0.2f));
            if (onLaunch != null) onLaunch.run();
        }

        if (!(birdPosition.equals(redBirdPosition) ? isDraggingRedBird : isDraggingYellowBird)) {
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

            for (Crate crate : crates) {
                if (crate.bounds.overlaps(new Rectangle(pig.bounds.x, pig.bounds.y - 1, pig.bounds.width, 1))) {
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
            if (!pig.isHurt &&
                (pig.bounds.contains(redBirdPosition) || pig.bounds.contains(yellowBirdPosition.x, yellowBirdPosition.y))) {
                pig.isHurt = true; // Change texture to hurt
            }

        }

        for (Crate crate : crates) {
            if (crate.bounds.contains(redBirdPosition.x, redBirdPosition.y)) {
                crate.velocity.add(redBirdVelocity.cpy().scl(0.5f)); // Apply velocity to the crate
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
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        slingshotTexture.dispose();
        redBirdTexture.dispose();
        yellowBirdTexture.dispose();
        pigTexture.dispose();
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
