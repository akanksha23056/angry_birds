package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.akanksha23056.Main;

public class LoseScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture loseImage;

    public LoseScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.loseImage = new Texture("losescreen.png");
    }

    @Override
    public void show() {
        // Play any sound or effect for the lose screen if needed
    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(loseImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Check if the user wants to return to the main menu or retry
        if (Gdx.input.isTouched()) {
            // Replace with actual navigation logic, like returning to the main menu or retrying
            game.setScreen(new LevelsScreen(game));
        }
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

    @Override
    public void dispose() {
        loseImage.dispose();
    }
}
