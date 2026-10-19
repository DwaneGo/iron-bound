
package io.github.dwanego.ironbound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class FirstScreen extends ScreenAdapter {
    private SpriteBatch batch;
    private Texture playerSheet;
    private TextureRegion playerFrame;
    private OrthographicCamera camera;
    private FitViewport viewport;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);

        batch = new SpriteBatch();

        playerSheet = new Texture(
            Gdx.files.internal("player-sheet.png")
        );

        // Select the first 64x64 cell at the top-left.
        playerFrame = new TextureRegion(
            playerSheet, 0, 0, 64, 64
        );
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Draw the selected frame, enlarged to 128x128.
        batch.draw(playerFrame, 100, 100, 128, 128);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        playerSheet.dispose();
    }
}
