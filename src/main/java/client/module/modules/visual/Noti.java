/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.visual;

import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.ui.notifi.Notifi;
import client.utils.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;

public class Noti
extends Module {
    @Settings(list={"Text", "Test1", "Test2"})
    public String mode = "Text";
    public List<Notifi> notifiList = new ArrayList<Notifi>();
    public static Noti INSTANCE = new Noti();
    @Settings(maxValue=300.0)
    public float moduleMoveX = 0.0f;

    protected Noti() {
        super("Noti", 0, true, ModuleType.VISUAL);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        int notiHight = 0;
        switch (this.mode) {
            case "Text": {
                notiHight = 20;
                break;
            }
            case "Test1": {
                notiHight = 40;
                break;
            }
            case "Test2": {
                notiHight = 30;
            }
        }
        int size = this.notifiList.size();
        for (Notifi notifi : this.notifiList) {
            for (Notifi notifi1 : this.notifiList) {
                if (!(Math.abs(notifi.targetY - notifi1.targetY) < 10.0f) || this.notifiList.indexOf(notifi) == this.notifiList.indexOf(notifi1) && this.notifiList.size() == size) continue;
                notifi.targetY += (float)notiHight;
            }
            notifi.moved = true;
            notifi.update();
            if (notifi.isText) {
                switch (this.mode) {
                    case "Text": {
                        RenderUtils.drawText(notifi.text, notifi.x, notifi.y);
                        break;
                    }
                    case "Test1": {
                        RenderUtils.drawNotifi(notifi.text, notifi.x, notifi.y, notifi);
                        break;
                    }
                    case "Test2": {
                        RenderUtils.drawNotifiB(notifi.text, notifi.x, notifi.y, notifi);
                    }
                }
            }
            if (!notifi.isImage) continue;
            RenderUtils.drawImage(notifi.image, (int)((float)sr.getScaledWidth() - notifi.x), (int)((float)sr.getScaledHeight() - notifi.y), notifi.imageWidth, notifi.imageHeight);
        }
        this.notifiList.removeIf(e -> e.state == -1);
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

