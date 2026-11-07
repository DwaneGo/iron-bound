package io.github.dwanego.ironbound.player;

public enum Weapon {
    PUNCH(
        "PUNCH",
        -1,
        0,
        -1,
        Attack.PUNCH
    ),
    KATANA(
        "KATANA",
        10,
        14,
        10,
        new Attack("Katana", 24f, 0.70f, 0.92f, 118f, 34f, -52f, 42f)
    ),
    FRYING_PAN(
        "FRYING PAN",
        11,
        12,
        8,
        new Attack("Frying Pan", 18f, 0.56f, 0.77f, 70f, 30f, -52f, 42f)
    ),
    SCISSORS(
        "SCISSORS",
        12,
        13,
        10,
        new Attack("Scissors", 20f, 0.60f, 0.88f, 64f, 26f, -50f, 48f)
    ),
    ELECTRIC_FAN(
        "ELECTRIC FAN",
        13,
        13,
        7,
        new Attack("Electric Fan", 16f, 0.63f, 0.85f, 78f, 32f, -52f, 40f)
    ),
    SLIPPERS(
        "SLIPPERS",
        15,
        12,
        8,
        new Attack("Slippers", 12f, 0.56f, 0.77f, 56f, 24f, -48f, 44f)
    ),
    ERASER(
        "ERASER",
        16,
        10,
        7,
        new Attack("Eraser", 14f, 0.63f, 0.69f, 38f, 28f, -52f, 42f)
    );

    private final String label;
    private final int animationRow;
    private final int animationFrameCount;
    private final int previewFrame;
    private final Attack attack;

    Weapon(
        String label,
        int animationRow,
        int animationFrameCount,
        int previewFrame,
        Attack attack
    ) {
        this.label = label;
        this.animationRow = animationRow;
        this.animationFrameCount = animationFrameCount;
        this.previewFrame = previewFrame;
        this.attack = attack;
    }

    public String getLabel() {
        return label;
    }

    public int getAnimationRow() {
        return animationRow;
    }

    public int getAnimationFrameCount() {
        return animationFrameCount;
    }

    public int getPreviewFrame() {
        return previewFrame;
    }

    public Attack getAttack() {
        return attack;
    }
}
