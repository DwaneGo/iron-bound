package io.github.dwanego.ironbound.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Arrays;

public class Player {

    private static final float TIME_STEP = 1f / 60f;
    private static final float MAX_FRAME_TIME = 0.25f;

    private static final float MOVE_ACCELERATION = 1800f;
    private static final float PLAYER_SIZE = 128f;
    private static final float MOVING_THRESHOLD = 7f;
    private static final float MODE_TRANSITION_DURATION = 0.2f;
    private static final float SLIDE_DURATION = 0.9f;
    private static final float SLIDE_HOLD_DURATION = 0.6f;
    private static final float SLIDE_FRAME_DURATION = 0.06f;
    private static final float SLIDE_COOLDOWN = 1f;
    private static final float RUNNING_SPEED = 180f;
    private static final float SPRINTING_SPEED = 330f;
    private static final int FRAME_SIZE = 64;
    private static final int WALKING_SHEET_ROW = 4;
    private static final int SPRINTING_SHEET_ROW = 6;
    private static final int RUNNING_SHEET_ROW = 5;
    private static final int SLIDING_SHEET_ROW = 7;
    private static final int JUMPING_SHEET_ROW = 8;

    private float movementDirection = 0f;
    private MovementMode movementMode = MovementMode.SPRINTING;
    private static final float GRAVITY = 1800f;
    private static final float JUMP_SPEED = 650f;
    private static final float GROUND_Y = 100f;

    private boolean grounded = true;
    private boolean jumpRequested = false;


    private float accumulator = 0f;

    private float x;
    private float y;

    private float previousX;
    private float previousY;

    // Weight is used as mass.
    private float weight;
    private float drag;

    private float forceX;
    private float forceY;

    private float velocityX;
    private float velocityY;

    private float accelerationX;
    private float accelerationY;

    private final Texture spriteSheet;
    private final TextureRegion idleFrame;
    private final Animation<TextureRegion> walkingAnimation;
    private final Animation<TextureRegion> runningAnimation;
    private final Animation<TextureRegion> sprintingAnimation;
    private final Animation<TextureRegion> slidingAnimation;
    private final Animation<TextureRegion> jumpingAnimation;
    private Animation<TextureRegion> transitionFromAnimation;
    private float animationTime;
    private float jumpingAnimationTime;
    private float transitionTime = MODE_TRANSITION_DURATION;
    private float transitionStartMaxSpeed = MovementMode.WALKING.maxSpeed;
    private float slideTimeRemaining;
    private float slideCooldownRemaining;
    private boolean facingLeft;

    public Player(float x, float y) {
        this(x, y, 1f, 2f, false);
    }

    public Player(float x, float y, boolean facingLeft) {
        this(x, y, 1f, 2f, facingLeft);
    }

    public Player(float x, float y, float weight, float drag) {
        this(x, y, weight, drag, false);
    }

    public Player(float x, float y, float weight, float drag, boolean facingLeft) {
        if (weight <= 0 || drag < 0) {
            throw new IllegalArgumentException(
                "Weight must be positive and drag cannot be negative."
            );
        }

        this.x = x;
        this.y = y;

        this.previousX = x;
        this.previousY = y;

        this.weight = weight;
        this.drag = drag;
        this.facingLeft = facingLeft;

        this.forceX = 0f;
        this.forceY = 0f;

        this.velocityX = 0f;
        this.velocityY = 0f;

        calculateAcceleration();

        spriteSheet = new Texture("player-sheet.png");
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, FRAME_SIZE, FRAME_SIZE);
        idleFrame = frames[0][0];
        walkingAnimation = createAnimation(frames[WALKING_SHEET_ROW - 1], 8, 0.12f);
        sprintingAnimation = createAnimation(frames[SPRINTING_SHEET_ROW - 1], 8, 0.06f);
        runningAnimation = createAnimation(frames[RUNNING_SHEET_ROW - 1], 11, 0.08f);
        slidingAnimation = createAnimation(
            frames[SLIDING_SHEET_ROW - 1],
            9,
            SLIDE_FRAME_DURATION
        );
        slidingAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        jumpingAnimation = createAnimation(frames[JUMPING_SHEET_ROW - 1], 8, 0.08f);
        jumpingAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> createAnimation(
        TextureRegion[] row,
        int frameCount,
        float frameDuration
    ) {
        Animation<TextureRegion> animation = new Animation<>(
            frameDuration,
            Arrays.copyOf(row, frameCount)
        );
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

    private void calculateAcceleration() {
        accelerationX = (forceX - drag * velocityX) / weight;
        accelerationY = (forceY - drag * velocityY) / weight;
    }

    public void update(float delta) {
        float frameTime = Math.min(Math.max(delta, 0f), MAX_FRAME_TIME);
        accumulator += frameTime;

        while (accumulator >= TIME_STEP) {
            previousX = x;
            previousY = y;

            if (jumpRequested && grounded) {
                velocityY = JUMP_SPEED;
                grounded = false;
                jumpingAnimationTime = 0f;
            }

            jumpRequested = false;

// Apply gravity.
            forceY = -weight * GRAVITY;

            if (movementDirection != 0f) {
                forceX = movementDirection * weight * MOVE_ACCELERATION + drag * velocityX;
            } else if (slideTimeRemaining > 0f) {
                forceX = drag * velocityX;
            } else {
                forceX = 0f;
            }

            calculateAcceleration();

            velocityX += accelerationX * TIME_STEP;
            velocityY += accelerationY * TIME_STEP;

            // Ramp the speed limit along with the animation transition.
            velocityX = Math.max(
                -getCurrentMaxSpeed(),
                Math.min(getCurrentMaxSpeed(), velocityX)
            );

            x += velocityX * TIME_STEP;
            y += velocityY * TIME_STEP;

            if (y <= GROUND_Y) {
                y = GROUND_Y;
                velocityY = 0f;
                grounded = true;
            }
            slideTimeRemaining = Math.max(0f, slideTimeRemaining - TIME_STEP);
            accumulator -= TIME_STEP;
        }

        slideCooldownRemaining = Math.max(0f, slideCooldownRemaining - frameTime);
        if (movementDirection != 0f) {
            animationTime += frameTime;
        } else {
            animationTime = 0f;
        }
        transitionTime = Math.min(
            MODE_TRANSITION_DURATION,
            transitionTime + frameTime
        );
        if (!grounded) {
            jumpingAnimationTime += frameTime;
        }
    }

    public void setMovement(float direction, MovementMode mode) {
        movementDirection = Math.max(-1f, Math.min(1f, direction));
        if (mode != movementMode) {
            float progress = getTransitionProgress();
            transitionStartMaxSpeed = getCurrentMaxSpeed();
            transitionFromAnimation = progress < 0.5f && transitionFromAnimation != null
                ? transitionFromAnimation
                : getMovementAnimation();
            movementMode = mode;
            transitionTime = 0f;
        }
        if (movementDirection != 0f) {
            facingLeft = movementDirection < 0f;
        } else {
            transitionFromAnimation = null;
        }
    }

    public void slide() {
        if (!grounded || slideCooldownRemaining > 0f
            || (movementDirection == 0f && Math.abs(velocityX) <= MOVING_THRESHOLD)) {
            return;
        }

        slideTimeRemaining = SLIDE_DURATION;
        slideCooldownRemaining = SLIDE_COOLDOWN;
    }

    public void setForce(float forceX, float forceY) {
        this.forceX = forceX;
        this.forceY = forceY;
    }

    public void render(SpriteBatch batch) {
        float alpha = accumulator / TIME_STEP;

        float renderX = previousX + (x - previousX) * alpha;
        float renderY = previousY + (y - previousY) * alpha;

        TextureRegion frame = !grounded
            ? jumpingAnimation.getKeyFrame(jumpingAnimationTime, false)
            : slideTimeRemaining > 0f
                ? getSlidingFrame()
                : movementDirection != 0f
                ? getMovementAnimation().getKeyFrame(animationTime)
                : idleFrame;
        if (grounded && slideTimeRemaining <= 0f && movementDirection != 0f) {
            drawSpeedBasedMovement(batch, renderX, renderY);
            return;
        }
        float progress = getTransitionProgress();
        if (slideTimeRemaining <= 0f && movementDirection != 0f
            && progress < 1f && transitionFromAnimation != null) {
            Color originalColor = new Color(batch.getColor());
            batch.setColor(
                originalColor.r,
                originalColor.g,
                originalColor.b,
                originalColor.a * (1f - progress)
            );
            drawFrame(batch, transitionFromAnimation.getKeyFrame(animationTime), renderX, renderY);
            batch.setColor(
                originalColor.r,
                originalColor.g,
                originalColor.b,
                originalColor.a * progress
            );
            drawFrame(batch, frame, renderX, renderY);
            batch.setColor(originalColor);
            if (progress >= 1f) {
                transitionFromAnimation = null;
            }
            return;
        }

        drawFrame(batch, frame, renderX, renderY);
    }

    private TextureRegion getSlidingFrame() {
        float elapsed = SLIDE_DURATION - slideTimeRemaining;
        if (elapsed < SLIDE_HOLD_DURATION) {
            int frameIndex = Math.min(3, (int) (elapsed / SLIDE_FRAME_DURATION));
            return slidingAnimation.getKeyFrames()[frameIndex];
        }

        float standUpTime = elapsed - SLIDE_HOLD_DURATION;
        int frameIndex = 4 + (int) (standUpTime / SLIDE_FRAME_DURATION);
        return slidingAnimation.getKeyFrames()[Math.min(frameIndex, 8)];
    }

    private void drawSpeedBasedMovement(SpriteBatch batch, float x, float y) {
        float progress = smoothStep(
            (Math.abs(velocityX) - RUNNING_SPEED) / (SPRINTING_SPEED - RUNNING_SPEED)
        );
        Color originalColor = new Color(batch.getColor());
        if (progress < 1f) {
            batch.setColor(
                originalColor.r,
                originalColor.g,
                originalColor.b,
                originalColor.a * (1f - progress)
            );
            drawFrame(batch, runningAnimation.getKeyFrame(animationTime), x, y);
        }
        if (progress > 0f) {
            batch.setColor(
                originalColor.r,
                originalColor.g,
                originalColor.b,
                originalColor.a * progress
            );
            drawFrame(batch, sprintingAnimation.getKeyFrame(animationTime), x, y);
        }
        batch.setColor(originalColor);
    }

    private void drawFrame(SpriteBatch batch, TextureRegion frame, float x, float y) {
        if (facingLeft) {
            batch.draw(frame, x + PLAYER_SIZE, y, -PLAYER_SIZE, PLAYER_SIZE);
        } else {
            batch.draw(frame, x, y, PLAYER_SIZE, PLAYER_SIZE);
        }
    }

    private Animation<TextureRegion> getMovementAnimation() {
        switch (movementMode) {
            case RUNNING:
                return runningAnimation;
            case SPRINTING:
                return runningAnimation;
            default:
                return walkingAnimation;
        }
    }

    private float getTransitionProgress() {
        return smoothStep(transitionTime / MODE_TRANSITION_DURATION);
    }

    private float smoothStep(float progress) {
        progress = Math.max(0f, Math.min(1f, progress));
        return progress * progress * (3f - 2f * progress);
    }

    private float getCurrentMaxSpeed() {
        float progress = getTransitionProgress();
        return transitionStartMaxSpeed
            + (movementMode.maxSpeed - transitionStartMaxSpeed) * progress;
    }

    public void jump() {
        if (grounded) {
            jumpRequested = true;
        }
    }

    public void dispose() {
        spriteSheet.dispose();
    }

    public enum MovementMode {
        WALKING(120f),
        RUNNING(240f),
        SPRINTING(360f);

        private final float maxSpeed;

        MovementMode(float maxSpeed) {
            this.maxSpeed = maxSpeed;
        }
    }
}
