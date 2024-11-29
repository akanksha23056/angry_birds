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
    private final Texture tntTexture;
    private final Texture tntExplodeTexture;
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

    // Entities
    private static class Entity {
        Rectangle bounds;
        Vector2 velocity;
        boolean isExploded;

        Entity(Rectangle bounds) {
            this.bounds = bounds;
            this.velocity = new Vector2(0, 0);
            this.isExploded = false;
        }
    }

    private final ArrayList<Entity> crates = new ArrayList<>();
    private Entity tntBlock;
    private Entity pig;

    // Ground height
    private final float groundY = 130;

    // Pause state
    private boolean isPaused = false;

    // TNT explosion timing
    private boolean isTNTExploded = false;
    private float explosionTimer = 0f;
    private final float explosionDuration = 1f; // 1 second delay for win screen

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
        this.tntTexture = new Texture("tnt.png");
        this.tntExplodeTexture = new Texture("tntexplode.png");
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");

        // Pause Button
        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);

        // Slingshot position
        this.slingshotPosition = new Vector2(200, groundY + 40);

        // Bird properties
        this.birdPosition = new Vector2(slingshotPosition.x - 30, slingshotPosition.y);
        this.birdVelocity = new Vector2(0, 0);
        this.isDragging = false;

        // Initialize crate
        Entity crate = new Entity(new Rectangle(400, groundY, 50, 50));
        crates.add(crate);

        // Initialize TNT above crate
        tntBlock = new Entity(new Rectangle(crate.bounds.x, crate.bounds.y + crate.bounds.height, 50, 50));

        // Initialize pig above TNT
        pig = new Entity(new Rectangle(tntBlock.bounds.x, tntBlock.bounds.y + tntBlock.bounds.height, 50, 50));
    }

    @Override
    public void render(float delta) {
        if (isPaused) return;

        // Clear screen
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1.0f);

        // Update game mechanics
        updateBirdPosition();
        updateEntities();
        checkCollisions();

        // Handle explosion delay
        if (isTNTExploded) {
            explosionTimer += delta;
            if (explosionTimer >= explosionDuration) {
                game.setScreen(new WinScreen(game, 1)); // Redirect to win screen
                return;
            }
        }

        // Draw everything
        batch.begin();

        // Draw background
        batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw slingshot
        batch.draw(slingshotTexture, slingshotPosition.x - 25, slingshotPosition.y - 50, 50, 100);

        // Draw bird
        batch.draw(birdTexture, birdPosition.x - 25, birdPosition.y - 25, 50, 50);

        // Draw crates
        for (Entity crate : crates) {
            batch.draw(crateTexture, crate.bounds.x, crate.bounds.y, crate.bounds.width, crate.bounds.height);
        }

        // Draw TNT or explosion
        if (tntBlock.isExploded) {
            // Explosion size is 5x TNT size
            float explosionWidth = tntBlock.bounds.width * 5;
            float explosionHeight = tntBlock.bounds.height * 5;

            // Draw explosion centered at the TNT block
            batch.draw(tntExplodeTexture,
                tntBlock.bounds.x - (explosionWidth - tntBlock.bounds.width) / 2, // Center explosion horizontally
                tntBlock.bounds.y - (explosionHeight - tntBlock.bounds.height) / 2, // Center explosion vertically
                explosionWidth,
                explosionHeight);
        } else {
            batch.draw(tntTexture, tntBlock.bounds.x, tntBlock.bounds.y, tntBlock.bounds.width, tntBlock.bounds.height);
        }

        // Draw pig
        if (!isTNTExploded) {
            batch.draw(pig.isExploded ? pigHurtTexture : pigTexture,
                pig.bounds.x, pig.bounds.y, pig.bounds.width, pig.bounds.height);
        }

        // Draw pause button
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

    private void updateEntities() {
        // Update crate
        for (Entity crate : crates) {
            updateEntity(crate);
        }

        // Update TNT
        boolean isTNTSupported = false;
        for (Entity crate : crates) {
            if (crate.bounds.overlaps(new Rectangle(tntBlock.bounds.x, tntBlock.bounds.y - 1, tntBlock.bounds.width, 1))) {
                isTNTSupported = true;
                break;
            }
        }
        if (!isTNTSupported) updateEntity(tntBlock);

        // Update pig
        if (!tntBlock.bounds.overlaps(new Rectangle(pig.bounds.x, pig.bounds.y - 1, pig.bounds.width, 1))) {
            updateEntity(pig);
        }
    }

    private void updateEntity(Entity entity) {
        entity.velocity.add(gravity);
        entity.bounds.x += entity.velocity.x;
        entity.bounds.y += entity.velocity.y;

        if (entity.bounds.y < groundY) {
            entity.bounds.y = groundY;
            entity.velocity.setZero();
        }
    }

    private void checkCollisions() {
        // Handle TNT explosion
        if (!tntBlock.isExploded && tntBlock.bounds.contains(birdPosition.x, birdPosition.y)) {
            tntBlock.isExploded = true;
            isTNTExploded = true; // Start explosion timer
            crates.clear();
            pig.isExploded = true;
        }

        // Handle pig collision
        if (!pig.isExploded && pig.bounds.contains(birdPosition.x, birdPosition.y)) {
            pig.isExploded = true; // Change to hurt texture
            game.setScreen(new WinScreen(game, 1)); // Redirect to win screen
        }

        // Handle crate collision
        for (Entity crate : crates) {
            if (crate.bounds.contains(birdPosition.x, birdPosition.y)) {
                crate.velocity.add(birdVelocity.cpy().scl(0.5f));
            }
        }
    }

    private void drawPauseButton() {
        boolean isHovered = pauseButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        if (isHovered) {
            batch.draw(pauseButtonHoverTexture, pauseButtonBounds.x - 5, pauseButtonBounds.y - 5,
                pauseButtonBounds.width + 10, pauseButtonBounds.height + 10);
            if (Gdx.input.isButtonJustPressed(0)) pauseGame();
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
        birdTexture.dispose();
        pigTexture.dispose();
        pigHurtTexture.dispose();
        crateTexture.dispose();
        tntTexture.dispose();
        tntExplodeTexture.dispose();
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
