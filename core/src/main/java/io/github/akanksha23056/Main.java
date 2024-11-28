// Main.java
package io.github.akanksha23056;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.akanksha23056.Screen.GameScreen;
import io.github.akanksha23056.Screen.HomeScreen;
import io.github.akanksha23056.Screen.LevelsScreen;

public class Main extends Game {
    public SpriteBatch batch;
    public Music backgroundMusic;
    public Sound buttonClickSound;
    public boolean musicMuted = false;
    public boolean volumeMuted = false; // Track button click sound state
    public boolean[] unlockedLevels;
    private LevelsScreen levelsScreen;

    public Main() {}

    public void create() {
        this.batch = new SpriteBatch();
        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("song.mp3"));
        this.backgroundMusic.setLooping(true);
        this.backgroundMusic.setVolume(0.5f);

        this.buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("buttons.mp3"));

        // Initialize unlocked levels (only level 1 is unlocked initially)
        this.unlockedLevels = new boolean[]{true, false, false};

        // Initialize LevelsScreen
        this.levelsScreen = new LevelsScreen(this);

        // Set the initial screen to GameScreen
        this.setScreen(new GameScreen(this));

        // Start a new thread for the timer
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Gdx.app.postRunnable(() -> {
                this.setScreen(new HomeScreen(this));
                if (!musicMuted) {
                    this.backgroundMusic.play();
                }
            });
        }).start();
    }

    public LevelsScreen getLevelsScreen() {
        return levelsScreen;
    }

    public void unlockLevel(int levelIndex) {
        if (levelIndex < unlockedLevels.length) {
            unlockedLevels[levelIndex] = true;
        }
    }

    public void muteMusic() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
        musicMuted = true;
    }

    public void unmuteMusic() {
        musicMuted = false;
        backgroundMusic.play();
    }

    // Toggle volume for button click sounds
    public void toggleVolume() {
        volumeMuted = !volumeMuted; // Toggle volumeMuted state
        if (volumeMuted) {
            stopButtonClickSound(); // Stop playing button click sounds if muted
        }
    }

    // Method to play button click sound
    public void playButtonClickSound() {
        if (!volumeMuted) { // Only play if not muted
            buttonClickSound.play();
        }
    }

    // Method to stop button click sound (if you have a specific stopping mechanism)
    public void stopButtonClickSound() {
        // Depending on how you're handling sound, you might have to implement stopping logic here
        // This is generally more relevant for streaming sounds, but for short sounds, they play once.
        // If you're managing multiple sounds, you may need to track currently playing instances.
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        this.batch.dispose();
        this.backgroundMusic.dispose();
        this.buttonClickSound.dispose();
    }
}
