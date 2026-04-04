package net.rslover521.createTrainStationSpeakers.content.station_speakers.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.rslover521.createTrainStationSpeakers.content.network.CTSPackets;

public class StationSpeakerConfigScreen extends AbstractContainerScreen<StationSpeakerConfigMenu> {
    private EditBox speakerNameBox;
    private EditBox stationNameBox;

    // Create the client-side editor screen for the speaker configuration menu.
    public StationSpeakerConfigScreen(StationSpeakerConfigMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 196;
        imageHeight = 166;
        inventoryLabelY = imageHeight - 94;
    }

    // Build the editable text boxes and action buttons once the screen opens.
    @Override
    protected void init() {
        super.init();
        titleLabelX = 14;
        titleLabelY = 10;

        speakerNameBox = new EditBox(font, leftPos + 14, topPos + 34, 168, 18,
                Component.translatable("gui.create_train_station_speakers.station_speaker.speaker_name"));
        speakerNameBox.setValue(menu.getSpeakerName());
        speakerNameBox.setMaxLength(64);
        speakerNameBox.setTextColor(0xECF8F8);
        speakerNameBox.setTextColorUneditable(0xECF8F8);
        speakerNameBox.setBordered(false);
        addRenderableWidget(speakerNameBox);

        stationNameBox = new EditBox(font, leftPos + 14, topPos + 78, 168, 18,
                Component.translatable("gui.create_train_station_speakers.station_speaker.station_name"));
        stationNameBox.setValue(menu.getLinkedStationName());
        stationNameBox.setMaxLength(128);
        stationNameBox.setTextColor(0xECF8F8);
        stationNameBox.setTextColorUneditable(0xECF8F8);
        stationNameBox.setBordered(false);
        addRenderableWidget(stationNameBox);

        // Save the edited values back to the server and then close the screen.
        addRenderableWidget(Button.builder(Component.translatable("gui.create_train_station_speakers.station_speaker.save"),
                        button -> saveAndClose())
                .bounds(leftPos + 106, topPos + 132, 76, 20)
                .build());

        // Close the screen without changing the server-side configuration.
        addRenderableWidget(Button.builder(Component.translatable("gui.create_train_station_speakers.station_speaker.cancel"),
                        button -> onClose())
                .bounds(leftPos + 14, topPos + 132, 76, 20)
                .build());

        setInitialFocus(speakerNameBox);
    }

    // Draw the menu background and the custom panel used by the CRN-inspired editor layout.
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xF0111C1F);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + imageHeight - 4, 0xF0162C31);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + 22, 0xF0246A73);
        graphics.fill(leftPos + 12, topPos + 32, leftPos + 184, topPos + 54, 0xCC0B2E36);
        graphics.fill(leftPos + 12, topPos + 76, leftPos + 184, topPos + 98, 0xCC0B2E36);
        graphics.fill(leftPos + 12, topPos + 106, leftPos + 184, topPos + 124, 0x88214247);
    }

    // Draw labels and helper text on top of the background after the base screen renders.
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0xE7F7F7, false);
        graphics.drawString(font,
                Component.translatable("gui.create_train_station_speakers.station_speaker.speaker_name"),
                14, 24, 0x8DC9C9, false);
        graphics.drawString(font,
                Component.translatable("gui.create_train_station_speakers.station_speaker.station_name"),
                14, 68, 0x8DC9C9, false);
        graphics.drawString(font,
                Component.translatable("gui.create_train_station_speakers.station_speaker.station_hint"),
                14, 108, 0x8DC9C9, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x8DC9C9, false);
    }

    // Render the full screen, including tooltips and the text boxes.
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    // Let the enter key save the current form values for faster iteration while testing.
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Send the current text box values to the server and close the editor.
    private void saveAndClose() {
        CTSPackets.CHANNEL.sendToServer(new UpdateStationSpeakerConfigPacket(
                menu.getSpeakerPos(),
                speakerNameBox.getValue(),
                stationNameBox.getValue()
        ));
        onClose();
    }
}
