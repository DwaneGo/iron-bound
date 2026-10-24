package io.github.dwanego.ironbound.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {

    private static final float TIME_STEP = 1f / 60f;
    private static final float MAX_FRAME_TIME = 0.25f;

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

    private Texture texture;

    public Player(float x, float y) {
        this(x, y, 1f, 2f);
    }

    public Player(float x, float y, float weight, float drag) {
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

        this.forceX = 0f;
        this.forceY = 0f;

        this.velocityX = 0f;
        this.velocityY = 0f;

        calculateAcceleration();

        texture = new Texture("player.png");
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

            calculateAcceleration();

            velocityX += accelerationX * TIME_STEP;
            velocityY += accelerationY * TIME_STEP;

            x += velocityX * TIME_STEP;
            y += velocityY * TIME_STEP;

            accumulator -= TIME_STEP;
        }
    }

    public void setForce(float forceX, float forceY) {
        this.forceX = forceX;
        this.forceY = forceY;
    }

    public void render(SpriteBatch batch) {
        float alpha = accumulator / TIME_STEP;

        float renderX = previousX + (x - previousX) * alpha;
        float renderY = previousY + (y - previousY) * alpha;

        batch.draw(texture, renderX, renderY);
    }

    public void dispose() {
        texture.dispose();
    }
}
