package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;
import io.github.akanksha23056.Objects.Bird;

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



    private final Rectangle level1Bounds;
    private final Rectangle level2Bounds;
    private final Rectangle level3Bounds;
    private final Rectangle backButtonBounds;

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

        // Initialize level button bounds
        float buttonWidth = 250.0F;
        float buttonHeight = 250.0F;
        this.level1Bounds = new Rectangle(((float) Gdx.graphics.getWidth() - buttonWidth) / 2.0F,
            (float) Gdx.graphics.getHeight() - buttonHeight - 20.0F, buttonWidth, buttonHeight);
        this.level2Bounds = new Rectangle(this.level1Bounds.x - (buttonWidth / 2.0F + 30.0F),
            this.level1Bounds.y - buttonHeight - 30.0F, buttonWidth, buttonHeight);
        this.level3Bounds = new Rectangle(this.level1Bounds.x + buttonWidth / 2.0F + 30.0F,
            this.level1Bounds.y - buttonHeight - 30.0F, buttonWidth, buttonHeight);
        this.backButtonBounds = new Rectangle(10.0F, (float) (Gdx.graphics.getHeight() - 10 - 100), 100.0F, 100.0F);
    }

    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    public void render(float delta) {
        ScreenUtils.clear(0.15F, 0.15F, 0.2F, 1.0F);
        batch.begin();
        batch.draw(levelsImage, 0.0F, 0.0F, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());

        // Handle level buttons
//        handleButton(level1Texture, level1HoverTexture, level1Bounds, "levelgame.jpg");
        if (level1Bounds.contains((float) Gdx.input.getX(), (float) (Gdx.graphics.getHeight() - Gdx.input.getY()))) {
            batch.draw(level1HoverTexture, level1Bounds.x - 5.0F, level1Bounds.y - 5.0F, level1Bounds.width + 10.0F, level1Bounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                Gdx.app.postRunnable(() -> game.setScreen(new Level1GameScreen(game, "level1game.jpg")));
            }
        } else {
            batch.draw(level1Texture, level1Bounds.x, level1Bounds.y, level1Bounds.width, level1Bounds.height);
        }

//        handleButton(level2Texture, level2HoverTexture, level2Bounds, "levelgame.jpg");
        //handles level 2 button
        if (level2Bounds.contains((float) Gdx.input.getX(), (float) (Gdx.graphics.getHeight() - Gdx.input.getY()))) {
            batch.draw(level2HoverTexture, level2Bounds.x - 5.0F, level2Bounds.y - 5.0F, level2Bounds.width + 10.0F, level2Bounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                Gdx.app.postRunnable(() -> game.setScreen(new Level2GameScreen(game, "level2game.jpg")));
            }
        } else {
            batch.draw(level2Texture, level2Bounds.x, level2Bounds.y, level2Bounds.width, level2Bounds.height);
        }

//        handleButton(level3Texture, level3HoverTexture, level3Bounds, "levelgame.jpg");

        if (level3Bounds.contains((float) Gdx.input.getX(), (float) (Gdx.graphics.getHeight() - Gdx.input.getY()))) {
            batch.draw(level3HoverTexture, level3Bounds.x - 5.0F, level3Bounds.y - 5.0F, level3Bounds.width + 10.0F, level3Bounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                Gdx.app.postRunnable(() -> game.setScreen(new Level2GameScreen(game, "level3game.png")));
            }
        } else {
            batch.draw(level3Texture, level3Bounds.x, level3Bounds.y, level3Bounds.width, level3Bounds.height);
        }

        // Handle back button
        handleBackButton();



        batch.end();
    }

    private void handleButton(Texture normalTexture, Texture hoverTexture, Rectangle bounds, String levelGameImagePath) {
        if (bounds.contains((float) Gdx.input.getX(), (float) (Gdx.graphics.getHeight() - Gdx.input.getY()))) {
            batch.draw(hoverTexture, bounds.x - 5.0F, bounds.y - 5.0F, bounds.width + 10.0F, bounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                Gdx.app.postRunnable(() -> game.setScreen(new Level1GameScreen(game, levelGameImagePath)));
            }
        } else {
            batch.draw(normalTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void handleBackButton() {
        if (backButtonBounds.contains((float) Gdx.input.getX(), (float) (Gdx.graphics.getHeight() - Gdx.input.getY()))) {
            batch.draw(backButtonHoverTexture, backButtonBounds.x - 5.0F, backButtonBounds.y - 5.0F, backButtonBounds.width + 30.0F, backButtonBounds.height + 30.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                Gdx.app.postRunnable(() -> game.setScreen(new HomeScreen(game)));
            }
        } else {
            batch.draw(backButtonTexture, backButtonBounds.x, backButtonBounds.y, backButtonBounds.width, backButtonBounds.height);
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
    }
}
