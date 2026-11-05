
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

        Label message = new Label(
            "Coming Soon",
            new Label.LabelStyle(font, Color.WHITE)
        );

        TextButton backButton =
            new TextButton("BACK", buttonStyle);

        table.add(title)
            .padBottom(40);

        table.row();

        table.add(message)
            .padBottom(40);

        table.row();

        table.add(backButton)
            .width(250)
            .height(60);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(
                InputEvent event, float x, float y
            ) {
                showMainMenu();
            }
        });
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
