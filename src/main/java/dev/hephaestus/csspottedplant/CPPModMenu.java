package dev.hephaestus.csspottedplant;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CPPModMenu extends Screen {
    public static final ModMenuApi API = new ModMenuApi() {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return CPPModMenu::new;
        }
    };

    private static final Text text = new TranslatableText("menu.css-potted-plant.config");

    private final Screen parent;

    protected CPPModMenu(Screen parent) {
        super(LiteralText.EMPTY);
        this.parent = parent;
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);

        if (parent != null) {
            parent.init(client, width, height);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, text, width / 2, height / 2 - textRenderer.fontHeight, -1);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        if (parent != null) {
            this.parent.renderBackground(matrices);
        } else {
            super.renderBackground(matrices);
        }
    }
}
