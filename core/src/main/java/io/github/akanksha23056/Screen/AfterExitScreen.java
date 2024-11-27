package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.akanksha23056.Main;
import io.github.akanksha23056.Objects.Pig;

public class AfterExitScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture afterExitImage;
    private float elapsedTime;
    private final Array<Pig> pigs;

    public AfterExitScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
        this.afterExitImage = new Texture("afterexit.png");
        this.elapsedTime = 0;

        // Initialize 20 pigs with random positions
        this.pigs = new Array<>();
        for (int i = 0; i < 50; i++) {
            // Create each pig with a random position (just an example for positions)
            float x = (float) Math.random() * Gdx.graphics.getWidth();
            float y = (float) Math.random() * Gdx.graphics.getHeight();


        }
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        // Clear the screen and draw the afterExit image
        batch.begin();

        // Calculate the dimensions to cover the screen
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Draw the afterExit image, scaling it to cover the screen
        batch.draw(afterExitImage, 0, 0, screenWidth, screenHeight);

        // Update and draw each pig for the flying effect
        for (Pig pig : pigs) {
            pig.act(delta);  // Update pig position
            pig.draw(batch, 1);    // Draw pig
        }
        batch.end();

        // Exit the game after 3 seconds
        if (elapsedTime >= 3) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        afterExitImage.dispose();
        for (Pig pig : pigs) {
            pig.dispose();
        }
    }

    // Empty lifecycle methods
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
