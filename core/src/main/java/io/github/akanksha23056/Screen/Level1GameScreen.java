package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.akanksha23056.Main;

public class Level1GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture levelImage;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;
    private final Rectangle pauseButtonBounds;
    private final Stage stage;
    private boolean isPaused;

    public Level1GameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch;
        this.levelImage = new Texture(levelImagePath);
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");
        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);
        this.isPaused = false;

        this.stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        initializeActors();
    }

    private void initializeActors() {
        // Load textures
        Texture redBirdTexture = new Texture("redbird.png");
        Texture pigTexture = new Texture("pig.png");
        Texture slingTexture = new Texture("sling.png");
        Texture crateTexture = new Texture("crate.png");

        // Create actors
        Image redBird = new Image(redBirdTexture);
        Image pig = new Image(pigTexture);
        Image sling = new Image(slingTexture);
        Image crate = new Image(crateTexture);

        // Set positions
        sling.setPosition(100, 100);
        redBird.setPosition(120, 150);
        pig.setPosition(600, 100);
        crate.setPosition(500, 100);

        // Add actors to stage
        stage.addActor(sling);
        stage.addActor(redBird);
        stage.addActor(pig);
        stage.addActor(crate);
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
        batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        drawPauseButton();
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void drawPauseButton() {
        boolean isHovered = pauseButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        if (isHovered) {
            batch.draw(pauseButtonHoverTexture, pauseButtonBounds.x - 5.0F, pauseButtonBounds.y - 5.0F,
                pauseButtonBounds.width + 10.0F, pauseButtonBounds.height + 10.0F);
            if (Gdx.input.isButtonJustPressed(0)) {
                game.playButtonClickSound();
                pauseGame();
            }
        } else {
            batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y,
                pauseButtonBounds.width, pauseButtonBounds.height);
        }
    }

    private void pauseGame() {
        isPaused = true;
        game.setScreen(new PauseScreen(game));
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        pauseButtonTexture.dispose();
        pauseButtonHoverTexture.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
