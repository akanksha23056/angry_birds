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

    public LoseScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.loseTexture = new Texture("losescreen.jpg");
        this.retryButtonTexture = new Texture("replay.png");
        this.retryButtonHoverTexture = new Texture("replay_hover.png");

        // Set button bounds
        float buttonWidth = 200f, buttonHeight = 80f;
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
        drawButton(retryButtonBounds, retryButtonTexture, retryButtonHoverTexture, () -> game.setScreen(new Level1GameScreen(game, "level1game.jpg")));

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
