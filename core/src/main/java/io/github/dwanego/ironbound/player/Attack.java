package io.github.dwanego.ironbound.player;

import com.badlogic.gdx.math.Rectangle;

public final class Attack {
    public static final Attack PUNCH = new Attack(
        "Punch",
        10f,
        0.14f,
        0.42f,
        44f,
        22f,
        -34f,
        50f
    );

    private final String name;
    private final float damage;
    private final float activeStart;
    private final float activeEnd;
    private final float hitboxWidth;
    private final float hitboxHeight;
    private final float hitboxXOffset;
    private final float hitboxYOffset;

    public Attack(
        String name,
        float damage,
        float activeStart,
        float activeEnd,
        float hitboxWidth,
        float hitboxHeight,
        float hitboxXOffset,
        float hitboxYOffset
    ) {
        if (name == null || name.isEmpty() || damage <= 0f || activeStart < 0f
            || activeEnd < activeStart || hitboxWidth <= 0f || hitboxHeight <= 0f) {
            throw new IllegalArgumentException("Invalid attack configuration.");
        }

        this.name = name;
        this.damage = damage;
        this.activeStart = activeStart;
        this.activeEnd = activeEnd;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.hitboxXOffset = hitboxXOffset;
        this.hitboxYOffset = hitboxYOffset;
    }

    public boolean isActiveAt(float time) {
        return time >= activeStart && time <= activeEnd;
    }

    public Rectangle createHitbox(float x, float y, float playerSize, boolean facingLeft) {
        float hitboxX = facingLeft
            ? x - hitboxWidth - hitboxXOffset
            : x + playerSize + hitboxXOffset;
        return new Rectangle(
            hitboxX,
            y + hitboxYOffset,
            hitboxWidth,
            hitboxHeight
        );
    }

    public String getName() {
        return name;
    }

    public float getDamage() {
        return damage;
    }
}
