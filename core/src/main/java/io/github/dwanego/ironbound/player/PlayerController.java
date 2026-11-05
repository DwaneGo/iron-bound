package io.github.dwanego.ironbound.player;

import com.badlogic.gdx.Gdx;

public class PlayerController {
    private final Player player;
    private final int leftKey;
    private final int rightKey;
    private final int jumpKey;
    private final int slideKey;
    private final int attackKey;

    public PlayerController(
        Player player,
        int leftKey,
        int rightKey,
        int jumpKey,
        int slideKey,
        int attackKey
    ) {
        this.player = player;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.jumpKey = jumpKey;
        this.slideKey = slideKey;
        this.attackKey = attackKey;
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
        player.setGuarding(Gdx.input.isKeyPressed(slideKey));
        if (Gdx.input.isKeyJustPressed(slideKey)) {
            player.slide();
        }
        if (Gdx.input.isKeyJustPressed(jumpKey)) {
            player.jump();
        }
        if (Gdx.input.isKeyJustPressed(attackKey)) {
            player.punch();
        }
    }
}
