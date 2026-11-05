package io.github.dwanego.ironbound;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    SpriteBatch spriteBatch;
    FitViewport viewport;
    OrthographicCamera camera;
    @Override
    public void create() {
        setScreen(new MainMenu(this));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render() {
        // organize code into three methods
        //ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        super.render();
        input();
        logic();
        draw();
    }

    private void input() {

    }

    private void logic() {

    }

    private void draw() {
        //viewport.apply();
        //spriteBatch.setProjectionMatrix(camera.combined);

        //spriteBatch.begin();

        // Draw Iron Bound's sprites here.

        //spriteBatch.end();
    }
    @Override
    public void dispose() {
        /*if (spriteBatch != null) {
            spriteBatch.dispose();
        }*/
    }
}
