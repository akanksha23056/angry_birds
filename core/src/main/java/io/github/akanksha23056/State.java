package io.github.akanksha23056;

import com.badlogic.gdx.math.Vector2;
import io.github.akanksha23056.Objects.*;

import java.util.ArrayList;

public class State {
    public static ArrayList<Pig> pigs = new ArrayList<>();
    public static ArrayList<Crate> crates = new ArrayList<>();
    public static ArrayList<Glass> glassSlabs = new ArrayList<>();
    public static BirdType currentBirdType;
    public static Vector2 redBirdPosition = new Vector2();
    public static Vector2 yellowBirdPosition = new Vector2();
    public static Vector2 blackBirdPosition = new Vector2();
    public static Vector2 birdVelocity = new Vector2();
    public static boolean isBirdLaunched = false;
    public static boolean isBlackBirdExploded = false;
    public static boolean isZombiePigSpawned = false;
    public static Pig zombiePig;
}
