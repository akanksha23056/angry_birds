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
import java.util.Iterator;

public class Level3GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;


    // Textures
    private final Texture levelImage;
    private final Texture slingshotTexture;
    private final Texture redBirdTexture;
    private final Texture yellowBirdTexture;
    private final Texture blackBirdTexture;
    private final Texture blackExplodeTexture;
    private final Texture pigTexture;
    private final Texture pigHurtTexture;
    private final Texture crateTexture;
    private final Texture glassTexture;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;

    // Pause Button
    private final Rectangle pauseButtonBounds;

    // Bird properties
    private enum BirdType { RED, YELLOW, BLACK }

    private BirdType currentBirdType = BirdType.RED;
    private Vector2 redBirdPosition, yellowBirdPosition, blackBirdPosition;
    private Vector2 birdVelocity;
    private boolean isDragging;
    private boolean isBirdLaunched = false;
    private boolean isBlackBirdExploded = false;

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
        float rotationAngle; // New: Simulates the rotation

        Glass(Rectangle bounds) {
            this.bounds = bounds;
            this.velocity = new Vector2(0, 0);
            this.rotationAngle = 0; // Initially upright
        }
    }


    private final ArrayList<Pig> pigs = new ArrayList<>();
    private final ArrayList<Crate> crates = new ArrayList<>();
    private final ArrayList<Glass> glassSlabs = new ArrayList<>();

    // Ground height
    private final float groundY = 100;

    // Pause state
    private boolean isPaused = false;

    public Level3GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Load textures
        this.levelImage = new Texture("level3game.png");
        this.slingshotTexture = new Texture("sling.png");
        this.redBirdTexture = new Texture("redbird.png");
        this.yellowBirdTexture = new Texture("yellowbird.png");
        this.blackBirdTexture = new Texture("blackbird.png");
        this.blackExplodeTexture = new Texture("blackexplode.png");
        this.pigTexture = new Texture("pig.png");
        this.pigHurtTexture = new Texture("pighurt.png");
        this.crateTexture = new Texture("crate.png");
        this.glassTexture = new Texture("glass.png");
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");

        // Pause Button
        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);

        // Slingshot position
        this.slingshotPosition = new Vector2(200, groundY + 40);

        // Bird positions
        this.redBirdPosition = new Vector2(slingshotPosition.x - 30, slingshotPosition.y);
        this.yellowBirdPosition = new Vector2(slingshotPosition.x - 50, slingshotPosition.y);
        this.blackBirdPosition = new Vector2(slingshotPosition.x - 70, slingshotPosition.y);
        this.birdVelocity = new Vector2(0, 0);
        this.isDragging = false;

        // Initialize crates
        for (int i = 0; i < 3; i++) {
            crates.add(new Crate(new Rectangle(300 + i * 100, groundY, 50, 50)));
        }

        // Initialize glass slabs
        for (Crate crate : crates) {
            glassSlabs.add(new Glass(new Rectangle(crate.bounds.x + 15, crate.bounds.y + crate.bounds.height, 20, 100)));
        }

        // Initialize pigs
        for (Glass glass : glassSlabs) {
            pigs.add(new Pig(new Rectangle(glass.bounds.x - 15, glass.bounds.y + glass.bounds.height, 50, 50)));
        }
    }

    @Override
    public void render(float delta) {
        if (isPaused) return;

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

        // Draw birds
        if (!isBirdLaunched || currentBirdType == BirdType.RED) {
            batch.draw(redBirdTexture, redBirdPosition.x - 25, redBirdPosition.y - 25, 50, 50);
        }
        if (!isBirdLaunched || currentBirdType == BirdType.YELLOW) {
            batch.draw(yellowBirdTexture, yellowBirdPosition.x - 25, yellowBirdPosition.y - 25, 50, 50);
        }
        if (!isBlackBirdExploded) {
            batch.draw(blackBirdTexture, blackBirdPosition.x - 25, blackBirdPosition.y - 25, 50, 50);
        } else {
            batch.draw(blackExplodeTexture, blackBirdPosition.x - 50, blackBirdPosition.y - 50, 100, 100);
        }

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
        // Draw glass slabs with rotation
        for (Glass glass : glassSlabs) {
            batch.draw(glassTexture,
                glass.bounds.x, glass.bounds.y,
                glass.bounds.width / 2, glass.bounds.height / 2, // Origin for rotation
                glass.bounds.width, glass.bounds.height,
                1, 1, // Scale
                glass.rotationAngle, // Rotation angle
                0, 0, // Texture origin
                glassTexture.getWidth(), glassTexture.getHeight(),
                false, false);
        }


        // Draw pause button
        drawPauseButton();

        batch.end();
    }

    private void updateBirdPosition() {
        Vector2 currentBirdPosition;
        float speedMultiplier;
        switch (currentBirdType) {
            case RED:
                currentBirdPosition = redBirdPosition;
                speedMultiplier = 1.0f; // Normal speed for red bird
                break;
            case YELLOW:
                currentBirdPosition = yellowBirdPosition;
                speedMultiplier = 1.5f; // Faster speed for yellow bird
                break;
            case BLACK:
                currentBirdPosition = blackBirdPosition;
                speedMultiplier = 1.5f; // Same speed as yellow bird
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentBirdType);
        }

        if (isBirdLaunched) {
            birdVelocity.add(gravity);
            birdVelocity.scl(damping);
            currentBirdPosition.add(birdVelocity);

            if (currentBirdPosition.y <= groundY) {
                currentBirdPosition.y = groundY;
                birdVelocity.setZero();

                if (currentBirdType == BirdType.BLACK && !isBlackBirdExploded) {
                    isBlackBirdExploded = true;
                    handleExplosion();
                } else if (currentBirdType == BirdType.RED) {
                    currentBirdType = BirdType.YELLOW;
                    isBirdLaunched = false;
                } else if (currentBirdType == BirdType.YELLOW) {
                    currentBirdType = BirdType.BLACK;
                    isBirdLaunched = false;
                }
            }
        } else if (Gdx.input.isTouched()) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

            if (isDragging || touchPosition.dst(slingshotPosition) <= slingshotRadius * 100) {
                isDragging = true;

                if (touchPosition.dst(slingshotPosition) > slingshotRadius * 100) {
                    currentBirdPosition.set(slingshotPosition.cpy().add(touchPosition.sub(slingshotPosition).nor().scl(slingshotRadius * 100)));
                } else {
                    currentBirdPosition.set(touchPosition);
                }
            }
        } else if (isDragging) {
            isDragging = false;
            isBirdLaunched = true;
            birdVelocity.set(slingshotPosition.cpy().sub(currentBirdPosition).scl(0.15f * speedMultiplier));
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

    private void updateGlassSlabs() {
        for (Glass glass : glassSlabs) {
            boolean isSupported = false;

            // Check if the glass is supported by any crate
            for (Crate crate : crates) {
                if (crate.bounds.overlaps(new Rectangle(glass.bounds.x, glass.bounds.y - 1, glass.bounds.width, 1))) {
                    isSupported = true;
                    break;
                }
            }

            if (!isSupported) {
                glass.velocity.add(gravity); // Apply gravity if unsupported
            }

            // Simulate tipping rotation
            if (glass.rotationAngle > 0) {
                glass.rotationAngle += 1.0f; // Increment tipping angle
                if (glass.rotationAngle > 90) { // Fully tipped
                    glass.rotationAngle = 90;
                }

                // Apply horizontal displacement to simulate tipping
                glass.bounds.x += 2.0f * Math.signum(glass.velocity.x);
            }

            glass.bounds.x += glass.velocity.x;
            glass.bounds.y += glass.velocity.y;

            if (glass.bounds.y < groundY) {
                glass.bounds.y = groundY; // Stop at the ground
                glass.velocity.setZero();
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
        Vector2 currentBirdPosition;
        switch (currentBirdType) {
            case RED:
                currentBirdPosition = redBirdPosition;
                break;
            case YELLOW:
                currentBirdPosition = yellowBirdPosition;
                break;
            case BLACK:
                currentBirdPosition = blackBirdPosition;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentBirdType);
        }

        for (Pig pig : pigs) {
            if (!pig.isHurt && currentBirdPosition.dst(pig.bounds.x + 25, pig.bounds.y + 25) < 25) {
                pig.isHurt = true;
            }
        }

        for (Crate crate : crates) {
            if (currentBirdPosition.dst(crate.bounds.x + 25, crate.bounds.y + 25) < 25) {
                crate.velocity.add(birdVelocity.cpy().scl(0.5f));
            }
        }

        for (Glass glass : glassSlabs) {
            if (currentBirdPosition.dst(glass.bounds.x + 10, glass.bounds.y + 50) < 25) {
                glass.velocity.add(birdVelocity.cpy().scl(0.5f));
                glass.rotationAngle = 5; // Initiate tipping
            }
        }
    }


    private void handleExplosion() {
        Iterator<Pig> pigIterator = pigs.iterator();
        while (pigIterator.hasNext()) {
            Pig pig = pigIterator.next();
            if (blackBirdPosition.dst(pig.bounds.x + 25, pig.bounds.y + 25) < 100) {
                pigIterator.remove();
            }
        }

        Iterator<Crate> crateIterator = crates.iterator();
        while (crateIterator.hasNext()) {
            Crate crate = crateIterator.next();
            if (blackBirdPosition.dst(crate.bounds.x + 25, crate.bounds.y + 25) < 100) {
                crateIterator.remove();
            }
        }

        Iterator<Glass> glassIterator = glassSlabs.iterator();
        while (glassIterator.hasNext()) {
            Glass glass = glassIterator.next();
            if (blackBirdPosition.dst(glass.bounds.x + 10, glass.bounds.y + 50) < 100) {
                glassIterator.remove();
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
    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        slingshotTexture.dispose();
        redBirdTexture.dispose();
        yellowBirdTexture.dispose();
        blackBirdTexture.dispose();
        blackExplodeTexture.dispose();
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
