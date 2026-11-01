package io.github.dwanego.ironbound.player;

import com.badlogic.gdx.Gdx;

public class PlayerController {
    private final Player player;
    private final int leftKey;
    private final int rightKey;
    private final int jumpKey;
    private final int slideKey;

    public PlayerController(Player player, int leftKey, int rightKey, int jumpKey, int slideKey) {
        this.player = player;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.jumpKey = jumpKey;
        this.slideKey = slideKey;
    }

    public void update() {
        float direction = 0f;
        if (Gdx.input.isKeyPressed(leftKey)) {
            direction -= 1f;
        }
        if (Gdx.input.isKeyPressed(rightKey)) {
            direction += 1f;
        }

        player.setMovement(direction, Player.MovementMode.SPRINTING);
        if (Gdx.input.isKeyJustPressed(slideKey)) {
            player.slide();
        }
        if (Gdx.input.isKeyJustPressed(jumpKey)) {
            player.jump();
        }
    }
}
