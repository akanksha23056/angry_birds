package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.akanksha23056.Main;

public class WinScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture winScreenImage;

    public WinScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.winScreenImage = new Texture("winscreen.png");
    }

    @Override
    public void show() {
        // Play background music or any win sound if needed
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // Draw the win screen image
        batch.begin();
        batch.draw(winScreenImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Adjust if necessary
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // Called when screen is hidden
    }

    @Override
    public void dispose() {
        // Dispose resources
        winScreenImage.dispose();
    }
}
