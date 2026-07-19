package com.mediev.charcreation.client.gui;

import com.mediev.charcreation.data.Background;
import com.mediev.charcreation.data.Gender;
import com.mediev.charcreation.data.Nationality;
import com.mediev.charcreation.network.NetworkingConstants;
import com.mediev.charcreation.validation.CharacterValidationResult;
import com.mediev.charcreation.validation.CharacterValidator;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public final class CharacterCreationScreen extends Screen {
    private TextFieldWidget firstNameField;
    private TextFieldWidget lastNameField;
    private TextFieldWidget dayField;
    private TextFieldWidget monthField;
    private TextFieldWidget yearField;
    private ThemedButtonWidget nationalityButton;
    private ThemedButtonWidget genderButton;
    private ThemedButtonWidget backgroundButton;
    private ThemedButtonWidget finishButton;

    private Nationality nationality = Nationality.values()[0];
    private Gender gender = Gender.values()[0];
    private Background background = Background.values()[0];

    private String errorMessage = "";
    private String serverError = "";

    public CharacterCreationScreen() {
        super(Text.literal("Character Creation"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int panelWidth = 320;
        int left = centerX - panelWidth / 2;
        int top = 40;

        firstNameField = new TextFieldWidget(textRenderer, left, top + 30, panelWidth, 20, Text.literal("First Name"));
        firstNameField.setMaxLength(16);
        firstNameField.setPlaceholder(Text.literal("First Name"));

        lastNameField = new TextFieldWidget(textRenderer, left, top + 60, panelWidth, 20, Text.literal("Last Name"));
        lastNameField.setMaxLength(16);
        lastNameField.setPlaceholder(Text.literal("Last Name"));

        nationalityButton = ThemedButtonWidget.create(left, top + 90, panelWidth, 20,
                Text.literal(nationality.getDisplayName()), btn -> cycleNationality());

        genderButton = ThemedButtonWidget.create(left, top + 120, panelWidth, 20,
                Text.literal(gender.getDisplayName()), btn -> cycleGender());

        int dobWidth = (panelWidth - 20) / 3;
        dayField = new TextFieldWidget(textRenderer, left, top + 150, dobWidth, 20, Text.literal("Day"));
        dayField.setMaxLength(2);
        dayField.setPlaceholder(Text.literal("DD"));

        monthField = new TextFieldWidget(textRenderer, left + dobWidth + 10, top + 150, dobWidth, 20, Text.literal("Month"));
        monthField.setMaxLength(2);
        monthField.setPlaceholder(Text.literal("MM"));

        yearField = new TextFieldWidget(textRenderer, left + (dobWidth + 10) * 2, top + 150, dobWidth, 20, Text.literal("Year"));
        yearField.setMaxLength(4);
        yearField.setPlaceholder(Text.literal("YYYY"));

        backgroundButton = ThemedButtonWidget.create(left, top + 180, panelWidth, 20,
                Text.literal(background.getDisplayName()), btn -> cycleBackground());

        finishButton = ThemedButtonWidget.create(left, top + 220, panelWidth, 24, Text.literal("Finish"), btn -> submit());

        addDrawableChild(firstNameField);
        addDrawableChild(lastNameField);
        addDrawableChild(nationalityButton);
        addDrawableChild(genderButton);
        addDrawableChild(dayField);
        addDrawableChild(monthField);
        addDrawableChild(yearField);
        addDrawableChild(backgroundButton);
        addDrawableChild(finishButton);
    }

    @Override
    public void tick() {
        updateValidation();
    }

    private void cycleNationality() {
        Nationality[] values = Nationality.values();
        nationality = values[(nationality.ordinal() + 1) % values.length];
        nationalityButton.setMessage(Text.literal(nationality.getDisplayName()));
    }

    private void cycleGender() {
        Gender[] values = Gender.values();
        gender = values[(gender.ordinal() + 1) % values.length];
        genderButton.setMessage(Text.literal(gender.getDisplayName()));
    }

    private void cycleBackground() {
        Background[] values = Background.values();
        background = values[(background.ordinal() + 1) % values.length];
        backgroundButton.setMessage(Text.literal(background.getDisplayName()));
    }

    private int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void updateValidation() {
        CharacterValidationResult result = CharacterValidator.validate(
                firstNameField.getText(),
                lastNameField.getText(),
                parseIntSafe(dayField.getText()),
                parseIntSafe(monthField.getText()),
                parseIntSafe(yearField.getText())
        );
        errorMessage = result.isValid() ? "" : result.getErrorMessage();
        finishButton.active = result.isValid();
    }

    private void submit() {
        CharacterValidationResult result = CharacterValidator.validate(
                firstNameField.getText(),
                lastNameField.getText(),
                parseIntSafe(dayField.getText()),
                parseIntSafe(monthField.getText()),
                parseIntSafe(yearField.getText())
        );
        if (!result.isValid()) {
            errorMessage = result.getErrorMessage();
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(firstNameField.getText().trim());
        buf.writeString(lastNameField.getText().trim());
        buf.writeEnumConstant(nationality);
        buf.writeEnumConstant(gender);
        buf.writeInt(parseIntSafe(dayField.getText()));
        buf.writeInt(parseIntSafe(monthField.getText()));
        buf.writeInt(parseIntSafe(yearField.getText()));
        buf.writeEnumConstant(background);
        ClientPlayNetworking.send(NetworkingConstants.SUBMIT_CHARACTER, buf);
    }

    public void showServerError(String message) {
        serverError = message;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, MedievalTheme.BACKGROUND_COLOR);

        int centerX = width / 2;
        int panelWidth = 340;
        int panelHeight = 280;
        int left = centerX - panelWidth / 2;
        int top = 30;

        context.fill(left, top, left + panelWidth, top + panelHeight, MedievalTheme.PANEL_COLOR);
        context.fill(left, top, left + panelWidth, top + 2, MedievalTheme.GOLD_ACCENT);
        context.fill(left, top + panelHeight - 2, left + panelWidth, top + panelHeight, MedievalTheme.GOLD_ACCENT);
        context.fill(left, top, left + 2, top + panelHeight, MedievalTheme.GOLD_ACCENT);
        context.fill(left + panelWidth - 2, top, left + panelWidth, top + panelHeight, MedievalTheme.GOLD_ACCENT);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Forge Your Legend"), centerX, top + 10, MedievalTheme.GOLD_ACCENT_BRIGHT);

        super.render(context, mouseX, mouseY, delta);

        String activeError = !serverError.isEmpty() ? serverError : errorMessage;
        if (!activeError.isEmpty()) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(activeError), centerX, top + panelHeight + 10, MedievalTheme.ERROR_COLOR);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}