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
    private final Texture pauseBackground;
    private final Texture replayButton, replayHoverButton;
    private final Texture playButton, playHoverButton;
    private final Texture menuButton, menuHoverButton;
    private final Rectangle replayBounds, playBounds, menuBounds;

    public PauseScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.pauseBackground = new Texture("pausescreen.jpg");
        this.replayButton = new Texture("replay.png");
        this.replayHoverButton = new Texture("replay_hover.png");
        this.playButton = new Texture("play.png");
        this.playHoverButton = new Texture("play_hover.png");
        this.menuButton = new Texture("menu.png");
        this.menuHoverButton = new Texture("menu_hover.png");

        // Set button sizes and new lower positions
        float buttonWidth = 180f, buttonHeight = 180f; // Increased size
        float centerX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float lowerY = Gdx.graphics.getHeight() / 4; // Positioned significantly lower

        replayBounds = new Rectangle(centerX - 200, lowerY, buttonWidth, buttonHeight); // Moved left for spacing
        playBounds = new Rectangle(centerX, lowerY, buttonWidth, buttonHeight); // Center button
        menuBounds = new Rectangle(centerX + 200, lowerY, buttonWidth, buttonHeight); // Moved right for spacing
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        batch.draw(pauseBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw buttons with hover effect and actions
        drawButton(replayBounds, replayButton, replayHoverButton, () -> {
            game.buttonClickSound.play();
            game.setScreen(new LevelGameScreen(game, "levelgame.jpg")); // Transition to LevelGameScreen
        });

        drawButton(playBounds, playButton, playHoverButton, () -> {
            game.buttonClickSound.play();
            game.setScreen(new LevelGameScreen(game, "levelgame.jpg")); // Transition to LevelGameScreen
        });

        drawButton(menuBounds, menuButton, menuHoverButton, () -> {
            game.buttonClickSound.play();
            game.setScreen(new LevelsScreen(game)); // Transition to LevelsScreen
        });

        batch.end();
    }

    private void drawButton(Rectangle bounds, Texture buttonTexture, Texture hoverTexture, Runnable action) {
        boolean isHovered = bounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        // Draw the button with hover effect
        if (isHovered) {
            batch.draw(hoverTexture, bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
            if (Gdx.input.justTouched()) {
                action.run(); // Execute the action if button is clicked
            }
        } else {
            batch.draw(buttonTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
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
