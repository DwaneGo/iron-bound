package io.github.dwanego.ironbound.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.Arrays;

public class Player {

    private static final float TIME_STEP = 1f / 60f;
    private static final float MAX_FRAME_TIME = 0.25f;

    private static final float MOVE_ACCELERATION = 1800f;
    private static final float PLAYER_SIZE = 128f;
    private static final int KAI_FRAME_WIDTH = 224;
    private static final int KAI_FRAME_HEIGHT = 112;
    private static final float KAI_GROUND_BOTTOM_MARGIN = 8f;
    private static final int KAI_STANDING_ROW_INDEX = 9;
    private static final int KAI_STANDING_COLUMN_INDEX = 4;
    private static final int KAI_WALKING_ROW_INDEX = 3;
    private static final int KAI_RUNNING_ROW_INDEX = 4;
    private static final int KAI_SLIDING_ROW_INDEX = 6;
    private static final int KAI_JUMPING_ROW_INDEX = 7;
    private static final int KAI_PUNCH_ROW_INDEX = 2;
    private static final int KAI_DOUBLE_JUMP_ROW_INDEX = 8;
    private static final int KAI_DYING_ROW_INDEX = 8;
    private static final int KAI_DYING_FRAME_COUNT = 6;
    private static final float[] KAI_DYING_FRAME_BOTTOM_Y = {
        104f, 104f, 104f, 104f, 83f, 66f
    };
    private static final float KAI_FRAME_SCALE = 1.5f;
    private static final float ERASER_PROJECTILE_LAUNCH_TIME = 0.63f;
    private static final float ERASER_PROJECTILE_SPEED = 600f;
    private static final float ERASER_PROJECTILE_WIDTH = 67.5f;
    private static final float ERASER_PROJECTILE_HEIGHT = 27f;
    private static final float ERASER_PROJECTILE_SOURCE_X = 192f;
    private static final float ERASER_PROJECTILE_SOURCE_Y = 62f;
    private static final float ERASER_PROJECTILE_SOURCE_WIDTH = 25f;
    private static final float ERASER_PROJECTILE_SOURCE_HEIGHT = 18f;
    private static final float ERASER_PROJECTILE_TRAIL_PIXELS = 20f;
    private static final float ERASER_PROJECTILE_LIFETIME = 2.5f;
    private static final float MOVING_THRESHOLD = 7f;
    private static final float MODE_TRANSITION_DURATION = 0.2f;
    private static final float SLIDE_DURATION = 0.9f;
    private static final float SLIDE_HOLD_DURATION = 0.6f;
    private static final float SLIDE_FRAME_DURATION = 0.06f;
    private static final float SLIDE_COOLDOWN = 1f;
    private static final float RUNNING_SPEED = 180f;
    private static final float SPRINTING_SPEED = 450f;
    private static final float MAX_HEALTH = 100f;
    private static final float HIT_FLASH_DURATION = 0.14f;
    private static final float BLOCK_ANIMATION_DURATION = 0.18f;
    private static final float ATTACK_FRAME_DURATION = 0.07f;
    private static final float ATTACK_DECELERATION = 3000f;
    private static final float HURTBOX_WIDTH = 22f;
    private static final float HURTBOX_HEIGHT = 89f;
    private static final float HURTBOX_X_OFFSET = 53f;
    private static final float HURTBOX_Y_OFFSET = 0f;
    private static final float GUARD_HURTBOX_HEIGHT = HURTBOX_HEIGHT - 10f;
    private static final float SLIDING_HURTBOX_HEIGHT = 44f;
    private static final float SLIDING_HURTBOX_Y_OFFSET = 0f;
    private static final int BLOCK_FRAME_INDEX = 6;
    private static final int ACTION_FRAME_COUNT = 8;
    private static final int FRAME_SIZE = 64;
    private static final int WALKING_SHEET_ROW = 4;
    private static final int SPRINTING_SHEET_ROW = 6;
    private static final int RUNNING_SHEET_ROW = 5;
    private static final int SLIDING_SHEET_ROW = 7;
    private static final int JUMPING_SHEET_ROW = 8;
    private static final int DOUBLE_JUMPING_SHEET_ROW = 9;
    private static final int IDLE_SHEET_ROW = 1;
    private static final int ATTACKING_SHEET_ROW = 3;
    private static final int DYING_SHEET_ROW = 11;
    private float movementDirection = 0f;
    private MovementMode movementMode = MovementMode.SPRINTING;
    private static final float GRAVITY = 1800f;
    private static final float JUMP_SPEED = 650f;
    private static final float GROUND_Y = 100f;

    private boolean grounded = true;
    private boolean jumpRequested = false;
    private boolean doubleJumping;
    private int jumpsUsed;


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
    private final Texture eraserProjectileTexture;
    private final TextureRegion eraserProjectileFrame;
    private final boolean kaiWeaponsMode;
    private final Weapon weapon;
    private final TextureRegion idleFrame;
    private final Animation<TextureRegion> walkingAnimation;
    private final Animation<TextureRegion> runningAnimation;
    private final Animation<TextureRegion> sprintingAnimation;
    private final Animation<TextureRegion> slidingAnimation;
    private final Animation<TextureRegion> jumpingAnimation;
    private final Animation<TextureRegion> doubleJumpingAnimation;
    private final Animation<TextureRegion> attackingAnimation;
    private final Animation<TextureRegion> kaiWeaponAnimation;
    private final Animation<TextureRegion> dyingAnimation;
    private final Rectangle projectileHitbox = new Rectangle();
    private final Rectangle projectileSweepHitbox = new Rectangle();
    private Animation<TextureRegion> transitionFromAnimation;
    private float animationTime;
    private float jumpingAnimationTime;
    private float transitionTime = MODE_TRANSITION_DURATION;
    private float transitionStartMaxSpeed = MovementMode.WALKING.maxSpeed;
    private float slideTimeRemaining;
    private float slideCooldownRemaining;
    private float attackTime;
    private float deathTime;
    private float blockTimeRemaining;
    private float hitFlashTimeRemaining;
    private float health = MAX_HEALTH;
    private Attack currentAttack;
    private boolean attacking;
    private boolean attackHasHit;
    private boolean guarding;
    private boolean dead;
    private boolean facingLeft;
    private boolean projectileLaunched;
    private boolean projectileActive;
    private float projectileX;
    private float projectileY;
    private float projectileLifetime;
    private int projectileDirection;
    private Animation<TextureRegion> currentAttackAnimation;

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
        this(x, y, weight, drag, facingLeft, false, Weapon.PUNCH);
    }

    public Player(
        float x,
        float y,
        float weight,
        float drag,
        boolean facingLeft,
        boolean kaiWeaponsMode,
        Weapon weapon
    ) {
        if (weight <= 0 || drag < 0) {
            throw new IllegalArgumentException(
                "Weight must be positive and drag cannot be negative."
            );
        }
        if (weapon == null || (!kaiWeaponsMode && weapon != Weapon.PUNCH)
            || (kaiWeaponsMode && weapon == Weapon.PUNCH)) {
            throw new IllegalArgumentException("Invalid weapon for the selected mode.");
        }

        this.x = x;
        this.y = y;

        this.previousX = x;
        this.previousY = y;

        this.weight = weight;
        this.drag = drag;
        this.facingLeft = facingLeft;
        this.kaiWeaponsMode = kaiWeaponsMode;
        this.weapon = weapon;

        this.forceX = 0f;
        this.forceY = 0f;

        this.velocityX = 0f;
        this.velocityY = 0f;

        calculateAcceleration();

        spriteSheet = new Texture(
            kaiWeaponsMode
                ? "sprite_sheet_KaiCenat_combat_aligned.png"
                : "player-sheet.png"
        );
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        eraserProjectileTexture = kaiWeaponsMode && weapon == Weapon.ERASER
            ? new Texture("kai_eraser_projectile.png")
            : null;
        if (eraserProjectileTexture != null) {
            eraserProjectileTexture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
            );
            eraserProjectileFrame = new TextureRegion(eraserProjectileTexture);
        } else {
            eraserProjectileFrame = null;
        }
        TextureRegion[][] frames;
        if (kaiWeaponsMode) {
            frames = TextureRegion.split(spriteSheet, KAI_FRAME_WIDTH, KAI_FRAME_HEIGHT);
            idleFrame = frames[KAI_STANDING_ROW_INDEX][KAI_STANDING_COLUMN_INDEX];
            walkingAnimation = createAnimation(
                frames[KAI_WALKING_ROW_INDEX],
                8,
                0.12f
            );
            runningAnimation = createAnimation(
                frames[KAI_RUNNING_ROW_INDEX],
                10,
                0.07f
            );
            sprintingAnimation = runningAnimation;
            slidingAnimation = createAnimation(
                frames[KAI_SLIDING_ROW_INDEX],
                9,
                SLIDE_FRAME_DURATION
            );
            jumpingAnimation = createAnimation(
                frames[KAI_JUMPING_ROW_INDEX],
                8,
                0.08f
            );
            doubleJumpingAnimation = createAnimation(
                frames[KAI_DOUBLE_JUMP_ROW_INDEX],
                9,
                0.1f
            );
            attackingAnimation = createAnimation(
                frames[KAI_PUNCH_ROW_INDEX],
                7,
                ATTACK_FRAME_DURATION
            );
            kaiWeaponAnimation = createAnimation(
                frames[weapon.getAnimationRow()],
                weapon.getAnimationFrameCount(),
                ATTACK_FRAME_DURATION
            );
            dyingAnimation = createAnimation(
                frames[KAI_DYING_ROW_INDEX],
                KAI_DYING_FRAME_COUNT,
                0.1f
            );
        } else {
            frames = TextureRegion.split(spriteSheet, FRAME_SIZE, FRAME_SIZE);
            idleFrame = frames[0][0];
            walkingAnimation = createAnimation(frames[WALKING_SHEET_ROW - 1], 8, 0.12f);
            sprintingAnimation = createAnimation(frames[SPRINTING_SHEET_ROW - 1], 8, 0.06f);
            runningAnimation = createAnimation(frames[RUNNING_SHEET_ROW - 1], 11, 0.08f);
            slidingAnimation = createAnimation(
                frames[SLIDING_SHEET_ROW - 1],
                9,
                SLIDE_FRAME_DURATION
            );
            jumpingAnimation = createAnimation(frames[JUMPING_SHEET_ROW - 1], 8, 0.08f);
            doubleJumpingAnimation = createAnimation(
                frames[DOUBLE_JUMPING_SHEET_ROW - 1],
                frames[DOUBLE_JUMPING_SHEET_ROW - 1].length,
                0.08f
            );
            attackingAnimation = createAnimation(
                frames[ATTACKING_SHEET_ROW - 1],
                ACTION_FRAME_COUNT,
                ATTACK_FRAME_DURATION
            );
            kaiWeaponAnimation = null;
            dyingAnimation = createAnimation(
                frames[DYING_SHEET_ROW - 1],
                ACTION_FRAME_COUNT,
                0.1f
            );
        }
        attackingAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        doubleJumpingAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        dyingAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        if (kaiWeaponAnimation != null) {
            kaiWeaponAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        }
        currentAttackAnimation = attackingAnimation;
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
        hitFlashTimeRemaining = Math.max(0f, hitFlashTimeRemaining - frameTime);
        blockTimeRemaining = Math.max(0f, blockTimeRemaining - frameTime);
        if (dead) {
            deathTime = Math.min(dyingAnimation.getAnimationDuration(), deathTime + frameTime);
            if (!grounded) {
                velocityY -= GRAVITY * frameTime;
                y += velocityY * frameTime;
                if (y <= GROUND_Y) {
                    y = GROUND_Y;
                    velocityY = 0f;
                    grounded = true;
                }
            }
            return;
        }

        accumulator += frameTime;

        while (accumulator >= TIME_STEP) {
            previousX = x;
            previousY = y;

            if (jumpRequested && jumpsUsed < 2) {
                velocityY = JUMP_SPEED;
                grounded = false;
                jumpsUsed++;
                if (jumpsUsed == 1) {
                    jumpingAnimationTime = 0f;
                } else {
                    doubleJumping = true;
                    jumpingAnimationTime = 0f;
                }
            }

            jumpRequested = false;

// Apply gravity.
            forceY = -weight * GRAVITY;

            if (guarding || attacking) {
                forceX = drag * velocityX;
            } else if (movementDirection != 0f) {
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
            if (guarding) {
                velocityX = 0f;
            } else if (attacking) {
                float deceleration = ATTACK_DECELERATION * TIME_STEP;
                velocityX = Math.abs(velocityX) <= deceleration
                    ? 0f
                    : velocityX - Math.signum(velocityX) * deceleration;
            }

            x += velocityX * TIME_STEP;
            y += velocityY * TIME_STEP;

            if (y <= GROUND_Y) {
                y = GROUND_Y;
                velocityY = 0f;
                grounded = true;
                jumpsUsed = 0;
                doubleJumping = false;
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
        if (attacking) {
            attackTime += frameTime;
            if (isEraserProjectile() && !projectileLaunched
                && attackTime >= ERASER_PROJECTILE_LAUNCH_TIME) {
                launchEraserProjectile();
            }
            if (attackTime >= currentAttackAnimation.getAnimationDuration()) {
                attacking = false;
                currentAttack = null;
            }
        }
        updateProjectile(frameTime);
    }

    public void setMovement(float direction, MovementMode mode) {
        if (dead) {
            movementDirection = 0f;
            return;
        }

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
        if (movementDirection != 0f && !attacking) {
            facingLeft = movementDirection < 0f;
        } else {
            transitionFromAnimation = null;
        }
    }

    public void slide() {
        if (dead || guarding || attacking || !grounded || slideCooldownRemaining > 0f
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
        renderProjectile(batch);

        if (dead) {
            int deathFrameIndex = dyingAnimation.getKeyFrameIndex(deathTime);
            float deathFloorAdjustment = kaiWeaponsMode
                ? getKaiDeathFloorAdjustment(deathFrameIndex)
                : 0f;
            drawFrame(
                batch,
                dyingAnimation.getKeyFrame(deathTime, false),
                kaiWeaponsMode ? x : renderX,
                kaiWeaponsMode ? y : renderY,
                deathFloorAdjustment
            );
            return;
        }
        if (guarding || blockTimeRemaining > 0f) {
            drawFrame(
                batch,
                attackingAnimation.getKeyFrames()[
                    Math.min(BLOCK_FRAME_INDEX, attackingAnimation.getKeyFrames().length - 1)
                ],
                renderX,
                renderY
            );
            return;
        }
        if (attacking) {
            drawFrame(
                batch,
                currentAttackAnimation.getKeyFrame(attackTime, false),
                renderX,
                renderY
            );
            return;
        }

        TextureRegion frame = !grounded
            ? doubleJumping
                ? doubleJumpingAnimation.getKeyFrame(jumpingAnimationTime, false)
                : jumpingAnimation.getKeyFrame(jumpingAnimationTime, false)
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
        if (kaiWeaponsMode) {
            drawFrame(batch, runningAnimation.getKeyFrame(animationTime), x, y);
            return;
        }

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
        drawFrame(batch, frame, x, y, 0f);
    }

    private void drawFrame(
        SpriteBatch batch,
        TextureRegion frame,
        float x,
        float y,
        float kaiVerticalAdjustment
    ) {
        if (kaiWeaponsMode) {
            float width = KAI_FRAME_WIDTH * KAI_FRAME_SCALE;
            float height = KAI_FRAME_HEIGHT * KAI_FRAME_SCALE;
            float bodyCenter = facingLeft ? KAI_FRAME_WIDTH - 80f : 80f;
            float drawX = x + PLAYER_SIZE / 2f - bodyCenter * KAI_FRAME_SCALE;
            float drawY = y - KAI_GROUND_BOTTOM_MARGIN * KAI_FRAME_SCALE
                + kaiVerticalAdjustment;
            if (facingLeft) {
                batch.draw(frame, drawX + width, drawY, -width, height);
            } else {
                batch.draw(frame, drawX, drawY, width, height);
            }
            return;
        }
        if (facingLeft) {
            batch.draw(frame, x + PLAYER_SIZE, y, -PLAYER_SIZE, PLAYER_SIZE);
        } else {
            batch.draw(frame, x, y, PLAYER_SIZE, PLAYER_SIZE);
        }
    }

    private float getKaiDeathFloorAdjustment(int frameIndex) {
        float bottomMargin = KAI_FRAME_HEIGHT - KAI_DYING_FRAME_BOTTOM_Y[frameIndex];
        return (KAI_GROUND_BOTTOM_MARGIN - bottomMargin) * KAI_FRAME_SCALE;
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
        if (!dead && !attacking && jumpsUsed < 2) {
            jumpRequested = true;
        }
    }

    public void punch() {
        useAttack(kaiWeaponsMode ? weapon.getAttack() : Attack.PUNCH);
    }

    public void useAttack(Attack attack) {
        if (attack == null) {
            throw new IllegalArgumentException("Attack cannot be null.");
        }
        if (dead || guarding || attacking || slideTimeRemaining > 0f) {
            return;
        }

        movementDirection = 0f;
        slideTimeRemaining = 0f;
        currentAttack = attack;
        currentAttackAnimation = kaiWeaponsMode && attack == weapon.getAttack()
            ? kaiWeaponAnimation
            : attackingAnimation;
        attacking = true;
        attackTime = 0f;
        attackHasHit = false;
        projectileLaunched = false;
    }

    public boolean tryHit(Player target) {
        if (isEraserProjectile()) {
            if (!projectileActive || target.dead
                || !projectileSweepHitbox.overlaps(target.getHurtbox())) {
                return false;
            }
            projectileActive = false;
            target.takeDamage(weapon.getAttack().getDamage());
            return true;
        }

        Rectangle hitbox = getAttackHitbox();
        if (hitbox == null || attackHasHit || target.dead || !hitbox.overlaps(target.getHurtbox())) {
            return false;
        }

        attackHasHit = true;
        target.takeDamage(currentAttack.getDamage());
        return true;
    }

    public Rectangle getAttackHitbox() {
        if (isEraserProjectile()) {
            return projectileActive ? projectileHitbox : null;
        }
        if (!attacking || currentAttack == null || !currentAttack.isActiveAt(attackTime)) {
            return null;
        }

        return currentAttack.createHitbox(x, y, PLAYER_SIZE, facingLeft);
    }

    public Rectangle getHurtbox() {
        boolean lowProfile = guarding || slideTimeRemaining > 0f;
        return new Rectangle(
            x + HURTBOX_X_OFFSET,
            y + (lowProfile ? SLIDING_HURTBOX_Y_OFFSET : HURTBOX_Y_OFFSET),
            HURTBOX_WIDTH,
            lowProfile ? GUARD_HURTBOX_HEIGHT : HURTBOX_HEIGHT
        );
    }

    public void setGuarding(boolean guarding) {
        this.guarding = guarding && !dead && movementDirection == 0f;
        if (this.guarding) {
            movementDirection = 0f;
            velocityX = 0f;
            slideTimeRemaining = 0f;
        }
    }

    public void takeDamage(float damage) {
        if (damage <= 0f || dead) {
            return;
        }

        if (guarding) {
            damage *= 0.2f;
            blockTimeRemaining = BLOCK_ANIMATION_DURATION;
        }

        health = Math.max(0f, health - damage);
        hitFlashTimeRemaining = HIT_FLASH_DURATION;
        if (health == 0f) {
            dead = true;
            deathTime = 0f;
            attacking = false;
            projectileActive = false;
            movementDirection = 0f;
            velocityX = 0f;
            velocityY = 0f;
            slideTimeRemaining = 0f;
        }
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    public boolean isDead() {
        return dead;
    }

    public float getHitFlashAmount() {
        return hitFlashTimeRemaining / HIT_FLASH_DURATION;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void dispose() {
        spriteSheet.dispose();
        if (eraserProjectileTexture != null) {
            eraserProjectileTexture.dispose();
        }
    }

    private boolean isEraserProjectile() {
        return kaiWeaponsMode && weapon == Weapon.ERASER;
    }

    private void launchEraserProjectile() {
        projectileLaunched = true;
        projectileActive = true;
        projectileLifetime = 0f;
        projectileDirection = facingLeft ? -1 : 1;
        float frameWidth = KAI_FRAME_WIDTH * KAI_FRAME_SCALE;
        float bodyCenter = facingLeft ? KAI_FRAME_WIDTH - 80f : 80f;
        float frameDrawX = x + PLAYER_SIZE / 2f - bodyCenter * KAI_FRAME_SCALE;
        float frameDrawY = y - KAI_FRAME_HEIGHT * KAI_FRAME_SCALE * 0.42f;
        projectileX = facingLeft
            ? frameDrawX + frameWidth
                - (ERASER_PROJECTILE_SOURCE_X + ERASER_PROJECTILE_SOURCE_WIDTH)
                    * KAI_FRAME_SCALE
            : frameDrawX
                + (ERASER_PROJECTILE_SOURCE_X - ERASER_PROJECTILE_TRAIL_PIXELS)
                    * KAI_FRAME_SCALE;
        projectileY = frameDrawY + KAI_FRAME_HEIGHT * KAI_FRAME_SCALE
            - (ERASER_PROJECTILE_SOURCE_Y + ERASER_PROJECTILE_SOURCE_HEIGHT)
                * KAI_FRAME_SCALE;
        projectileHitbox.set(
            getProjectileHeadX(),
            projectileY + 10f,
            ERASER_PROJECTILE_SOURCE_WIDTH * KAI_FRAME_SCALE,
            ERASER_PROJECTILE_HEIGHT - 3f
        );
        projectileSweepHitbox.set(projectileHitbox);
    }

    private void updateProjectile(float delta) {
        if (!projectileActive) {
            return;
        }
        float previousX = projectileX;
        projectileX += projectileDirection * ERASER_PROJECTILE_SPEED * delta;
        projectileLifetime += delta;
        projectileHitbox.setPosition(getProjectileHeadX(), projectileY + 10f);
        float previousHeadX = previousX
            + (projectileDirection > 0
                ? ERASER_PROJECTILE_TRAIL_PIXELS * KAI_FRAME_SCALE
                : 0f);
        float currentHeadX = getProjectileHeadX();
        projectileSweepHitbox.set(
            Math.min(previousHeadX, currentHeadX),
            projectileY + 10f,
            ERASER_PROJECTILE_SOURCE_WIDTH * KAI_FRAME_SCALE
                + Math.abs(projectileX - previousX),
            ERASER_PROJECTILE_HEIGHT - 3f
        );
        if (projectileLifetime >= ERASER_PROJECTILE_LIFETIME) {
            projectileActive = false;
        }
    }

    private void renderProjectile(SpriteBatch batch) {
        if (!projectileActive) {
            return;
        }
        if (projectileDirection < 0) {
            batch.draw(
                eraserProjectileFrame,
                projectileX + ERASER_PROJECTILE_WIDTH,
                projectileY,
                -ERASER_PROJECTILE_WIDTH,
                ERASER_PROJECTILE_HEIGHT
            );
        } else {
            batch.draw(
                eraserProjectileFrame,
                projectileX,
                projectileY,
                ERASER_PROJECTILE_WIDTH,
                ERASER_PROJECTILE_HEIGHT
            );
        }
    }

    private float getProjectileHeadX() {
        return projectileDirection > 0
            ? projectileX + ERASER_PROJECTILE_TRAIL_PIXELS * KAI_FRAME_SCALE
            : projectileX;
    }

    public enum MovementMode {
        WALKING(120f),
        RUNNING(240f),
        SPRINTING(480f);

        private final float maxSpeed;

        MovementMode(float maxSpeed) {
            this.maxSpeed = maxSpeed;
        }
    }
}
