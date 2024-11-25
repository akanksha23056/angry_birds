package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

public class SettingsScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture settingsBoardImage;
    private final Texture settingsBackImage;
    private final Texture settingsBackHoverImage;
    private final Texture exitImage;
    private final Texture exitHoverImage;
    private final Texture faqImage;
    private final Texture musicImage;
    private final Texture musicHoverImage;
    private final Texture musicMuteImage;
    private final Texture volumeImage;
    private final Texture volumeHoverImage;
    private final Texture volumeMuteImage;

    private final Rectangle settingsBackBounds;
    private final Rectangle exitBounds;
    private final Rectangle faqBounds;
    private final Rectangle musicBounds;
    private final Rectangle volumeBounds;

    public SettingsScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.settingsBoardImage = new Texture("settingsboard.png");
        this.settingsBackImage = new Texture("settingsback.png");
        this.settingsBackHoverImage = new Texture("settingsexit_hover.png");
        this.exitImage = new Texture("exit.png");
        this.exitHoverImage = new Texture("exit.png");
        this.faqImage = new Texture("faq.png");
        this.musicImage = new Texture("music.png");
        this.musicHoverImage = new Texture("music_hover.png");
        this.musicMuteImage = new Texture("musicmute.png");
        this.volumeImage = new Texture("volume.png");
        this.volumeHoverImage = new Texture("volume_hover.png");
        this.volumeMuteImage = new Texture("volumemute.png");

        float boardWidth = 500f;
        float boardHeight = 300f;
        float boardX = (Gdx.graphics.getWidth() - boardWidth) / 2;
        float boardY = (Gdx.graphics.getHeight() - boardHeight) / 2;

        // Button dimensions
        float largeButtonWidth = 130f;  // FAQ, Music, Volume
        float largeButtonHeight = 130f;

        // Button positions
        settingsBackBounds = new Rectangle(boardX - 60, boardY + boardHeight - 80, 100f, 100f);  // Adjusted position for Settings Exit button
        faqBounds = new Rectangle(boardX + 30, boardY + boardHeight / 2 - 40, largeButtonWidth, largeButtonHeight);
        musicBounds = new Rectangle(boardX + 180, boardY + boardHeight / 2 - 40, largeButtonWidth, largeButtonHeight);
        volumeBounds = new Rectangle(boardX + 330, boardY + boardHeight / 2 - 40, largeButtonWidth, largeButtonHeight);

        // Enlarged and moved up Exit button dimensions and position
        exitBounds = new Rectangle(boardX + 140, boardY + 30, largeButtonWidth * 1.5f, largeButtonHeight * 0.9f);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1.0f);
        batch.begin();

        // Draw settings board background
        batch.draw(settingsBoardImage,
            (float) (Gdx.graphics.getWidth() - settingsBoardImage.getWidth()) / 2,
            (float) (Gdx.graphics.getHeight() - settingsBoardImage.getHeight()) / 2);

        // Settings Back button with hover effect
        handleButton(settingsBackBounds, settingsBackImage, settingsBackHoverImage, () -> game.setScreen(new HomeScreen(game)));

        // FAQ button (static, no click action)
        batch.draw(faqImage, faqBounds.x, faqBounds.y, faqBounds.width, faqBounds.height);

        // Music button with hover and mute functionality
        handleToggleButton(musicBounds, game.musicMuted ? musicMuteImage : musicImage, musicHoverImage, () -> {
            game.musicMuted = !game.musicMuted;
            if (game.musicMuted) {
                game.muteMusic();
            } else {
                game.unmuteMusic();
            }
        });

        // Volume button with hover to mute/unmute button click sound
        handleToggleButton(volumeBounds, game.volumeMuted ? volumeMuteImage : volumeImage, volumeHoverImage, () -> {
            game.volumeMuted = !game.volumeMuted; // Toggle volume muted state
        });

        // Exit button with hover effect and sound
        handleButton(exitBounds, exitImage, exitHoverImage, () -> game.setScreen(new AfterExitScreen(game)));

        batch.end();
    }

    private void handleButton(Rectangle bounds, Texture normal, Texture hover, Runnable action) {
        if (isCursorNear(bounds)) {
            batch.draw(hover, bounds.x - 2.0F, bounds.y - 2.0F, bounds.width + 4.0F, bounds.height + 4.0F);
            if (Gdx.input.justTouched()) {
                game.playButtonClickSound(); // Play button click sound if not muted
                action.run();
            }
        } else {
            batch.draw(normal, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void handleToggleButton(Rectangle bounds, Texture normal, Texture hover, Runnable action) {
        if (isCursorNear(bounds)) {
            batch.draw(hover, bounds.x - 2.0F, bounds.y - 2.0F, bounds.width + 4.0F, bounds.height + 4.0F);
            if (Gdx.input.justTouched()) {
                game.playButtonClickSound(); // Play button click sound if not muted
                action.run();
            }
        } else {
            batch.draw(normal, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private boolean isCursorNear(Rectangle bounds) {
        float cursorX = Gdx.input.getX();
        float cursorY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float tolerance = 10.0f;
        return bounds.contains(cursorX, cursorY) ||
            bounds.contains(cursorX + tolerance, cursorY + tolerance) ||
            bounds.contains(cursorX - tolerance, cursorY - tolerance);
    }

    @Override
    public void dispose() {
        settingsBoardImage.dispose();
        settingsBackImage.dispose();
        settingsBackHoverImage.dispose();
        exitImage.dispose();
        exitHoverImage.dispose();
        faqImage.dispose();
        musicImage.dispose();
        musicHoverImage.dispose();
        musicMuteImage.dispose();
        volumeImage.dispose();
        volumeHoverImage.dispose();
        volumeMuteImage.dispose();
    }

    // Empty lifecycle methods
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
