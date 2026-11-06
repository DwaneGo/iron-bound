
package io.github.dwanego.ironbound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
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

public class MainMenu extends ScreenAdapter {

    private final Main game;

    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture;

    private TextButton.TextButtonStyle buttonStyle;

    public MainMenu(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(800, 600));
        Gdx.input.setInputProcessor(stage);

        // Font
        font = new BitmapFont();
        font.getData().setScale(2f);

        // Button background
        Pixmap pixmap = new Pixmap(
            1, 1, Pixmap.Format.RGBA8888
        );

        pixmap.setColor(
            new Color(0.18f, 0.19f, 0.22f, 1f)
        );

        pixmap.fill();

        buttonTexture = new Texture(pixmap);
        pixmap.dispose();

        // Button style
        buttonStyle = new TextButton.TextButtonStyle();

        buttonStyle.up =
            new TextureRegionDrawable(buttonTexture);

        buttonStyle.down = buttonStyle.up;
        buttonStyle.over = buttonStyle.up;

        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.GOLD;

        showMainMenu();
    }

    // MAIN MENU

    private void showMainMenu() {

        stage.clear();

        // Main container
        Table table = new Table();
        table.setFillParent(true);

        // Center alignment
        table.center();

        stage.addActor(table);

        // Game title / Logo
        Label.LabelStyle titleStyle =
            new Label.LabelStyle(font, Color.GOLD);

        Label title = new Label(
            "IRON BOUND", titleStyle
        );

        title.setFontScale(1.8f);

        // Logo above the menu
        table.add(title)
            .padBottom(60)
            .center();

        table.row();

        // Menu buttons
        TextButton playButton =
            new TextButton("PLAY", buttonStyle);

        TextButton optionsButton =
            new TextButton("OPTIONS", buttonStyle);

        TextButton creditsButton =
            new TextButton("CREDITS", buttonStyle);

        // Column layout
        table.add(playButton)
            .width(250)
            .height(60)
            .padBottom(15);

        table.row();

        table.add(optionsButton)
            .width(250)
            .height(60)
            .padBottom(15);

        table.row();

        table.add(creditsButton)
            .width(250)
            .height(60);


        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event, float x, float y
            ){
                game.setScreen(new WeaponSelectionScreen(game));
                MainMenu.this.dispose();
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event, float x, float y
            ) {
                showOptions();
            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event, float x, float y
            ) {
                showCredits();
            }
        });
    }

    private void showOptions() {

        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        stage.addActor(table);

        Label title = new Label(
            "OPTIONS",
            new Label.LabelStyle(font, Color.GOLD)
        );

        title.setFontScale(1.5f);

        TextButton displayModeButton = new TextButton(
            Gdx.graphics.isFullscreen() ? "DISPLAY: FULLSCREEN" : "DISPLAY: WINDOWED",
            buttonStyle
        );

        Label controlsHint = new Label(
            "Click a key to rebind it. Press ESC to cancel.",
            new Label.LabelStyle(font, Color.LIGHT_GRAY)
        );

        Table controlsTable = new Table();
        controlsTable.add(createControlsColumn(1)).width(300).padRight(30);
        controlsTable.add(createControlsColumn(2)).width(300);

        TextButton backButton =
            new TextButton("BACK", buttonStyle);

        table.add(title)
            .padBottom(15);

        table.row();

        table.add(displayModeButton)
            .width(300)
            .height(50)
            .padBottom(10);

        table.row();

        table.add(controlsHint).padBottom(8);
        table.row();

        table.add(controlsTable).padBottom(10);
        table.row();

        table.add(backButton)
            .width(250)
            .height(50);

        displayModeButton.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event, float x, float y
            ) {
                GameSettings.setFullscreen(!Gdx.graphics.isFullscreen());
                showOptions();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event, float x, float y
            ) {
                showMainMenu();
            }
        });
    }

    private Table createControlsColumn(int playerNumber) {
        Table column = new Table();
        Color playerColor = playerNumber == 1 ? Color.GOLD : Color.CYAN;
        Label playerTitle = new Label(
            "PLAYER " + playerNumber,
            new Label.LabelStyle(font, playerColor)
        );
        column.add(playerTitle).colspan(2).padBottom(10);
        column.row();

        for (GameSettings.Control control : GameSettings.Control.values()) {
            Label controlLabel = new Label(
                control.getLabel(),
                new Label.LabelStyle(font, Color.WHITE)
            );
            TextButton keyButton = new TextButton(
                GameSettings.getKeyName(playerNumber, control),
                buttonStyle
            );
            column.add(controlLabel).left().width(115).height(42);
            column.add(keyButton).width(125).height(38).padBottom(4);
            column.row();

            keyButton.addListener(new ClickListener() {
                @Override
                public void clicked(
                    InputEvent event, float x, float y
                ) {
                    keyButton.setText("PRESS KEY");
                    Gdx.input.setInputProcessor(new InputAdapter() {
                        @Override
                        public boolean keyDown(int keycode) {
                            if (keycode != Keys.ESCAPE) {
                                GameSettings.setKey(playerNumber, control, keycode);
                            }
                            Gdx.input.setInputProcessor(stage);
                            showOptions();
                            return true;
                        }
                    });
                }
            });
        }

        return column;
    }


    private void showCredits() {

        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        stage.addActor(table);

        Label title = new Label(
            "CREDITS",
            new Label.LabelStyle(font, Color.GOLD)
        );

        title.setFontScale(1.5f);

        table.add(title)
            .padBottom(35);

        table.row();

        String[] members = {
            "Dwane",
            "Iorne",
            "Jhoemar",
            "James",
            "Harvey"
        };

        Label.LabelStyle memberStyle =
            new Label.LabelStyle(font, Color.WHITE);

        for (String member : members) {

            Label name = new Label(
                member, memberStyle
            );

            table.add(name)
                .padBottom(12)
                .center();

            table.row();
        }

        TextButton backButton =
            new TextButton("BACK", buttonStyle);

        table.add(backButton)
            .width(250)
            .height(60)
            .padTop(30);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event, float x, float y
            ) {
                showMainMenu();
            }
        });
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(
            0.06f, 0.07f, 0.09f, 1f
        );

        stage.getViewport().apply();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(
            width, height, true
        );
    }

    @Override
    public void dispose() {

        stage.dispose();
        buttonTexture.dispose();
        font.dispose();
    }
}
