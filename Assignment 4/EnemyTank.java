import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EnemyTank extends Tank {
    // Timer that controls random movement updates
    private final Timeline movementTimer;

    // Timer that controls firing AI (fires at the player)
    private final Timeline fireAI;

    // Random generator for timing variations
    private static final Random rnd = new Random();

    // For random movement
    private static final Random RANDOM = new Random();
    private enum Direction { UP, DOWN, LEFT, RIGHT }
    private Direction currentDir;
    private int stepsLeft = 0;
    private static final int MIN_STEPS = 20;
    private static final int MAX_STEPS = 60;

    /**
     * Constructor for EnemyTank
     * @param spritePaths paths to animation frames for this tank
     * @param startX initial x-coordinate
     * @param startY initial y-coordinate
     * @param walls list of wall ImageViews for collision detection
     * @param bulletImage image used for firing bullets
     * @param bullets list to store active Bullet objects
     * @param playerTank reference to the player's tank (target)
     * @param root the Pane to which bullets (and other nodes) are added
     */
    public EnemyTank(String[] spritePaths, double startX, double startY, List<ImageView> walls, Image bulletImage, List<Bullet> bullets, Tank playerTank, Pane root) {
        // Initialize the base Tank with sprite frames, start position, and walls
        super(spritePaths, startX, startY, walls);
        setEnemies(Collections.singletonList(playerTank));

        // Set up a repeating timer to call randomMovement() every 50 milliseconds
        movementTimer = new Timeline(new KeyFrame(
                Duration.millis(50),
                e -> randomMovement()  // move in a random direction
        ));
        movementTimer.setCycleCount(Timeline.INDEFINITE);
        movementTimer.play();  // start random movement immediately

        // Set up a repeating timer for firing AI, with a random interval between 1 and 2 seconds
        fireAI = new Timeline(new KeyFrame(
                Duration.seconds(1 + rnd.nextDouble()),  // random delay between shots
                e -> {
                    // Fire a bullet targeting only the playerTank
                    this.fire(bulletImage, root, bullets, walls, Collections.singletonList(playerTank));
                }
        ));
        fireAI.setCycleCount(Timeline.INDEFINITE);
        fireAI.play();  // start firing behavior immediately
    }

    /**
     * Stop all AI-related timers (movement and firing).
     * Call this when the game is paused or the enemy should be disabled.
     */
    public void stopAI() {
        if (movementTimer != null) movementTimer.stop();
        if (fireAI != null) fireAI.stop();
    }

    /**
     * Restart all AI-related timers (movement and firing).
     * Call this when the game resumes or the enemy should become active again.
     */
    public void startAI() {
        if (movementTimer != null) movementTimer.play();
        if (fireAI != null) fireAI.play();
    }

    /**
     * Basic random wandering AI:
     * - Pick a random direction for a random number of steps,
     * - Then pick a new direction
     */
    public void randomMovement() {
        if (stepsLeft <= 0) {
            currentDir = Direction.values()[RANDOM.nextInt(4)];
            stepsLeft = RANDOM.nextInt(MAX_STEPS - MIN_STEPS + 1) + MIN_STEPS;
        }

        // Move according to current direction
        switch (currentDir) {
            case UP:
                moveForward();
                break;
            case DOWN:
                moveBackward();
                break;
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
        }
        stepsLeft--;
    }
}