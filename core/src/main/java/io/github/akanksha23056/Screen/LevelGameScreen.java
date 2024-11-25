package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

public class LevelGameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture levelImage;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;
    private final Rectangle pauseButtonBounds;

    public LevelGameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch;
        this.levelImage = new Texture(levelImagePath);
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");
        this.pauseButtonBounds = new Rectangle(10.0F, Gdx.graphics.getHeight() - 110.0F, 100.0F, 100.0F);
    }

    @Override
    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15F, 0.15F, 0.2F, 1.0F);
        batch.begin();
        batch.draw(levelImage, 0.0F, 0.0F, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        drawPauseButton();
        batch.end();
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
