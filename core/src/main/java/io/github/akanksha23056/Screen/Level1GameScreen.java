package io.github.akanksha23056.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.akanksha23056.Main;
import io.github.akanksha23056.Objects.Bird;
import io.github.akanksha23056.Objects.Catapult;

public class Level1GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture levelImage;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;
    private final Rectangle pauseButtonBounds;

    private final Stage stage;
    private final Catapult catapult;
    private final Array<Bird> birds;

    private Bird currentBird;
    private boolean isBirdLaunched;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private boolean isPaused;

    private Body currentBirdBody;

    private Vector2 dragStart = new Vector2();
    private Vector2 dragEnd = new Vector2();
    private boolean isDragging = false;

    public Level1GameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.batch = game.batch;
        this.levelImage = new Texture(levelImagePath);
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");
        this.pauseButtonBounds = new Rectangle(10, Gdx.graphics.getHeight() - 120, 100, 100);

        // Initialize Box2D World
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        // Initialize Stage
        this.stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        // Initialize Catapult
        this.catapult = new Catapult("sling.png", 100, 100);
        stage.addActor(catapult);

        // Initialize Birds
        this.birds = new Array<>();
        initializeBirds();

        // Set the first bird on the catapult
        if (birds.size > 0) {
            setCurrentBird(birds.first());
        }

        this.isPaused = false;
    }

    private void initializeBirds() {
        float birdStartX = 50;
        float birdStartY = 100;

        // Create three birds
        for (int i = 0; i < 3; i++) {
            Bird bird = new Bird(world, "redbird.png", birdStartX + i * 50, birdStartY);
            birds.add(bird);
            stage.addActor(bird);
        }
    }

    private void setCurrentBird(Bird bird) {
        currentBird = bird;
        currentBird.setPosition(catapult.getX() + 20, catapult.getY() + 50);
        isBirdLaunched = false;

        if (currentBirdBody != null) {
            world.destroyBody(currentBirdBody);
        }

        BodyDef birdBodyDef = new BodyDef();
        birdBodyDef.type = BodyDef.BodyType.DynamicBody;
        birdBodyDef.position.set(currentBird.getX(), currentBird.getY());

        PolygonShape birdShape = new PolygonShape();
        birdShape.setAsBox(currentBird.getWidth() / 2, currentBird.getHeight() / 2);

        FixtureDef birdFixtureDef = new FixtureDef();
        birdFixtureDef.shape = birdShape;
        birdFixtureDef.density = 1.0f;
        birdFixtureDef.restitution = 0.5f;
        birdFixtureDef.friction = 0.3f;

        currentBirdBody = world.createBody(birdBodyDef);
        currentBirdBody.createFixture(birdFixtureDef);

        birdShape.dispose();

        currentBird.reset();
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

        world.step(1 / 60f, 6, 2);

        batch.begin();
        batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        drawPauseButton();
        batch.end();

        debugRenderer.render(world, stage.getCamera().combined);

        stage.act(delta);
        stage.draw();

        handleBirdDragAndLaunch();
    }

    private void handleBirdDragAndLaunch() {
        if (currentBird != null && !isBirdLaunched) {
            if (Gdx.input.isTouched()) {
                if (!isDragging) {
                    dragStart.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
                    isDragging = true;
                } else {
                    dragEnd.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
                }
            } else if (isDragging) {
                Vector2 launchVector = dragStart.sub(dragEnd).scl(0.5f);
                launchBird(launchVector);
                isBirdLaunched = true;
                isDragging = false;

                Gdx.app.postRunnable(() -> {
                    birds.removeValue(currentBird, true);
                    if (birds.size > 0) {
                        setCurrentBird(birds.first());
                    }
                });
            }
        }
    }

    private void launchBird(Vector2 velocity) {
        if (currentBirdBody != null) {
            currentBirdBody.setLinearVelocity(velocity);
        }
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
        world.dispose();
        debugRenderer.dispose();
        for (Bird bird : birds) {
            bird.dispose();
        }
        catapult.dispose();
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
