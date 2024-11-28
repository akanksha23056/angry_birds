package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

public class PauseScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Screen currentLevel; // Reference to the current level

    private final Texture pauseBackground;
    private final Texture replayButton, replayHoverButton;
    private final Texture playButton, playHoverButton;
    private final Texture menuButton, menuHoverButton;
    private final Rectangle replayBounds, playBounds, menuBounds;

    public PauseScreen(Main game, Screen currentLevel) {
        this.game = game;
        this.batch = game.batch;
        this.currentLevel = currentLevel; // Store the reference to the current level

        this.pauseBackground = new Texture("pausescreen.jpg");
        this.replayButton = new Texture("replay.png");
        this.replayHoverButton = new Texture("replay_hover.png");
        this.playButton = new Texture("play.png");
        this.playHoverButton = new Texture("play_hover.png");
        this.menuButton = new Texture("menu.png");
        this.menuHoverButton = new Texture("menu_hover.png");

        // Set button sizes and positions
        float buttonWidth = 180f, buttonHeight = 180f;
        float centerX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float lowerY = Gdx.graphics.getHeight() / 4;

        replayBounds = new Rectangle(centerX - 200, lowerY, buttonWidth, buttonHeight);
        playBounds = new Rectangle(centerX, lowerY, buttonWidth, buttonHeight);
        menuBounds = new Rectangle(centerX + 200, lowerY, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        batch.draw(pauseBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw buttons with hover effects
        drawButton(replayBounds, replayButton, replayHoverButton, () -> {
            game.buttonClickSound.play();
            restartLevel(); // Restart the current level
        });

        drawButton(playBounds, playButton, playHoverButton, () -> {
            game.buttonClickSound.play();
            game.setScreen(currentLevel); // Resume the current level
        });

        drawButton(menuBounds, menuButton, menuHoverButton, () -> {
            game.buttonClickSound.play();
            game.setScreen(new LevelsScreen(game)); // Go back to LevelsScreen
        });

        batch.end();
    }

    private void drawButton(Rectangle bounds, Texture buttonTexture, Texture hoverTexture, Runnable action) {
        boolean isHovered = bounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        if (isHovered) {
            batch.draw(hoverTexture, bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
            if (Gdx.input.justTouched()) {
                action.run(); // Perform the action on click
            }
        } else {
            batch.draw(buttonTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void restartLevel() {
        // Identify the type of the current level and restart accordingly
        if (currentLevel instanceof Level2GameScreen) {
            game.setScreen(new Level2GameScreen(game)); // Restart Level2GameScreen
        }
        // Add similar conditions for other levels if necessary
    }

    @Override
    public void dispose() {
        pauseBackground.dispose();
        replayButton.dispose();
        replayHoverButton.dispose();
        playButton.dispose();
        playHoverButton.dispose();
        menuButton.dispose();
        menuHoverButton.dispose();
    }

    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
