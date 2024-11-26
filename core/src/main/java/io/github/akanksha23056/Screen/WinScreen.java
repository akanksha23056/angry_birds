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

    public WinScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch;

        // Check if the texture exists
        if (!Gdx.files.internal("winscreen.jpg").exists()) {
            Gdx.app.error("WinScreen", "winscreen.jpg does not exist in the assets folder!");
        }

        this.winScreenImage = new Texture("winscreen.jpg");
    }

    @Override
    public void show() {
        if (!game.musicMuted && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(winScreenImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
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
        winScreenImage.dispose();
    }
}
