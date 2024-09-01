/*
 * Decompiled with CFR 0.151.
 */
package client.ui.notifi;

import client.ui.font.CFontRenderer;
import client.utils.MSTimer;
import client.utils.MinecraftInstance;
import net.minecraft.util.ResourceLocation;

public class Notifi
extends MinecraftInstance {
    public ResourceLocation image;
    public boolean moved;
    public int imageWidth;
    public int imageHeight;
    public String text;
    public float x;
    public float y;
    public boolean isText;
    public boolean isImage;
    public float targetX;
    public float targetY;
    public int state = 0;
    public static final int APPEAR = 0;
    public static final int STAY = 2;
    public static final int DISAPPEAR = 1;
    public float startX = 0.0f;
    private int stayTime = 1000;
    private CFontRenderer fontRenderer;
    private MSTimer msTimer = new MSTimer();
    public MSTimer timer = new MSTimer();

    public Notifi(String text) {
        this.text = text;
        this.startX = this.x = (float)(-Notifi.mc.fontRendererObj.getStringWidth(text));
        this.y = 0.0f;
        this.targetX = 70.0f;
        this.targetY = 0.0f;
        this.isText = true;
    }

    public Notifi(String text, float moveX) {
        this.text = text;
        this.startX = this.x = (float)(-Notifi.mc.fontRendererObj.getStringWidth(text));
        this.y = 0.0f;
        this.targetY = 0.0f;
        this.targetX = moveX;
        this.isText = true;
    }

    public Notifi(ResourceLocation image, int imageWidth, int imageHeight) {
        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.startX = this.x = (float)(-imageWidth);
        this.targetY = 0.0f;
        this.y = 0.0f;
        this.targetX = 70.0f;
        this.isImage = true;
    }

    public Notifi(ResourceLocation image, int imageWidth, int imageHeight, float moveX) {
        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.startX = this.x = (float)(-imageWidth);
        this.targetY = 0.0f;
        this.y = imageHeight;
        this.targetX = moveX;
        this.isImage = true;
    }

    public void setFontRenderer(CFontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.startX = this.x = (float)(-fontRenderer.getStringWidth(this.text));
    }

    public void setCustomAppearXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void addY(float addY) {
        this.targetY = this.y + addY;
    }

    public void update() {
        switch (this.state) {
            case -1: {
                break;
            }
            case 0: {
                if (Math.abs(this.x - this.targetX) > 1.0f && this.targetX > this.x) {
                    this.x = (float)((double)this.x + 5.24616746);
                    break;
                }
                this.state = 2;
                this.msTimer.reset();
                break;
            }
            case 2: {
                if (!this.msTimer.hasPassed(this.stayTime)) break;
                this.state = 1;
                break;
            }
            case 1: {
                if (this.x >= this.startX) {
                    this.x = (float)((double)this.x - 5.24616746);
                    break;
                }
                this.state = -1;
            }
        }
        if (this.targetY > this.y) {
            this.y += this.targetY;
            if (this.targetY - this.y < 1.0f) {
                this.y = this.targetY;
            }
        }
    }
}

