import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;

public class GameScene {

    private Scene scene;               // The main game scene
    private Pane root;                 // Root pane that holds everything
    private Pane world;                // Pane representing the game world (scrollable)
    private final Stage stage;

    // LIVES & RESPAWN
    private int lives;
    private Label livesLabel;          // Label to display remaining lives
    private final double initialSpawnX = 672; // Starting X position for player respawn
    private final double initialSpawnY = 680; // Starting Y position for player respawn

    // SCORE DISPLAY
    private int score;
    private Label scoreLabel;          // Label to display score

    private Tank playerTank;
    private AnimationTimer gameLoop;   // Main game loop timer
    private AnimationTimer cameraLoop; // Camera-following timer

    // PAUSE OVERLAY
    private boolean paused = false;
    private Pane pauseOverlay;         // Semi-transparent pane shown when paused

    // GAME OVER OVERLAY
    private boolean gameOver = false;
    private Pane gameOverOverlay;      // Semi-transparent pane shown on game over

    // IMAGES & ASSETS
    private Image smallExplosionImage;
    private Image explosionImage;
    private Image wallPhoto;
    private Image bulletImage;
    private String[] tankImagesForYellow; // Sprite paths for player's tank
    private String[] tankImagesForWhite;  // Sprite paths for enemy tanks

    // WALLS
    private final List<ImageView> walls = new ArrayList<>();

    // BULLETS & ENEMIES
    private final List<Bullet> bullets = new ArrayList<>();    // Active bullets in the world
    private final List<Tank> enemyTanks = new ArrayList<>();   // Active enemy tanks
    private final List<Tank> allTanks = new ArrayList<>(enemyTanks); // List of all tanks (including player)

    // SCENE & WORLD DIMENSIONS
    private static final double SCENE_W = 784;  // Width of the visible viewport
    private static final double SCENE_H = 784;  // Height of the visible viewport
    private static final double WORLD_W = 1344; // Width of the entire game world
    private static final double WORLD_H = 896;  // Height of the entire game world

    // TIMING CONTROL (seconds)
    private long lastMoveTime = 0;      // Last time player moved
    private long lastFireTime = 0;      // Last time player fired
    private long lastSpawnTime = 0;     // Last time an enemy spawned
    private static final long MOVE_INTERVAL = 25_000_000L;   // 25 ms between moves
    private static final long FIRE_INTERVAL = 1_000_000_000L; // 1 second between shots
    private static final long SPAWN_INTERVAL = 3_000_000_000L; // 3 seconds between spawns

    // Set of currently pressed keys for continuous input
    private final Set<KeyCode> keysPressed = new HashSet<>();

    // Timer used to delay the player's respawn after death
    private PauseTransition respawnTimer;

    /**
     * Constructor: sets up stage, loads assets, creates walls, player, enemies, overlays,
     * input handling, game loop, and camera loop.
     */
    public GameScene(Stage stage) {
        this.stage = stage;
        setupStageAndWorld(stage);
        loadAssets(stage);
        createWalls(world, wallPhoto, walls);
        setupPlayerLivesAndScores();
        setupPauseOverlay();
        setupInputHandling();
        setupGameLoop();
        setupCamera();
    }

    public Scene getScene() {return scene;}

    public void setScore(int score) {this.score = score;}
    public int getScore() {return score;}

    public int getLives() {return lives;}
    public void setLives(int lives) {this.lives = lives;}

    /**
     * Initialize the root pane, world pane, scene, and stage settings.
     */
    private void setupStageAndWorld(Stage stage) {
        root = new Pane();
        root.setPrefSize(SCENE_W, SCENE_H);

        world = new Pane();
        world.setPrefSize(WORLD_W, WORLD_H);
        world.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        root.getChildren().add(world);

        scene = new Scene(root, SCENE_W, SCENE_H);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Tank2025");
        stage.show();
    }

    /**
     * Creates an explosion animation at (centerX, centerY) using the provided image.
     * Shows the explosion for 0.3 seconds, then removes it.
     */
    private void spawnExplosion(double centerX, double centerY, Image image, Pane root) {
        // Create ImageView for explosion
        ImageView explosion = new ImageView(image);

        // Calculate width/height to center properly
        double w = image.getWidth();
        double h = image.getHeight();

        // Position the explosion so its center is at (centerX, centerY)
        explosion.setX(centerX - w / 2.0);
        explosion.setY(centerY - h / 2.0);

        root.getChildren().add(explosion);
        explosion.toFront();

        // After 0.3 seconds, remove the explosion image
        PauseTransition pt = new PauseTransition(Duration.seconds(0.3));
        pt.setOnFinished(e -> root.getChildren().remove(explosion));
        pt.play();
    }

    /**
     * Build all wall segments:
     * - A cross shape (vertical + horizontal lines at center)
     * - Top, bottom, left, and right edges
     * Walls are 16×14 px tiles.
     */
    private void createWalls(Pane root, Image wallPhoto, List<ImageView> walls) {
        int segmentW = 16;   // Wall tile width
        int segmentH = 14;   // Wall tile height
        int length   = 15;   // Half-arm length for central cross
        double centerX = (root.getWidth()  - segmentW) / 2; // Center X
        double centerY = (root.getHeight() - segmentH) / 2; // Center Y
        double width  = root.getWidth();
        double height = root.getHeight();

        // Vertical line of the cross
        for (int i = -length; i <= length; i++) {
            ImageView iv = new ImageView(wallPhoto);
            iv.setX(centerX);
            iv.setY(centerY + i * segmentH);
            walls.add(iv);
            root.getChildren().add(iv);
        }
        // Horizontal line of the cross
        for (int j = -length; j <= length; j++) {
            ImageView iv = new ImageView(wallPhoto);
            iv.setX(centerX + j * segmentW);
            iv.setY(centerY);
            walls.add(iv);
            root.getChildren().add(iv);
        }
        // Top and bottom edges
        int cols = (int) Math.ceil(width / segmentW);
        for (int i = 0; i < cols; i++) {
            // Top edge tile
            ImageView top = new ImageView(wallPhoto);
            top.setFitWidth(segmentW);
            top.setFitHeight(segmentH);
            top.setX(i * segmentW);
            top.setY(0);
            walls.add(top);
            root.getChildren().add(top);

            // Bottom edge tile
            ImageView bot = new ImageView(wallPhoto);
            bot.setFitWidth(segmentW);
            bot.setFitHeight(segmentH);
            bot.setX(i * segmentW);
            bot.setY(height - segmentH);
            walls.add(bot);
            root.getChildren().add(bot);
        }
        // Left and right edges
        int rows = (int) Math.ceil(height / segmentH);
        for (int j = 0; j < rows; j++) {
            // Left edge tile
            ImageView left = new ImageView(wallPhoto);
            left.setFitWidth(segmentW);
            left.setFitHeight(segmentH);
            left.setX(0);
            left.setY(j * segmentH);
            walls.add(left);
            root.getChildren().add(left);

            // Right edge tile
            ImageView right = new ImageView(wallPhoto);
            right.setFitWidth(segmentW);
            right.setFitHeight(segmentH);
            right.setX(width - segmentW);
            right.setY(j * segmentH);
            walls.add(right);
            root.getChildren().add(right);
        }
    }

    /**
     * Load all image assets into memory and set the application icon.
     */
    private void loadAssets(Stage stage) {
        Image icon = new Image("assets/yellowTank1.png");
        stage.getIcons().add(icon);

        wallPhoto = new Image("assets/wall.png");
        smallExplosionImage = new Image("assets/smallExplosion.png");
        explosionImage = new Image("assets/explosion.png");
        bulletImage = new Image("assets/bullet.png");

        // Sprite paths for player (yellow) and enemy (white) tanks
        tankImagesForYellow = new String[]{"assets/yellowTank1.png", "assets/yellowTank2.png"};
        tankImagesForWhite  = new String[]{"assets/whiteTank1.png", "assets/whiteTank2.png"};
    }

    /**
     * Initialize the player tank, spawn a few enemies, and set up UI labels
     * for lives and score.
     */
    private void setupPlayerLivesAndScores() {
        // Create player tank at the spawn location
        playerTank = new Tank(tankImagesForYellow, initialSpawnX, initialSpawnY, walls);
        playerTank.setEnemies(enemyTanks);
        world.getChildren().add(playerTank.getView());
        allTanks.add(playerTank);

        // Immediately spawn three enemies
        spawnEnemy();
        spawnEnemy();
        spawnEnemy();

        // Initialize lives and display label
        lives = 3;
        livesLabel = new Label("Lives: " + lives);
        livesLabel.setFont(Font.font(18));
        livesLabel.setBackground(Background.EMPTY);
        livesLabel.setTextFill(Color.WHITE);
        livesLabel.setLayoutX(20);
        livesLabel.setLayoutY(20);
        root.getChildren().add(livesLabel);

        // Initialize score and display label
        score = 0;
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(Font.font(18));
        scoreLabel.setBackground(Background.EMPTY);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setLayoutX(20);
        scoreLabel.setLayoutY(50);
        root.getChildren().add(scoreLabel);
    }

    /**
     * Move all bullets forward and handle collisions:
     * - If it hits a tank, call handleTankCollision()
     * - If it hits a wall, call handleWallCollision()
     */
    private void processBullets() {
        Iterator<Bullet> bit = bullets.iterator();
        while (bit.hasNext()) {
            Bullet b = bit.next();
            ImageView bv = b.getView();

            double dx = b.getSpeed() * Math.cos(Math.toRadians(b.getAngle()));
            double dy = b.getSpeed() * Math.sin(Math.toRadians(b.getAngle()));

            // If bullet cannot move (collision detected)
            if (!b.canMove(dx, dy)) {
                switch (b.getLastCollision()) {
                    case TANK:
                        handleTankCollision(bv, bit);
                        break;
                    case WALL:
                        handleWallCollision(bv, bit);
                        break;
                }
                continue; // Skip actual move() call if collision occurred
            }
            // No collision: move bullet normally
            b.move();
        }
    }

    /**
     * Called when a bullet collides with a tank.
     * - Find which tank was hit
     * - Spawn large explosion
     * - Remove tank from world (and list)
     * - Update score or lives accordingly
     */
    private void handleTankCollision(ImageView bv, Iterator<Bullet> bit) {
        // Loop through all tanks to find which one was hit
        for (Tank tank : allTanks) {
            if (tank.isShoot()) { // This tank was marked as hit by Bullet.canMove
                ImageView tv = tank.getView();
                Bounds tb = tv.getBoundsInParent();
                double centerX = tb.getMinX() + tb.getWidth() / 2.0;
                double centerY = tb.getMinY() + tb.getHeight() / 2.0;

                // Show a big explosion at the tank’s center
                spawnExplosion(centerX, centerY, explosionImage, world);

                // Remove the tank’s ImageView from the world Pane
                world.getChildren().remove(tv);

                if (tank instanceof EnemyTank) {
                    // If it was an enemy tank: remove from list, stop its AI, update score
                    allTanks.remove(tank);
                    ((EnemyTank) tank).stopAI();
                    setScore(getScore() + 100);
                    scoreLabel.setText("Score: " + score);
                } else {
                    // If it was the player’s tank: decrement lives and update label
                    setLives(getLives() - 1);
                    livesLabel.setText("Lives: " + lives);

                    if (lives > 0) {
                        // Cancel any existing respawnTimer and stop the camera before delaying respawn
                        if (respawnTimer != null) {
                            respawnTimer.stop();
                        }
                        cameraLoop.stop();
                        world.getChildren().remove(playerTank.getView());

                        // Create a new 3-second delay before respawn
                        respawnTimer = new PauseTransition(Duration.seconds(2));
                        respawnTimer.setOnFinished(e -> {
                            respawnPlayer();        // Reposition and re-add player view
                            cameraLoop.start();     // Restart camera tracking
                            respawnTimer = null;    // Clear the reference
                        });
                        respawnTimer.play();
                    } else {
                        // If no lives remain, show Game Over and remove player from list
                        showGameOverScreen();
                        allTanks.remove(tank);
                    }
                }
                break; // Only handle one tank per bullet collision
            }
        }

        // Remove the bullet’s ImageView and remove the Bullet from its iterator
        world.getChildren().remove(bv);
        bit.remove();
    }

    /**
     * Called when a bullet collides with a wall.
     * - Spawn a small explosion at impact point
     * - Remove bullet
     */
    private void handleWallCollision(ImageView bv, Iterator<Bullet> bit) {
        double cx = bv.getX() + bv.getBoundsInParent().getWidth()  / 2.0;
        double cy = bv.getY() + bv.getBoundsInParent().getHeight() / 2.0;
        spawnExplosion(cx, cy, smallExplosionImage, world);
        world.getChildren().remove(bv);
        bit.remove();
    }

    /**
     * Set up the main game loop using AnimationTimer.
     * Each frame:
     *  - Skip if paused
     *  - Read key presses for movement and firing
     *  - Enforce move/fire/spawn intervals
     *  - Update bullets
     */
    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (paused || gameOver) return; // Do nothing if paused

                // Read currently pressed keys
                boolean up    = keysPressed.contains(KeyCode.UP);
                boolean down  = keysPressed.contains(KeyCode.DOWN);
                boolean left  = keysPressed.contains(KeyCode.LEFT);
                boolean right = keysPressed.contains(KeyCode.RIGHT);
                boolean fire  = keysPressed.contains(KeyCode.X);

                // 1) Player movement at fixed intervals
                if (now - lastMoveTime > MOVE_INTERVAL) {
                    // Horizontal movement has priority if only left OR right is pressed
                    if (left ^ right) {
                        if (left)  playerTank.moveLeft();
                        else       playerTank.moveRight();
                    }
                    // Otherwise, vertical movement if up OR down
                    else if (up ^ down) {
                        if (up)    playerTank.moveForward();
                        else       playerTank.moveBackward();
                    }
                    lastMoveTime = now;
                }

                // 2) Player firing at fixed interval
                if (fire && now - lastFireTime > FIRE_INTERVAL) {
                    playerTank.fire(bulletImage, world, bullets, walls, enemyTanks);
                    lastFireTime = now;
                }

                // 3) Spawn new enemy at fixed interval
                if (now - lastSpawnTime > SPAWN_INTERVAL) {
                    spawnEnemy();
                    lastSpawnTime = now;
                }

                // 4) Update bullets and handle collisions
                processBullets();
            }
        };
        gameLoop.start();
    }

    /**
     * Set up key listeners for:
     * - Adding/removing keys from keysPressed set
     * - Immediate actions (Pause, Restart, Exit) on P, R, ESCAPE
     */
    private void setupInputHandling() {
        scene.setOnKeyPressed(e -> {
            keysPressed.add(e.getCode());
            switch (e.getCode()) {
                case P:
                    if (!gameOver) togglePause();
                    break;
                case R:
                    if (paused || gameOver) restartGame(stage);
                    break;
                case ESCAPE:
                    stage.close();
                    break;
                default:
                    // Movement and firing handled in gameLoop
            }
        });
        scene.setOnKeyReleased(e -> {
            keysPressed.remove(e.getCode());
        });
    }

    /**
     * Create and add a new EnemyTank at a random X position near top of world.
     */
    private void spawnEnemy() {
        double minX = 60;
        double maxX = 1250;
        double x = minX + Math.random() * (maxX - minX);
        double y = 40;
        EnemyTank e = new EnemyTank(tankImagesForWhite, x, y, walls, bulletImage, bullets, playerTank, world);
        world.getChildren().add(e.getView());
        enemyTanks.add(e);
        allTanks.add(e);
    }

    /**
     * Respawn the player at the initial spawn coordinates.
     * Reset shooting flag and re-add the view to the world.
     */
    private void respawnPlayer() {
        playerTank.setX(initialSpawnX);
        playerTank.setY(initialSpawnY);
        playerTank.setShoot(false);
        playerTank.getView().setRotate(270);
        world.getChildren().add(playerTank.getView());
    }

    /**
     * Toggle pause state:
     * - Show/hide pause overlay
     * - Stop/start gameLoop and enemy AI timers
     */
    private void togglePause() {
        paused = !paused;
        pauseOverlay.setVisible(paused);
        if (paused) {
            gameLoop.stop();
            cameraLoop.stop();
            if(respawnTimer !=null){
                respawnTimer.stop();
            }

            for (Tank t: enemyTanks) {
                if (t instanceof EnemyTank) {
                    ((EnemyTank) t).stopAI();
                }
            }
            pauseOverlay.toFront();
        } else {
            gameLoop.start();
            cameraLoop.start();
            if(respawnTimer !=null){
                respawnTimer.playFromStart();
            }
            for (Tank t: enemyTanks) {
                if (t instanceof EnemyTank) {
                    ((EnemyTank) t).startAI();
                }
            }
        }
    }

    /**
     * Build a semi-transparent overlay with "PAUSED" text and instructions.
     */
    private void setupPauseOverlay() {
        pauseOverlay = new Pane();
        pauseOverlay.setPrefSize(SCENE_W, SCENE_H);
        pauseOverlay.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));

        Label pausedLabel = new Label("PAUSED");
        pausedLabel.setFont(Font.font(48));
        pausedLabel.setTextFill(Color.WHITE);
        // Center the "PAUSED" label
        pausedLabel.layoutXProperty().bind(pauseOverlay.widthProperty().subtract(pausedLabel.widthProperty()).divide(2));
        pausedLabel.layoutYProperty().bind(pauseOverlay.heightProperty().subtract(pausedLabel.heightProperty()).divide(2));
        pauseOverlay.getChildren().add(pausedLabel);
        pauseOverlay.setVisible(false);
        root.getChildren().add(pauseOverlay);

        Label continueLabel = new Label("Press P to continue");
        continueLabel.setFont(Font.font(24));
        continueLabel.setTextFill(Color.WHITE);
        continueLabel.setBackground(Background.EMPTY);
        // Position below the "PAUSED" label
        continueLabel.layoutXProperty().bind(pauseOverlay.widthProperty().subtract(continueLabel.widthProperty()).divide(2));
        continueLabel.layoutYProperty().bind(pausedLabel.layoutYProperty().add(pausedLabel.heightProperty()).add(-15));
        pauseOverlay.getChildren().add(continueLabel);

        Label restartLabel = new Label("Press R to restart");
        restartLabel.setFont(Font.font(24));
        restartLabel.setTextFill(Color.WHITE);
        restartLabel.setBackground(Background.EMPTY);
        // Position further below the "PAUSED" label
        restartLabel.layoutXProperty().bind(pauseOverlay.widthProperty().subtract(restartLabel.widthProperty()).divide(2));
        restartLabel.layoutYProperty().bind(pausedLabel.layoutYProperty().add(pausedLabel.heightProperty()).add(15));
        pauseOverlay.getChildren().add(restartLabel);
    }

    /**
     * Restart the entire game:
     * - Clear flags
     * - Reinitialize stage, assets, walls, player, enemies, overlays, input, loops
     */
    private void restartGame(Stage stage) {
        // If there is a pending respawn delay, cancel it so it cannot trigger after restart
        if (respawnTimer != null) {
            respawnTimer.stop();
            respawnTimer = null;
        }
        // Reset pause and game-over state flags
        paused = false;
        gameOver = false;
        // Remove all existing enemy tanks and all tanks from their lists
        enemyTanks.clear();
        allTanks.clear();
        bullets.clear();
        walls.clear();
        lastFireTime = 0;
        lastMoveTime = 0;
        lastSpawnTime = 0;
        setupStageAndWorld(stage);
        loadAssets(stage);
        createWalls(world, wallPhoto, walls);
        setupPlayerLivesAndScores();
        setupPauseOverlay();
        setupInputHandling();
        setupGameLoop();
        setupCamera();
    }

    /**
     * Stop game loop and enemy AI, then show a "GAME OVER" overlay with final score
     * and instruction to press R to restart.
     */
    private void showGameOverScreen() {
        gameLoop.stop();
        cameraLoop.stop();
        for (Tank t : enemyTanks) {
            if (t instanceof EnemyTank) {
                ((EnemyTank) t).stopAI();
            }
        }
        gameOver = true;

        gameOverOverlay = new Pane();
        gameOverOverlay.setPrefSize(SCENE_W, SCENE_H);
        gameOverOverlay.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        root.getChildren().add(gameOverOverlay);
        gameOverOverlay.toFront();

        Label go = new Label("GAME OVER!");
        go.setFont(Font.font(72));
        go.setTextFill(Color.RED);
        // Center the "GAME OVER!" text
        go.layoutXProperty().bind(gameOverOverlay.widthProperty().subtract(go.widthProperty()).divide(2));
        go.layoutYProperty().bind(gameOverOverlay.heightProperty().subtract(go.heightProperty()).divide(2).subtract(50));
        gameOverOverlay.getChildren().add(go);

        Label scoreBoard = new Label("Your Score: " + score);
        scoreBoard.setFont(Font.font(24));
        scoreBoard.setTextFill(Color.RED);
        scoreBoard.setBackground(Background.EMPTY);
        // Position below the "GAME OVER!" text
        scoreBoard.layoutXProperty().bind(gameOverOverlay.widthProperty().subtract(scoreBoard.widthProperty()).divide(2));
        scoreBoard.layoutYProperty().bind(go.layoutYProperty().add(go.heightProperty()).add(15));
        gameOverOverlay.getChildren().add(scoreBoard);

        Label restartLabel = new Label("Press R to restart");
        restartLabel.setFont(Font.font(24));
        restartLabel.setTextFill(Color.RED);
        restartLabel.setBackground(Background.EMPTY);
        // Position below the score text
        restartLabel.layoutXProperty().bind(gameOverOverlay.widthProperty().subtract(restartLabel.widthProperty()).divide(2));
        restartLabel.layoutYProperty().bind(scoreBoard.layoutYProperty().add(scoreBoard.heightProperty()).add(15));
        gameOverOverlay.getChildren().add(restartLabel);
    }

    /**
     * Set up a camera that follows the player's tank:
     * Each frame, compute the center of the tank and adjust world.translateX/Y
     * so that tank remains in view, clamped to world edges.
     */
    private void setupCamera() {
        cameraLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (playerTank.getView().getParent() == null) {
                    return;
                }
                // Get the player's tank bounds and find its center
                Bounds tb = playerTank.getView().getBoundsInParent();
                double centerX = tb.getMinX() + tb.getWidth()  / 2.0;
                double centerY = tb.getMinY() + tb.getHeight() / 2.0;

                // Visible scene dimensions
                double sceneW = scene.getWidth();
                double sceneH = scene.getHeight();
                // World dimensions
                double worldW = world.getBoundsInLocal().getWidth();
                double worldH = world.getBoundsInLocal().getHeight();

                // Compute offsets and clamp so we don't scroll out of world bounds
                double offsetX = Math.max(0, Math.min(worldW - sceneW, centerX - sceneW / 2.0));
                double offsetY = Math.max(0, Math.min(worldH - sceneH, centerY - sceneH / 2.0));

                // Apply translation to world pane so the camera follows the tank
                world.setTranslateX(-offsetX);
                world.setTranslateY(-offsetY);
            }
        };
        cameraLoop.start();
    }
}