package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

import java.util.Random;

public class LevelsScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture levelsImage;
    private final Texture level1Texture;
    private final Texture level1HoverTexture;
    private final Texture level2Texture;
    private final Texture level2HoverTexture;
    private final Texture level3Texture;
    private final Texture level3HoverTexture;
    private final Texture backButtonTexture;
    private final Texture backButtonHoverTexture;
    private final Texture randomButtonTexture;
    private final Texture randomButtonHoverTexture;

    private final Rectangle level1Bounds;
    private final Rectangle level2Bounds;
    private final Rectangle level3Bounds;
    private final Rectangle backButtonBounds;
    private final Rectangle randomButtonBounds;

    public LevelsScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.levelsImage = new Texture("levels.jpg");
        this.level1Texture = new Texture("level1.png");
        this.level1HoverTexture = new Texture("level1.png");
        this.level2Texture = new Texture("level2.png");
        this.level2HoverTexture = new Texture("level2.png");
        this.level3Texture = new Texture("level3.png");
        this.level3HoverTexture = new Texture("level3.png");
        this.backButtonTexture = new Texture("back.png");
        this.backButtonHoverTexture = new Texture("back_hover.png");
        this.randomButtonTexture = new Texture("special.png");
        this.randomButtonHoverTexture = new Texture("special_hover.png");

        // Initialize button bounds
        float buttonWidth = 250.0F;
        float buttonHeight = 250.0F;

        this.level1Bounds = new Rectangle(
            ((float) Gdx.graphics.getWidth() - buttonWidth) / 2.0F,
            (float) Gdx.graphics.getHeight() - buttonHeight - 20.0F,
            buttonWidth, buttonHeight
        );
        this.level2Bounds = new Rectangle(
            this.level1Bounds.x - (buttonWidth / 2.0F + 30.0F),
            this.level1Bounds.y - buttonHeight - 30.0F,
            buttonWidth, buttonHeight
        );
        this.level3Bounds = new Rectangle(
            this.level1Bounds.x + buttonWidth / 2.0F + 30.0F,
            this.level1Bounds.y - buttonHeight - 30.0F,
            buttonWidth, buttonHeight
        );
        this.backButtonBounds = new Rectangle(10.0F, Gdx.graphics.getHeight() - 10 - 100, 100.0F, 100.0F);
        this.randomButtonBounds = new Rectangle(
            ((float) Gdx.graphics.getWidth() - buttonWidth) / 2.0F,
            this.level2Bounds.y - buttonHeight - 40.0F,
            buttonWidth, buttonHeight
        );
    }

    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    public void render(float delta) {
        ScreenUtils.clear(0.15F, 0.15F, 0.2F, 1.0F);
        batch.begin();
        batch.draw(levelsImage, 0.0F, 0.0F, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Render level buttons
        renderLevelButton(level1Bounds, level1Texture, level1HoverTexture, () -> game.setScreen(new Level1GameScreen(game, "level1game.png")));
        renderLevelButton(level2Bounds, level2Texture, level2HoverTexture, () -> game.setScreen(new Level2GameScreen(game)));
        renderLevelButton(level3Bounds, level3Texture, level3HoverTexture, () -> game.setScreen(new Level3GameScreen(game)));

        // Render back button
        handleBackButton();

        // Render random button
        handleRandomButton();

        batch.end();
    }

    private void renderLevelButton(Rectangle bounds, Texture normalTexture, Texture hoverTexture, Runnable onClick) {
        // Draw normal or hover state
        if (bounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
            batch.draw(hoverTexture, bounds.x - 5.0F, bounds.y - 5.0F, bounds.width + 10.0F, bounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                Gdx.app.postRunnable(onClick);
            }
        } else {
            batch.draw(normalTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void handleBackButton() {
        if (backButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
            batch.draw(backButtonHoverTexture, backButtonBounds.x - 5.0F, backButtonBounds.y - 5.0F, backButtonBounds.width + 30.0F, backButtonBounds.height + 30.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                Gdx.app.postRunnable(() -> game.setScreen(new HomeScreen(game)));
            }
        } else {
            batch.draw(backButtonTexture, backButtonBounds.x, backButtonBounds.y, backButtonBounds.width, backButtonBounds.height);
        }
    }

    private void handleRandomButton() {
        if (randomButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
            batch.draw(randomButtonHoverTexture, randomButtonBounds.x - 5.0F, randomButtonBounds.y - 5.0F, randomButtonBounds.width + 10.0F, randomButtonBounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                Gdx.app.postRunnable(() -> {
                    Random random = new Random();
                    int randomLevel = random.nextInt(3); // Randomly pick 0, 1, or 2

                    // Navigate to the selected level
                    switch (randomLevel) {
                        case 0:
                            game.setScreen(new Level1GameScreen(game, "level1game.png"));
                            break;
                        case 1:
                            game.setScreen(new Level2GameScreen(game));
                            break;
                        case 2:
                            game.setScreen(new Level3GameScreen(game));
                            break;
                    }
                });
            }
        } else {
            batch.draw(randomButtonTexture, randomButtonBounds.x, randomButtonBounds.y, randomButtonBounds.width, randomButtonBounds.height);
        }
    }

    public void resize(int width, int height) { }

    public void pause() { }

    public void resume() { }

    public void hide() { }

    public void dispose() {
        levelsImage.dispose();
        level1Texture.dispose();
        level1HoverTexture.dispose();
        level2Texture.dispose();
        level2HoverTexture.dispose();
        level3Texture.dispose();
        level3HoverTexture.dispose();
        backButtonTexture.dispose();
        backButtonHoverTexture.dispose();
        randomButtonTexture.dispose();
        randomButtonHoverTexture.dispose();
    }
}
