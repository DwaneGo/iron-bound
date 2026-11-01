
package io.github.dwanego.ironbound;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.dwanego.ironbound.player.Player;
import io.github.dwanego.ironbound.player.PlayerController;

public class FirstScreen extends ScreenAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Player player;
    private PlayerController playerController;
    private Player player2;
    private PlayerController player2Controller;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        player = new Player(100f, 100f);
        playerController = new PlayerController(
            player,
            Keys.A,
            Keys.D,
            Keys.W,
            Keys.S
        );
        player2 = new Player(600f, 100f, true);
        player2Controller = new PlayerController(
            player2,
            Keys.LEFT,
            Keys.RIGHT,
            Keys.UP,
            Keys.DOWN
        );
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1f);

        playerController.update();
        player.update(delta);
        player2Controller.update();
        player2.update(delta);

        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.65f, 0.2f, 1f);
        float groundBottom = camera.position.y - viewport.getWorldHeight() / 2f;
        shapeRenderer.rect(
            camera.position.x - viewport.getWorldWidth() / 2f,
            groundBottom,
            viewport.getWorldWidth(),
            100f - groundBottom
        );
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        player.render(batch);
        player2.render(batch);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        player.dispose();
        player2.dispose();
    }
}
