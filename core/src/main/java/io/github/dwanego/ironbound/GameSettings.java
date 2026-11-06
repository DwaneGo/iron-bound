package io.github.dwanego.ironbound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import java.util.Locale;

public final class GameSettings {
    public enum Control {
        LEFT("LEFT"),
        RIGHT("RIGHT"),
        JUMP("JUMP"),
        SLIDE("SLIDE"),
        ATTACK("ATTACK");

        private final String label;

        Control(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final String PREFERENCES_NAME = "iron-bound-settings";

    private GameSettings() {
    }

    public static int getKey(int playerNumber, Control control) {
        return preferences().getInteger(
            getControlPreferenceKey(playerNumber, control),
            getDefaultKey(playerNumber, control)
        );
    }

    public static void setKey(int playerNumber, Control control, int keycode) {
        preferences().putInteger(
            getControlPreferenceKey(playerNumber, control),
            keycode
        ).flush();
    }

    public static boolean isFullscreen() {
        return preferences().getBoolean("fullscreen", false);
    }

    public static void setFullscreen(boolean fullscreen) {
        if (fullscreen != Gdx.graphics.isFullscreen()) {
            boolean changed;
            if (fullscreen) {
                changed = Gdx.graphics.setFullscreenMode(
                    Gdx.graphics.getDisplayMode()
                );
            } else {
                changed = Gdx.graphics.setWindowedMode(1280, 720);
            }

            if (!changed) {
                throw new IllegalStateException(
                    "Unable to switch to " + (fullscreen ? "fullscreen" : "windowed") + " mode"
                );
            }
        }

        preferences().putBoolean("fullscreen", fullscreen).flush();
    }

    public static void applySavedDisplayMode() {
        if (isFullscreen() && !Gdx.graphics.isFullscreen()
            && !Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode())) {
            throw new IllegalStateException("Unable to apply saved fullscreen mode");
        }
    }

    public static String getKeyName(int playerNumber, Control control) {
        return Keys.toString(getKey(playerNumber, control));
    }

    private static Preferences preferences() {
        return Gdx.app.getPreferences(PREFERENCES_NAME);
    }

    private static String getControlPreferenceKey(int playerNumber, Control control) {
        if (playerNumber != 1 && playerNumber != 2) {
            throw new IllegalArgumentException("Player number must be 1 or 2");
        }
        return "player" + playerNumber + "."
            + control.name().toLowerCase(Locale.ROOT);
    }

    private static int getDefaultKey(int playerNumber, Control control) {
        if (playerNumber == 1) {
            switch (control) {
                case LEFT:
                    return Keys.A;
                case RIGHT:
                    return Keys.D;
                case JUMP:
                    return Keys.W;
                case SLIDE:
                    return Keys.S;
                case ATTACK:
                    return Keys.Y;
                default:
                    throw new IllegalArgumentException("Unsupported control: " + control);
            }
        }

        if (playerNumber == 2) {
            switch (control) {
                case LEFT:
                    return Keys.LEFT;
                case RIGHT:
                    return Keys.RIGHT;
                case JUMP:
                    return Keys.UP;
                case SLIDE:
                    return Keys.DOWN;
                case ATTACK:
                    return Keys.NUMPAD_3;
                default:
                    throw new IllegalArgumentException("Unsupported control: " + control);
            }
        }

        throw new IllegalArgumentException("Player number must be 1 or 2");
    }
}
