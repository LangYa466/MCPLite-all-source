/*
 * Decompiled with CFR 0.151.
 */
package client.ui.element;

import client.Client;
import client.ui.element.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElementManager {
    public List<Element> elements = new ArrayList<Element>();
    public static Element potionEffect = new Element("PotionEffect");
    public static Element targetHUD = new Element("TargetHUD");
    public static Element SessionHUD = new Element("SessionHUD");
    public static Element arrayList = new Element("ArrayList");
    public static Element logo = new Element("LOGO");
    public static ElementManager INSTANCE = new ElementManager();
    private Element currentElement;
    private int x;
    private int y;

    private ElementManager() {
        this.elements.add(potionEffect);
        this.elements.add(targetHUD);
        this.elements.add(SessionHUD);
        this.elements.add(arrayList);
        this.elements.add(logo);
    }

    public static Element get(String string) {
        return ElementManager.INSTANCE.elements.stream().filter(element -> element.name.equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (Element element : this.elements) {
                if (!this.dragged(mouseX, mouseY, element)) continue;
                this.currentElement = element;
                this.x = (int)((float)mouseX - this.currentElement.moveX);
                this.y = (int)((float)mouseY - this.currentElement.moveY);
            }
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.currentElement != null) {
            this.currentElement.moveX = mouseX - this.x;
            this.currentElement.moveY = mouseY - this.y;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.x = mouseX;
        this.y = mouseY;
        this.currentElement = null;
        Client.configManager.saveConfig();
    }

    public boolean dragged(int mouseX, int mouseY, Element element) {
        return (float)mouseX >= element.posX && (float)mouseX <= element.posX + element.width && (float)mouseY >= element.posY && (float)mouseY <= element.posY + element.height;
    }
}

