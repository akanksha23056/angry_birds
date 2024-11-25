
package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.akanksha23056.Main;

public class GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture gameScreenImage;
    private final float screenWidth;
    private final float screenHeight;
    private float elapsedTime = 0.0F;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.gameScreenImage = new Texture("gamescreen.jpg");
        this.screenWidth = (float)Gdx.graphics.getWidth();
        this.screenHeight = (float)Gdx.graphics.getHeight();
    }

    public void show() {
    }

    public void render(float delta) {
        this.elapsedTime += delta;
        if (this.elapsedTime > 3.0F) {
            this.game.setScreen(new HomeScreen(this.game));
        }

        ScreenUtils.clear(0.15F, 0.15F, 0.2F, 1.0F);
        this.batch.begin();
        this.batch.draw(this.gameScreenImage, 0.0F, 0.0F, this.screenWidth, this.screenHeight);
        this.batch.end();
    }

    public void resize(int width, int height) {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
        this.gameScreenImage.dispose();
    }
}
