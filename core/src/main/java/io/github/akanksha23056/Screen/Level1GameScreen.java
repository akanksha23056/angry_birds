package io.github.akanksha23056.Screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private final Texture levelImage;
    private final Texture pauseButtonTexture;
    private final Texture pauseButtonHoverTexture;

    private final Stage stage;
    private final Catapult catapult;
    private final Array<Bird> birds;
    private Bird currentBird;

    private boolean isBirdLaunched;
    private boolean isDragging;

    private World world;
    private Body currentBirdBody;
    private Vector2 dragStart;  // Start point for dragging
    private Vector2 dragEnd;    // End point for dragging
    private final ShapeRenderer shapeRenderer;

    private final float trajectoryTimeStep = 0.1f; // Time step for trajectory points
    private final int maxTrajectoryPoints = 20;   // Number of points to render

    public Level1GameScreen(Main game, String levelImagePath) {
        this.game = game;
        this.levelImage = new Texture(levelImagePath);
        this.pauseButtonTexture = new Texture("pause.png");
        this.pauseButtonHoverTexture = new Texture("pause_hover.png");

        this.world = new World(new Vector2(0, -9.8f), true);
        this.stage = new Stage(new ScreenViewport(), game.batch);
        this.catapult = new Catapult("catapult.png", 100, 100);
        this.birds = new Array<>();
        this.shapeRenderer = new ShapeRenderer();

        initializeBirds();
        if (birds.size > 0) {
            setCurrentBird(birds.first());
        }

        dragStart = new Vector2();
        dragEnd = new Vector2();
    }

    private void initializeBirds() {
        for (int i = 0; i < 3; i++) {
            Bird bird = new Bird(world, "redbird.png", 100, 100);
            birds.add(bird);
        }
    }

    private void setCurrentBird(Bird bird) {
        this.currentBird = bird;
        bird.setPosition(catapult.getX() + 20, catapult.getY() + 50);
        createBirdBody(bird);
        isBirdLaunched = false;
    }

    private void createBirdBody(Bird bird) {
        if (currentBirdBody != null) {
            world.destroyBody(currentBirdBody);
        }
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(bird.getX(), bird.getY());

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bird.getWidth() / 2, bird.getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.4f;

        currentBirdBody = world.createBody(bodyDef);
        currentBirdBody.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        world.step(1 / 60f, 6, 2);
        stage.act(delta);

        game.batch.begin();
        game.batch.draw(levelImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        handleInput();
        drawTrajectory();
        stage.draw();

        shapeRenderer.end();
    }

    private void handleInput() {
        if (!isBirdLaunched && Gdx.input.isTouched()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                isDragging = true;
                dragEnd.set(Gdx.input.getX(), Gdx.input.getY());
            } else if (isDragging) {
                Vector2 launchVelocity = calculateLaunchVelocity(dragStart, dragEnd);
                launchBird(launchVelocity);
                isDragging = false;
            }
        }
    }

    private void launchBird(Vector2 velocity) {
        if (currentBirdBody != null) {
            currentBirdBody.setLinearVelocity(velocity);
            isBirdLaunched = true;
            birds.removeValue(currentBird, true);
        }
    }

    private Vector2 calculateLaunchVelocity(Vector2 start, Vector2 end) {
        Vector2 velocity = start.sub(end);
        velocity.scl(0.1f); // Adjust the scale for a realistic launch
        return velocity;
    }

    private void drawTrajectory() {
        if (isDragging && currentBirdBody != null) {
            Vector2 launchVelocity = calculateLaunchVelocity(dragStart, dragEnd);
            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);

            Vector2 position = new Vector2(currentBirdBody.getPosition());
            Vector2 velocity = new Vector2(launchVelocity);

            for (int i = 0; i < maxTrajectoryPoints; i++) {
                float time = i * trajectoryTimeStep;
                Vector2 trajectoryPoint = calculateTrajectoryPoint(position, velocity, time);
                shapeRenderer.circle(trajectoryPoint.x, trajectoryPoint.y, 2);
            }
            shapeRenderer.end();
        }
    }

    private Vector2 calculateTrajectoryPoint(Vector2 position, Vector2 velocity, float time) {
        float x = position.x + velocity.x * time;
        float y = position.y + velocity.y * time - 0.5f * 9.8f * time * time;
        return new Vector2(x, y);
    }

    @Override
    public void dispose() {
        levelImage.dispose();
        pauseButtonTexture.dispose();
        pauseButtonHoverTexture.dispose();
        shapeRenderer.dispose();
        world.dispose();
        stage.dispose();
    }

    // Remaining lifecycle methods...
}
