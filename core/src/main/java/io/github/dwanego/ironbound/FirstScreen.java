
package io.github.dwanego.ironbound;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.dwanego.ironbound.player.Player;
import io.github.dwanego.ironbound.player.PlayerController;

public class FirstScreen extends ScreenAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;
    private ShaderProgram hitFlashShader;
    private Player player;
    private PlayerController playerController;
    private Player player2;
    private PlayerController player2Controller;
    private boolean showHitboxes = true;;

    private static final float HEALTH_BAR_HEIGHT = 20f;
    private static final float HEALTH_BAR_MARGIN = 24f;
    private static final float HEALTH_BAR_CENTER_GAP = 32f;
    private static final float CAMERA_SMOOTHING = 5f;
    private static final float CAMERA_MIN_ZOOM = 1f;
    private static final float CAMERA_MAX_ZOOM = 2.5f;
    private static final float CAMERA_HORIZONTAL_PADDING = 160f;
    private static final float CAMERA_VERTICAL_PADDING = 96f;
    private static final float CAMERA_VERTICAL_OFFSET = 136f;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(800, 600, camera);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        hitFlashShader = new ShaderProgram(
            "attribute vec4 a_position;\n"
                + "attribute vec4 a_color;\n"
                + "attribute vec2 a_texCoord0;\n"
                + "uniform mat4 u_projTrans;\n"
                + "varying vec4 v_color;\n"
                + "varying vec2 v_texCoords;\n"
                + "void main() {\n"
                + "  v_color = a_color;\n"
                + "  v_texCoords = a_texCoord0;\n"
                + "  gl_Position = u_projTrans * a_position;\n"
                + "}\n",
            "#ifdef GL_ES\n"
                + "precision mediump float;\n"
                + "#endif\n"
                + "varying vec4 v_color;\n"
                + "varying vec2 v_texCoords;\n"
                + "uniform sampler2D u_texture;\n"
                + "uniform float u_flash;\n"
                + "void main() {\n"
                + "  vec4 texColor = texture2D(u_texture, v_texCoords);\n"
                + "  vec3 flashColor = mix(texColor.rgb, vec3(1.0), u_flash);\n"
                + "  gl_FragColor = vec4(flashColor, texColor.a) * v_color;\n"
                + "}\n"
        );
        if (!hitFlashShader.isCompiled()) {
            throw new IllegalStateException(
                "Hit flash shader compilation failed: " + hitFlashShader.getLog()
            );
        }

        player = new Player(100f, 100f);
        playerController = new PlayerController(
            player,
            Keys.A,
            Keys.D,
            Keys.W,
            Keys.S,
            Keys.Y
        );
        player2 = new Player(600f, 100f, true);
        player2Controller = new PlayerController(
            player2,
            Keys.LEFT,
            Keys.RIGHT,
            Keys.UP,
            Keys.DOWN,
            Keys.NUMPAD_3
        );
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1f);

        if (Gdx.input.isKeyJustPressed(Keys.H)) {
            showHitboxes = !showHitboxes;
        }

        playerController.update();
        player.update(delta);
        player2Controller.update();
        player2.update(delta);
        player.tryHit(player2);
        player2.tryHit(player);
        updateCamera(delta);

        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.65f, 0.2f, 1f);
        float visibleWidth = getVisibleWorldWidth();
        float visibleHeight = getVisibleWorldHeight();
        float groundBottom = camera.position.y - visibleHeight / 2f;
        shapeRenderer.rect(
            camera.position.x - visibleWidth / 2f,
            groundBottom,
            visibleWidth,
            100f - groundBottom
        );
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.setShader(hitFlashShader);
        hitFlashShader.setUniformf("u_flash", player.getHitFlashAmount());
        player.render(batch);
        hitFlashShader.setUniformf("u_flash", player2.getHitFlashAmount());
        player2.render(batch);
        batch.setShader(null);

        batch.end();
        renderHealthBars();
        if (showHitboxes) {
            renderHitboxes();
        }
    }

    private void updateCamera(float delta) {
        float player1CenterX = player.getX() + 64f;
        float player2CenterX = player2.getX() + 64f;
        float player1CenterY = player.getY() + 64f;
        float player2CenterY = player2.getY() + 64f;
        float targetX = (player1CenterX + player2CenterX) / 2f;
        float targetY = (player1CenterY + player2CenterY) / 2f + CAMERA_VERTICAL_OFFSET;

        float horizontalSpan = Math.abs(player1CenterX - player2CenterX)
            + 128f
            + CAMERA_HORIZONTAL_PADDING;
        float verticalSpan = Math.abs(player1CenterY - player2CenterY)
            + 128f
            + CAMERA_VERTICAL_PADDING;
        float targetZoom = Math.max(
            horizontalSpan / (viewport.getWorldWidth() * 0.8f),
            verticalSpan / (viewport.getWorldHeight() * 0.75f)
        );
        targetZoom = MathUtils.clamp(targetZoom, CAMERA_MIN_ZOOM, CAMERA_MAX_ZOOM);

        float smoothing = 1f - (float) Math.exp(-CAMERA_SMOOTHING * Math.max(delta, 0f));
        camera.position.x = MathUtils.lerp(camera.position.x, targetX, smoothing);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, smoothing);
        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, smoothing);
        camera.update();
    }

    private float getVisibleWorldWidth() {
        return viewport.getWorldWidth() * camera.zoom;
    }

    private float getVisibleWorldHeight() {
        return viewport.getWorldHeight() * camera.zoom;
    }

    private void renderHitboxes() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        Rectangle playerHitbox = player.getAttackHitbox();
        if (playerHitbox != null) {
            shapeRenderer.rect(
                playerHitbox.x,
                playerHitbox.y,
                playerHitbox.width,
                playerHitbox.height
            );
        }
        Rectangle player2Hitbox = player2.getAttackHitbox();
        if (player2Hitbox != null) {
            shapeRenderer.rect(
                player2Hitbox.x,
                player2Hitbox.y,
                player2Hitbox.width,
                player2Hitbox.height
            );
        }
        shapeRenderer.setColor(Color.CYAN);
        Rectangle playerHurtbox = player.getHurtbox();
        shapeRenderer.rect(
            playerHurtbox.x,
            playerHurtbox.y,
            playerHurtbox.width,
            playerHurtbox.height
        );
        Rectangle player2Hurtbox = player2.getHurtbox();
        shapeRenderer.rect(
            player2Hurtbox.x,
            player2Hurtbox.y,
            player2Hurtbox.width,
            player2Hurtbox.height
        );
        shapeRenderer.end();
    }

    private void renderHealthBars() {
        float visibleWidth = getVisibleWorldWidth();
        float visibleHeight = getVisibleWorldHeight();
        float screenLeft = camera.position.x - visibleWidth / 2f;
        float screenTop = camera.position.y + visibleHeight / 2f;
        float barY = screenTop - HEALTH_BAR_MARGIN - HEALTH_BAR_HEIGHT;
        float barWidth = (
            visibleWidth
                - HEALTH_BAR_MARGIN * 2f
                - HEALTH_BAR_CENTER_GAP
        ) / 2f;
        float leftBarX = screenLeft + HEALTH_BAR_MARGIN;
        float rightBarX = leftBarX + barWidth + HEALTH_BAR_CENTER_GAP;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.12f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(leftBarX, barY, barWidth, HEALTH_BAR_HEIGHT);
        shapeRenderer.rect(rightBarX, barY, barWidth, HEALTH_BAR_HEIGHT);
        shapeRenderer.setColor(0.88f, 0.12f, 0.12f, 1f);
        shapeRenderer.rect(
            leftBarX,
            barY,
            barWidth * player.getHealth() / player.getMaxHealth(),
            HEALTH_BAR_HEIGHT
        );
        float rightHealthWidth = barWidth * player2.getHealth() / player2.getMaxHealth();
        shapeRenderer.rect(
            rightBarX + barWidth - rightHealthWidth,
            barY,
            rightHealthWidth,
            HEALTH_BAR_HEIGHT
        );
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(leftBarX, barY, barWidth, HEALTH_BAR_HEIGHT);
        shapeRenderer.rect(rightBarX, barY, barWidth, HEALTH_BAR_HEIGHT);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        hitFlashShader.dispose();
        player.dispose();
        player2.dispose();
    }
}
