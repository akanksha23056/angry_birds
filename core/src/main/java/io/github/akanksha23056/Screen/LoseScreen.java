package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

public class LoseScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture loseTexture;
    private final Texture retryButtonTexture;
    private final Texture retryButtonHoverTexture;
    private final Rectangle retryButtonBounds;
    private final int currentLevel;

    public LoseScreen(Main game, int currentLevel) {
        this.game = game;
        this.batch = game.batch;
        this.loseTexture = new Texture("losescreen.jpg");
        this.retryButtonTexture = new Texture("replay.png");
        this.retryButtonHoverTexture = new Texture("replay_hover.png");
        this.currentLevel = currentLevel;

        // Set button bounds
        float buttonWidth = 180f, buttonHeight = 180f;
        float centerX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float lowerY = Gdx.graphics.getHeight() / 4;

        retryButtonBounds = new Rectangle(centerX, lowerY, buttonWidth, buttonHeight);
    }

    @Override
    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        batch.draw(loseTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw retry button with hover effects
        drawButton(retryButtonBounds, retryButtonTexture, retryButtonHoverTexture, this::handleRetry);

        batch.end();
    }

    private void drawButton(Rectangle bounds, Texture buttonTexture, Texture hoverTexture, Runnable action) {
        boolean isHovered = bounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        if (isHovered) {
            batch.draw(hoverTexture, bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
            if (Gdx.input.justTouched()) {
                action.run();
            }
        } else {
            batch.draw(buttonTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void handleRetry() {
        switch (currentLevel) {
            case 1:
                game.setScreen(new Level1GameScreen(game, "level1game.jpg"));
                break;
            case 2:
                game.setScreen(new Level2GameScreen(game));
                break;
            // Add more cases if there are more levels
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        loseTexture.dispose();
        retryButtonTexture.dispose();
        retryButtonHoverTexture.dispose();
    }
}
