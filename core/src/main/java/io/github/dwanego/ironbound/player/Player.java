package io.github.dwanego.ironbound.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Player {

    private float x;
    private float y;

    private Texture texture;
    public Player(float x, float y) {
        this.x = x;
        this.y = y;

        texture = new Texture("player.png");
    }

    public void update(float delta) {
        // movement later
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }
}
