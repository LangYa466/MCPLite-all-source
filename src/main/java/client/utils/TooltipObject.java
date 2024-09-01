/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.ui.font.FontLoaders;
import client.utils.ColorUtil;
import client.utils.MutablePair;
import client.utils.RenderUtils;
import client.utils.Screen;
import client.utils.anim.Animation;
import client.utils.anim.Direction;
import client.utils.anim.impl.DecelerateAnimation;

public class TooltipObject
implements Screen {
    private boolean hovering = false;
    private boolean round = true;
    private final Animation fadeInAnimation = new DecelerateAnimation(250, 1.0).setDirection(Direction.BACKWARDS);
    private String tooltip;
    private String additionalInformation;
    private float width = 150.0f;
    private float height = 40.0f;

    public TooltipObject(String tooltip) {
        this.tooltip = tooltip;
    }

    public TooltipObject() {
    }

    @Override
    public void initGui() {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        this.fadeInAnimation.setDirection(this.hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        float x = mouseX - 2;
        float y = mouseY + 13;
        float fadeAnim = this.fadeInAnimation.getOutput().floatValue();
        if (this.tooltip == null || this.fadeInAnimation.finished(Direction.BACKWARDS)) {
            return;
        }
        if (this.tooltip.contains("\n")) {
            RenderUtils.scissorStart(x - 1.5f, y - 1.5f, (this.width + 4.0f) * fadeAnim, this.height + 4.0f);
            MutablePair<Float, Float> whPair = FontLoaders.GoogleSans14.drawNewLineText(this.tooltip, x + 2.0f, y + 2.0f, ColorUtil.applyOpacity(-1, fadeAnim), 3.0f);
            float additionalHeight = 0.0f;
            if (this.additionalInformation != null) {
                additionalHeight = FontLoaders.GoogleSans14.drawWrappedText(this.additionalInformation, x + 2.0f, y + 1.5f + whPair.getSecond().floatValue(), ColorUtil.applyOpacity(-1, fadeAnim), this.width, 3.0f);
            }
            RenderUtils.scissorEnd();
            this.width = this.additionalInformation != null ? Math.max(150.0f, whPair.getFirst().floatValue() + 4.0f) : whPair.getFirst().floatValue() + 4.0f;
            this.height = whPair.getSecond().floatValue() + additionalHeight;
        } else {
            this.width = FontLoaders.GoogleSans14.getStringWidth(this.tooltip) + 4;
            this.height = FontLoaders.GoogleSans14.getHeight() + 2;
            RenderUtils.scissorStart(x - 1.5f, y - 1.5f, (this.width + 4.0f) * fadeAnim, this.height + 4.0f);
            if (!this.round) {
                RenderUtils.drawBorderedRect(x, y, this.width, this.height, 1.0f, ColorUtil.tripleColor(15, fadeAnim).getRGB(), ColorUtil.tripleColor(45, fadeAnim).getRGB());
            }
            FontLoaders.GoogleSans14.drawCenteredString(this.tooltip, x + this.width / 2.0f, y + FontLoaders.GoogleSans14.getMiddleOfBox(this.height), ColorUtil.applyOpacity(-1, fadeAnim));
            RenderUtils.scissorEnd();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void setTip(String tooltip) {
        this.tooltip = tooltip;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }

    public boolean isHovering() {
        return this.hovering;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    public Animation getFadeInAnimation() {
        return this.fadeInAnimation;
    }
}

