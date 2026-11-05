
package io.github.dwanego.ironbound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class WeaponSelectionScreen extends ScreenAdapter {

    private final Main game;

    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture;
    private Texture readyTexture;

    private TextButton.TextButtonStyle normalStyle;
    private TextButton.TextButtonStyle readyStyle;

    private boolean player1Ready = false;
    private boolean player2Ready = false;

    private TextButton startButton;

    public WeaponSelectionScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(800, 600));
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();

        buttonTexture = createTexture(
            new Color(0.20f, 0.23f, 0.28f, 1f)
        );

        readyTexture = createTexture(
            new Color(0.20f, 0.50f, 0.30f, 1f)
        );

        normalStyle = new TextButton.TextButtonStyle();
        normalStyle.up = new TextureRegionDrawable(buttonTexture);
        normalStyle.font = font;
        normalStyle.fontColor = Color.WHITE;

        readyStyle = new TextButton.TextButtonStyle();
        readyStyle.up = new TextureRegionDrawable(readyTexture);
        readyStyle.font = font;
        readyStyle.fontColor = Color.WHITE;

        createUI();
    }

    private Texture createTexture(Color color) {
        Pixmap pixmap = new Pixmap(
            1, 1, Pixmap.Format.RGBA8888
        );

        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

    private void createUI() {
        stage.clear();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        stage.addActor(mainTable);

        Label title = new Label(
            "IRON BOUND",
            new Label.LabelStyle(font, Color.GOLD)
        );
        title.setFontScale(2.5f);

        Label subtitle = new Label(
            "WEAPON SELECTION",
            new Label.LabelStyle(font, Color.LIGHT_GRAY)
        );
        subtitle.setFontScale(1.5f);

        mainTable.add(title).padBottom(20);
        mainTable.row();

        mainTable.add(subtitle).padBottom(50);
        mainTable.row();

        Table playersTable = new Table();

        playersTable.add(createPlayerPanel(1))
            .width(300)
            .padRight(40);

        playersTable.add(createPlayerPanel(2))
            .width(300);

        mainTable.add(playersTable).padBottom(40);
        mainTable.row();

        startButton = new TextButton(
            "WAITING FOR PLAYERS", normalStyle
        );

        mainTable.add(startButton)
            .width(300)
            .height(60)
            .padBottom(15);

        mainTable.row();

        TextButton backButton = new TextButton(
            "BACK", normalStyle
        );

        mainTable.add(backButton)
            .width(200)
            .height(50);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player1Ready && player2Ready) {
                    game.setScreen(new FightScene(game));
                    WeaponSelectionScreen.this.dispose();
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
                WeaponSelectionScreen.this.dispose();
            }
        });
    }

    private Table createPlayerPanel(int playerNumber) {
        Table table = new Table();

        Color playerColor = playerNumber == 1
            ? Color.GOLD
            : Color.CYAN;

        Label playerTitle = new Label(
            "PLAYER " + playerNumber,
            new Label.LabelStyle(font, playerColor)
        );

        playerTitle.setFontScale(1.8f);

        table.add(playerTitle).padBottom(35);
        table.row();

        Label placeholder = new Label(
            "WEAPONS COMING SOON",
            new Label.LabelStyle(font, Color.LIGHT_GRAY)
        );

        table.add(placeholder)
            .height(120)
            .padBottom(20);

        table.row();

        TextButton readyButton = new TextButton(
            "READY", normalStyle
        );

        table.add(readyButton)
            .width(260)
            .height(60);

        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (playerNumber == 1) {
                    player1Ready = !player1Ready;

                    readyButton.setStyle(
                        player1Ready ? readyStyle : normalStyle
                    );

                    readyButton.setText(
                        player1Ready ? "READY!" : "READY"
                    );

                } else {
                    player2Ready = !player2Ready;

                    readyButton.setStyle(
                        player2Ready ? readyStyle : normalStyle
                    );

                    readyButton.setText(
                        player2Ready ? "READY!" : "READY"
                    );
                }

                if (player1Ready && player2Ready) {
                    startButton.setText("START FIGHT");
                    startButton.setStyle(readyStyle);
                } else {
                    startButton.setText("WAITING FOR PLAYERS");
                    startButton.setStyle(normalStyle);
                }
            }
        });

        return table;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.06f, 0.08f, 1f);

        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        buttonTexture.dispose();
        readyTexture.dispose();
        font.dispose();
    }
}
