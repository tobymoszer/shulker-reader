package tobymoszer.shulkerreader.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import tobymoszer.shulkerreader.ShulkerReader;

public class ShulkerReaderScreen extends AbstractContainerScreen<ShulkerReaderScreenHandler> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
		ShulkerReader.MOD_ID,
		"textures/gui/container/shulker_reader.png"
	);

	public ShulkerReaderScreen(ShulkerReaderScreenHandler handler, Inventory inventory, Component title) {
		super(handler, inventory, title, 176, 133);
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = 8;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractBackground(graphics, mouseX, mouseY, delta);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		graphics.blit(
			RenderPipelines.GUI_TEXTURED,
			TEXTURE,
			x,
			y,
			0.0f,
			0.0f,
			this.imageWidth,
			this.imageHeight,
			256,
			256
		);
	}
}
