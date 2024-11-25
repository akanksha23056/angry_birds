package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

public class HomeScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture homeScreenImage;
    private final Texture playButtonImage;
    private final Texture playButtonHoverImage;
    private final Texture settingsButtonImage;
    private final Texture settingsButtonHoverImage;
    private final Rectangle playButtonBounds;
    private final Rectangle settingsButtonBounds;

    public HomeScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.homeScreenImage = new Texture("homescreen.jpg");
        this.playButtonImage = new Texture("playbutton.png");
        this.playButtonHoverImage = new Texture("playbutton.png"); // Ensure this is the correct hover texture
        this.settingsButtonImage = new Texture("settings.png");
        this.settingsButtonHoverImage = new Texture("settings_hover.png");

        // Play button configuration
        float playButtonWidth = 300.0F;
        float playButtonHeight = 150.0F;
        float playButtonX = (Gdx.graphics.getWidth() - playButtonWidth) / 2.0F;
        float playButtonY = Gdx.graphics.getHeight() / 2.0F - 90;
        this.playButtonBounds = new Rectangle(playButtonX, playButtonY, playButtonWidth, playButtonHeight);

        // Settings button configuration (positioned in the extreme bottom-left corner)
        float settingsButtonWidth = 200.0F;
        float settingsButtonHeight = 200.0F;
        float settingsButtonX = 0.0F;  // Exact left edge
        float settingsButtonY = 0.0F;  // Exact bottom edge
        this.settingsButtonBounds = new Rectangle(settingsButtonX, settingsButtonY, settingsButtonWidth, settingsButtonHeight);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15F, 0.15F, 0.2F, 1.0F);

        batch.begin();
        // Draw background image
        batch.draw(homeScreenImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Play button with hover effect
        drawPlayButton();
        // Settings button with hover effect
        drawSettingsButton();

        batch.end();
    }

    private void drawPlayButton() {
        if (playButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
            batch.draw(playButtonHoverImage, playButtonBounds.x - 5, playButtonBounds.y - 5, playButtonBounds.width + 20, playButtonBounds.height + 20);
            if (Gdx.input.isTouched()) {
                game.playButtonClickSound(); // Play button click sound
                game.backgroundMusic.stop(); // Stop the music when navigating to the next screen
                game.setScreen(new LevelsScreen(game)); // Transition to LevelsScreen
            }
        } else {
            batch.draw(playButtonImage, playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        }
    }

    private void drawSettingsButton() {
        if (settingsButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
            batch.draw(settingsButtonHoverImage, settingsButtonBounds.x - 5, settingsButtonBounds.y - 5, settingsButtonBounds.width + 10, settingsButtonBounds.height + 10);
            if (Gdx.input.isTouched()) {
                game.playButtonClickSound(); // Play button click sound
                game.setScreen(new SettingsScreen(game)); // Transition to SettingsScreen
            }
        } else {
            batch.draw(settingsButtonImage, settingsButtonBounds.x, settingsButtonBounds.y, settingsButtonBounds.width, settingsButtonBounds.height);
        }
    }

    @Override
    public void dispose() {
        homeScreenImage.dispose();
        playButtonImage.dispose();
        playButtonHoverImage.dispose();
        settingsButtonImage.dispose();
        settingsButtonHoverImage.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
