package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

public class WinScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture winTexture;
    private final Texture menuButtonTexture;
    private final Texture menuButtonHoverTexture;
    private final Texture playNextButtonTexture;
    private final Texture playNextButtonHoverTexture;
    private final Texture replayButtonTexture;
    private final Texture replayButtonHoverTexture;
    private final Rectangle menuButtonBounds;
    private final Rectangle playNextButtonBounds;
    private final Rectangle replayButtonBounds;
    private final int currentLevel;

    public WinScreen(Main game, int currentLevel) {
        this.game = game;
        this.batch = game.batch;
        this.winTexture = new Texture("winscreen.jpg");
        this.menuButtonTexture = new Texture("menu.png");
        this.menuButtonHoverTexture = new Texture("menu_hover.png");
        this.playNextButtonTexture = new Texture("playnext.png");
        this.playNextButtonHoverTexture = new Texture("playnext_hover.png");
        this.replayButtonTexture = new Texture("replay.png");
        this.replayButtonHoverTexture = new Texture("replay_hover.png");
        this.currentLevel = currentLevel;

        // Set button sizes and positions
        float buttonWidth = 180f, buttonHeight = 180f;
        float centerX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float lowerY = Gdx.graphics.getHeight() / 4;

        menuButtonBounds = new Rectangle(centerX - 200, lowerY, buttonWidth, buttonHeight);
        playNextButtonBounds = new Rectangle(centerX, lowerY, buttonWidth, buttonHeight);
        replayButtonBounds = new Rectangle(centerX + 200, lowerY, buttonWidth, buttonHeight);
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
        batch.draw(winTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw buttons with hover effects
        drawButton(menuButtonBounds, menuButtonTexture, menuButtonHoverTexture, () -> game.setScreen(game.getLevelsScreen()));
        drawButton(playNextButtonBounds, playNextButtonTexture, playNextButtonHoverTexture, this::handlePlayNext);
        drawButton(replayButtonBounds, replayButtonTexture, replayButtonHoverTexture, this::handleReplay);

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

    private void handlePlayNext() {
        switch (currentLevel) {
            case 1:
                game.setScreen(new Level2GameScreen(game));
                break;
            case 2:
                game.setScreen(new Level3GameScreen(game));
                break;
            // Add more cases if there are more levels
        }
    }

    private void handleReplay() {
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
        winTexture.dispose();
        menuButtonTexture.dispose();
        menuButtonHoverTexture.dispose();
        playNextButtonTexture.dispose();
        playNextButtonHoverTexture.dispose();
        replayButtonTexture.dispose();
        replayButtonHoverTexture.dispose();
    }
}
