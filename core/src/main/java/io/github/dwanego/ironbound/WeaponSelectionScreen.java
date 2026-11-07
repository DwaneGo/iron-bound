
package io.github.dwanego.ironbound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.dwanego.ironbound.player.Weapon;

public class WeaponSelectionScreen extends ScreenAdapter {

    private final Main game;

    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture;
    private Texture readyTexture;
    private Texture menuBackgroundTexture;
    private Texture testSpriteSheet;
    private Texture kaiSpriteSheet;
    private TextureRegion[][] testSpriteFrames;
    private TextureRegion[][] kaiSpriteFrames;

    private TextButton.TextButtonStyle normalStyle;
    private TextButton.TextButtonStyle readyStyle;

    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private int player1WeaponIndex;
    private int player2WeaponIndex;
    private boolean kaiWeaponsMode;

    private TextButton startButton;
    private TextButton player1ReadyButton;
    private TextButton player2ReadyButton;
    private TextButton modeButton;
    private TextButton player1PreviousButton;
    private TextButton player1NextButton;
    private TextButton player2PreviousButton;
    private TextButton player2NextButton;
    private Label player1WeaponLabel;
    private Label player2WeaponLabel;
    private Image player1Preview;
    private Image player2Preview;

    private static final Weapon[] WEAPONS = {
        Weapon.KATANA,
        Weapon.FRYING_PAN,
        Weapon.SCISSORS,
        Weapon.ELECTRIC_FAN,
        Weapon.SLIPPERS,
        Weapon.ERASER
    };

    public WeaponSelectionScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(800, 600));
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        menuBackgroundTexture = new Texture("win7.png");
        menuBackgroundTexture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );
        testSpriteSheet = new Texture("player-sheet.png");
        kaiSpriteSheet = new Texture("sprite_sheet_KaiCenat_combat_aligned.png");
        testSpriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        kaiSpriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        testSpriteFrames = TextureRegion.split(testSpriteSheet, 64, 64);
        kaiSpriteFrames = TextureRegion.split(kaiSpriteSheet, 224, 112);

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
        Image background = new Image(menuBackgroundTexture);
        background.setFillParent(true);
        background.setScaling(Scaling.fill);
        stage.addActor(background);
        background.toBack();

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
        subtitle.setFontScale(1.25f);

        mainTable.add(title).padBottom(8);
        mainTable.row();

        mainTable.add(subtitle).padBottom(12);
        mainTable.row();

        modeButton = new TextButton(getModeButtonText(), normalStyle);
        mainTable.add(modeButton)
            .width(340)
            .height(42)
            .padBottom(20);
        mainTable.row();

        Table playersTable = new Table();

        playersTable.add(createPlayerPanel(1))
            .width(300)
            .padRight(30);

        playersTable.add(createPlayerPanel(2))
            .width(300);

        mainTable.add(playersTable).padBottom(20);
        mainTable.row();

        startButton = new TextButton(
            "WAITING FOR PLAYERS", normalStyle
        );

        mainTable.add(startButton)
            .width(300)
            .height(50)
            .padBottom(10);

        mainTable.row();

        TextButton backButton = new TextButton(
            "BACK", normalStyle
        );

        mainTable.add(backButton)
            .width(200)
            .height(42);

        modeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                kaiWeaponsMode = !kaiWeaponsMode;
                player1Ready = false;
                player2Ready = false;
                resetReadyButton(player1ReadyButton);
                resetReadyButton(player2ReadyButton);
                updateWeaponSelection();
                updateStartButton();
            }
        });

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player1Ready && player2Ready) {
                    game.setScreen(new FightScene(
                        game,
                        kaiWeaponsMode,
                        kaiWeaponsMode ? getSelectedWeaponType(1) : Weapon.PUNCH,
                        kaiWeaponsMode ? getSelectedWeaponType(2) : Weapon.PUNCH
                    ));
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

        updateWeaponSelection();
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

        playerTitle.setFontScale(1.5f);

        table.add(playerTitle).padBottom(8);
        table.row();

        Image preview = new Image(getPreviewRegion(playerNumber));
        preview.setScaling(Scaling.fit);
        if (playerNumber == 1) {
            player1Preview = preview;
        } else {
            player2Preview = preview;
        }
        table.add(preview).width(230).height(105).padBottom(8);
        table.row();

        Table weaponPicker = new Table();
        TextButton previousWeaponButton = new TextButton("<", normalStyle);
        TextButton nextWeaponButton = new TextButton(">", normalStyle);
        Label weaponLabel = new Label(
            getSelectedWeapon(playerNumber),
            new Label.LabelStyle(font, Color.LIGHT_GRAY)
        );
        weaponLabel.setAlignment(Align.center);
        if (playerNumber == 1) {
            player1PreviousButton = previousWeaponButton;
            player1NextButton = nextWeaponButton;
            player1WeaponLabel = weaponLabel;
        } else {
            player2PreviousButton = previousWeaponButton;
            player2NextButton = nextWeaponButton;
            player2WeaponLabel = weaponLabel;
        }

        weaponPicker.add(previousWeaponButton).width(42).height(42);
        weaponPicker.add(weaponLabel).width(190).height(42);
        weaponPicker.add(nextWeaponButton).width(42).height(42);
        table.add(weaponPicker).padBottom(10);

        table.row();

        TextButton readyButton = new TextButton(
            "READY", normalStyle
        );
        if (playerNumber == 1) {
            player1ReadyButton = readyButton;
        } else {
            player2ReadyButton = readyButton;
        }

        table.add(readyButton)
            .width(250)
            .height(50);

        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (playerNumber == 1) {
                    player1Ready = !player1Ready;
                } else {
                    player2Ready = !player2Ready;
                }

                boolean ready = playerNumber == 1 ? player1Ready : player2Ready;
                readyButton.setStyle(ready ? readyStyle : normalStyle);
                readyButton.setText(ready ? "READY!" : "READY");
                updateStartButton();
            }
        });

        previousWeaponButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cycleWeapon(playerNumber, -1, weaponLabel);
            }
        });

        nextWeaponButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cycleWeapon(playerNumber, 1, weaponLabel);
            }
        });

        return table;
    }

    private String getSelectedWeapon(int playerNumber) {
        int weaponIndex = playerNumber == 1 ? player1WeaponIndex : player2WeaponIndex;
        return kaiWeaponsMode ? WEAPONS[weaponIndex].getLabel() : Weapon.PUNCH.getLabel();
    }

    private void cycleWeapon(int playerNumber, int direction, Label weaponLabel) {
        if (!kaiWeaponsMode) {
            return;
        }
        if (playerNumber == 1) {
            player1WeaponIndex = (player1WeaponIndex + direction + WEAPONS.length)
                % WEAPONS.length;
            if (player1Ready) {
                player1Ready = false;
                resetReadyButton(player1ReadyButton);
            }
        } else {
            player2WeaponIndex = (player2WeaponIndex + direction + WEAPONS.length)
                % WEAPONS.length;
            if (player2Ready) {
                player2Ready = false;
                resetReadyButton(player2ReadyButton);
            }
        }

        weaponLabel.setText(getSelectedWeapon(playerNumber));
        updatePreview(playerNumber);
        updateStartButton();
    }

    private TextureRegion getPreviewRegion(int playerNumber) {
        if (!kaiWeaponsMode) {
            return testSpriteFrames[0][0];
        }
        Weapon weapon = getSelectedWeaponType(playerNumber);
        return kaiSpriteFrames[weapon.getAnimationRow()][weapon.getPreviewFrame()];
    }

    private Weapon getSelectedWeaponType(int playerNumber) {
        int weaponIndex = playerNumber == 1 ? player1WeaponIndex : player2WeaponIndex;
        return WEAPONS[weaponIndex];
    }

    private void updatePreview(int playerNumber) {
        Image preview = playerNumber == 1 ? player1Preview : player2Preview;
        preview.setDrawable(new TextureRegionDrawable(getPreviewRegion(playerNumber)));
    }

    private String getModeButtonText() {
        return kaiWeaponsMode
            ? "MODE: KAI CENAT WEAPONS"
            : "MODE: TEST (CURRENT SPRITE SHEET)";
    }

    private void updateWeaponSelection() {
        modeButton.setText(getModeButtonText());
        player1WeaponLabel.setText(getSelectedWeapon(1));
        player2WeaponLabel.setText(getSelectedWeapon(2));
        player1PreviousButton.setDisabled(!kaiWeaponsMode);
        player1NextButton.setDisabled(!kaiWeaponsMode);
        player2PreviousButton.setDisabled(!kaiWeaponsMode);
        player2NextButton.setDisabled(!kaiWeaponsMode);
        updatePreview(1);
        updatePreview(2);
    }

    private void resetReadyButton(TextButton readyButton) {
        readyButton.setStyle(normalStyle);
        readyButton.setText("READY");
    }

    private void updateStartButton() {
        if (player1Ready && player2Ready) {
            startButton.setText("START FIGHT");
            startButton.setStyle(readyStyle);
        } else {
            startButton.setText("WAITING FOR PLAYERS");
            startButton.setStyle(normalStyle);
        }
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
        menuBackgroundTexture.dispose();
        testSpriteSheet.dispose();
        kaiSpriteSheet.dispose();
        font.dispose();
    }
}
