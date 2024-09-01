/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.Client;
import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.misc.ClientSettings;
import client.module.modules.misc.Disabler;
import client.module.modules.misc.Gapple;
import client.module.modules.visual.ClientColor;
import client.module.modules.world.Scaffold;
import client.ui.font.FontLoaders;
import client.utils.BlockUtils;
import client.utils.ClientUtils;
import client.utils.InventoryUtils;
import client.utils.ItemUtils;
import client.utils.MSTimer;
import client.utils.RenderUtils;
import client.utils.StencilUtil;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.opengl.GL11;

public class ItemManager
extends Module {
    @Settings
    private boolean inventoryCleaner = false;
    @Settings
    private boolean chestStealer = false;
    @Settings(maxValue=10000.0, name="ChestCloseDelay")
    private int chestCloseDelay = 200;
    @Settings(maxValue=10000.0, name="ChestCloseDelay")
    public int chestCoolDownDelay = 50;
    @Settings(maxValue=10000.0, name="ChestItemDelay")
    private int chestItemDelay = 0;
    @Settings(maxValue=10000.0, name="InventoryDelay")
    private int trashDelay = 1;
    @Settings(maxValue=10000.0, name="SortDelay")
    private int sortDelay = 1;
    @Settings(maxValue=10000.0, name="AutoArmorDelay")
    private int autoArmorDelay = 1;
    @Settings(maxValue=10000.0, name="Delay")
    public int lagDelay = 200;
    @Settings(maxValue=10000.0, name="TotalDelay")
    public int totalDelay = 200;
    @Settings
    private boolean onlyInvOpen = false;
    @Settings
    private boolean autoArmor = false;
    @Settings
    private boolean sortHotBar = false;
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar1 = "Sword";
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar2 = "Bow";
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar3 = "AppleGold";
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar4 = "Block";
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar5 = "Block";
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar6 = "Peal";
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar7 = "Disable";
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar8 = "Disable";
    @Settings(list={"Disable", "Sword", "Bow", "Block", "AppleGold", "Peal", "Projectiles", "Potion", "Axe", "Pickaxe"})
    private String hotbar9 = "Disable";
    @Settings
    public boolean silent = false;
    @Settings
    private boolean keepFood = false;
    @Settings
    private boolean windowClick = false;
    @Settings
    private boolean silentCircle = false;
    @Settings
    private boolean silentShow = false;
    @Settings(minValue=-200.0, maxValue=200.0)
    private int boxY = 0;
    private final List<String> specialItem = Arrays.asList("\u5f00\u59cb\u6e38\u620f", "\u6e38\u620f", "\u7ba1\u7406", "\u7a7a\u95f4", "\u7279\u6548", "\u70b9\u51fb", "\u9053\u5177", "\u804c\u4e1a");
    private final MSTimer openTimer = new MSTimer();
    private final MSTimer stealerTimer = new MSTimer();
    private final MSTimer trashTimer = new MSTimer();
    private final MSTimer midWayTimer = new MSTimer();
    private final MSTimer sortTimer = new MSTimer();
    public final MSTimer lagTimer = new MSTimer();
    private final MSTimer totalTimer = new MSTimer();
    public final MSTimer chestCoolDownTimer = new MSTimer();
    private final List<Integer> lagStack = new ArrayList<Integer>();
    public GuiScreen openScreen;
    public Container screenContainer;
    private boolean aShitForSort = false;

    public ItemManager() {
        super("ItemManager", 23, ModuleType.MISC);
    }

    private String types(int index) {
        String[] list = new String[]{this.hotbar1, this.hotbar2, this.hotbar3, this.hotbar4, this.hotbar5, this.hotbar6, this.hotbar7, this.hotbar8, this.hotbar9};
        return list[index];
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (ClientUtils.nullCheck() || Gapple.pulsing || Gapple.eating) {
            return;
        }
        if (event.getPacket() instanceof C0DPacketCloseWindow) {
            this.openScreen = null;
            this.screenContainer = null;
        }
    }

    @Override
    public void onUpdate() {
        if (ClientUtils.nullCheck() || Gapple.pulsing || Gapple.eating) {
            return;
        }
    }

    @Override
    public void onEnable() {
        this.openScreen = null;
        this.screenContainer = null;
    }

    @Override
    public void onDisable() {
        this.openScreen = null;
        this.screenContainer = null;
    }

    @Override
    public void onTick() {
        AtomicBoolean atomicBoolean;
        if (ClientUtils.nullCheck()) {
            return;
        }
        ArrayList<Integer> trashes = new ArrayList<Integer>();
        HashMap<Object, Integer> swords = new HashMap<Object, Integer>();
        HashMap<ItemStack, Integer> armor = new HashMap<ItemStack, Integer>();
        HashMap<Object, Integer> bows = new HashMap<Object, Integer>();
        HashMap<ItemStack, Integer> tools = new HashMap<ItemStack, Integer>();
        Gapple gapple = (Gapple)Client.moduleManager.moduleMap.get(Gapple.class);
        AtomicBoolean invFull = new AtomicBoolean(false);
        if (!this.totalTimer.hasPassed(this.totalDelay) && this.totalDelay != 0) {
            return;
        }
        if (!this.chestCoolDownTimer.hasPassed(this.chestCoolDownDelay) && this.chestCloseDelay != 0) {
            return;
        }
        ItemManager.mc.thePlayer.inventoryContainer.inventorySlots.forEach(slot -> {
            if (!slot.getHasStack()) {
                if (slot.slotNumber < 45 && slot.slotNumber > 8) {
                    invFull.set(false);
                } else {
                    invFull.set(true);
                }
            }
        });
        if (ItemManager.mc.currentScreen instanceof GuiChest || ItemManager.mc.currentScreen instanceof GuiFurnace || ItemManager.mc.currentScreen instanceof GuiBrewingStand) {
            this.openScreen = ItemManager.mc.currentScreen;
            this.screenContainer = ItemManager.mc.thePlayer.openContainer;
            if (this.silent) {
                mc.displayGuiScreen(null);
            }
        }
        if (ClientUtils.nullCheck() || Gapple.pulsing || Gapple.eating || gapple.getState()) {
            if (this.openScreen != null || this.screenContainer != null) {
                ItemManager.mc.thePlayer.closeScreen();
            }
            return;
        }
        if (!invFull.get()) {
            // TODO 笑死我了 没给ItemManager写isNUll检测 空指针了吧
            if (mc.getCurrentServerData() == null) return;
            ArrayList<Slot> slots;
            if (!this.lagTimer.hasPassed((long)this.lagDelay + ItemManager.mc.getCurrentServerData().pingToServer)) {
                return;
            }
            if (ClientSettings.INSTANCE.onHyt) {
                atomicBoolean = new AtomicBoolean(false);
                AtomicBoolean finalAtomicBoolean = atomicBoolean;
                Arrays.stream(ItemManager.mc.thePlayer.inventory.mainInventory).forEach(e -> {
                    if (e != null) {
                        this.specialItem.forEach(s -> {
                            if (e.getDisplayName().contains((CharSequence)s)) {
                                finalAtomicBoolean.set(true);
                            }
                        });
                    }
                });
                if (atomicBoolean.get()) {
                    return;
                }
            }
            if (this.openScreen instanceof GuiChest && this.chestStealer) {
                slots = new ArrayList<Slot>();
                GuiChest guiChest = (GuiChest)this.openScreen;
                for (int i = 0; i < guiChest.inventoryRows * 9; ++i) {
                    if (guiChest.inventorySlots.inventorySlots.get(i).getStack() == null) continue;
                    slots.add(guiChest.inventorySlots.inventorySlots.get(i));
                }
                if (slots.isEmpty()) {
                    if (!this.stealerTimer.hasPassed(this.chestCloseDelay) || this.chestCloseDelay == 0) {
                        return;
                    }
                    ItemManager.mc.thePlayer.closeScreen();
                } else {
                    slots.forEach(slot -> {
                        if (this.midWayTimer.hasPassed(this.chestItemDelay) || this.chestItemDelay == 0) {
                            this.windowClick(guiChest.inventorySlots.windowId, slot.slotNumber, 0, 1, ItemManager.mc.thePlayer);
                            this.midWayTimer.reset();
                        }
                    });
                    this.stealerTimer.reset();
                }
                this.chestCoolDownTimer.reset();
                this.lagTimer.reset();
                return;
            }
            if (this.openScreen instanceof GuiFurnace && this.chestStealer) {
                slots = new ArrayList();
                ContainerFurnace guiChest = (ContainerFurnace)this.screenContainer;
                for (int i = 0; i < guiChest.tileFurnace.getSizeInventory(); ++i) {
                    if (((Slot)guiChest.inventorySlots.get(i)).getStack() == null) continue;
                    slots.add((Slot)guiChest.inventorySlots.get(i));
                }
                if (slots.isEmpty()) {
                    if (!this.stealerTimer.hasPassed(this.chestCloseDelay) || this.chestCloseDelay == 0) {
                        return;
                    }
                    ItemManager.mc.thePlayer.closeScreen();
                } else {
                    slots.forEach(slot -> {
                        if (this.midWayTimer.hasPassed(this.chestItemDelay) || this.chestItemDelay == 0) {
                            this.windowClick(((GuiFurnace)this.openScreen).inventorySlots.windowId, slot.slotNumber, 0, 1, ItemManager.mc.thePlayer);
                            this.midWayTimer.reset();
                        }
                    });
                    this.stealerTimer.reset();
                }
                this.chestCoolDownTimer.reset();
                this.lagTimer.reset();
                return;
            }
            if (this.openScreen instanceof GuiBrewingStand && this.chestStealer) {
                slots = new ArrayList();
                ContainerBrewingStand guiChest = (ContainerBrewingStand)this.screenContainer;
                for (int i = 0; i < 3; ++i) {
                    if (((Slot)guiChest.inventorySlots.get(i)).getStack() == null) continue;
                    slots.add((Slot)guiChest.inventorySlots.get(i));
                }
                if (slots.isEmpty()) {
                    if (!this.stealerTimer.hasPassed(this.chestCloseDelay) || this.chestCloseDelay == 0) {
                        return;
                    }
                    ItemManager.mc.thePlayer.closeScreen();
                } else {
                    slots.forEach(slot -> {
                        if (this.midWayTimer.hasPassed(this.chestItemDelay) || this.chestItemDelay == 0) {
                            this.windowClick(((GuiBrewingStand)this.openScreen).inventorySlots.windowId, slot.slotNumber, 0, 1, ItemManager.mc.thePlayer);
                            this.midWayTimer.reset();
                        }
                    });
                    this.stealerTimer.reset();
                }
                this.chestCoolDownTimer.reset();
                this.lagTimer.reset();
                return;
            }
        } else if (this.openScreen != null || this.screenContainer != null) {
            ItemManager.mc.thePlayer.closeScreen();
        }
        if (!this.lagTimer.hasPassed((long)this.lagDelay + ItemManager.mc.getCurrentServerData().pingToServer) && this.lagDelay != 0) {
            return;
        }
        if (this.sortHotBar && (!this.onlyInvOpen || ItemManager.mc.currentScreen instanceof GuiInventory) && this.openScreen == null && this.screenContainer == null) {
            atomicBoolean = new AtomicBoolean(false);
            ArrayList<Integer> hasSorted = new ArrayList<Integer>();
            for (int i = 44; i >= 36; --i) {
                Slot slot2 = ItemManager.mc.thePlayer.inventoryContainer.getSlot(i);
                if (!this.sortTimer.hasPassed(this.sortDelay)) continue;
                if (!(!this.types(i - 36).equals("Block") || slot2.getHasStack() && slot2.getStack().getItem() instanceof ItemBlock && slot2.getStack().stackSize != 0)) {
                    Scaffold scaffold = (Scaffold)Client.moduleManager.moduleMap.get(Scaffold.class);
                    int found = InventoryUtils.findItem(0, i, hasSorted, ItemBlock.class);
                    while (found >= 36 && this.types(found - 36).equals("Block")) {
                        hasSorted.add(found);
                        found = InventoryUtils.findItem(0, i, hasSorted, ItemBlock.class);
                    }
                    if (this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, found, i - 36, 2, ItemManager.mc.thePlayer)) {
                        atomicBoolean.set(true);
                    }
                }
                hasSorted.add(i);
            }
            if (atomicBoolean.get()) {
                return;
            }
        }
        Optional<Slot> bestSword = ItemManager.mc.thePlayer.inventoryContainer.inventorySlots.stream().filter(slot1 -> slot1.getStack() != null && slot1.getStack().getItem() instanceof ItemSword).max(Comparator.comparingDouble(sword -> (double)((ItemSword)sword.getStack().getItem()).attackDamage + 1.25 * (double)ItemUtils.getEnchantment(sword.getStack(), Enchantment.sharpness)));
        if (this.inventoryCleaner && (!this.onlyInvOpen || ItemManager.mc.currentScreen instanceof GuiInventory) && this.openScreen == null && this.screenContainer == null) {
            boolean startInt = false;
            int endInt = 45;
            for (int i = 0; i < endInt; ++i) {
                ItemStack itemStack2 = ItemManager.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (itemStack2 == null) continue;
                Item item = itemStack2.getItem();
                if (item instanceof ItemSword) {
                    swords.put(itemStack2, i);
                    continue;
                }
                if (item instanceof ItemArmor) {
                    armor.put(itemStack2, i);
                    continue;
                }
                if (item instanceof ItemTool) {
                    tools.put(itemStack2, i);
                    continue;
                }
                if (item instanceof ItemBlock) {
                    Block block = ((ItemBlock)item).getBlock();
                    if (block instanceof BlockTNT || !BlockUtils.isUnwantedBlock(block)) continue;
                    trashes.add(i);
                    continue;
                }
                if (item instanceof ItemAppleGold || item instanceof ItemPotion || item instanceof ItemFood && this.keepFood) continue;
                if (item instanceof ItemBow) {
                    bows.put(itemStack2, i);
                    continue;
                }
                if (item instanceof ItemSnowball || item instanceof ItemEgg || item instanceof ItemEnderPearl || item == Items.arrow) continue;
                trashes.add(i);
            }
            List<Map.Entry> sorted = swords.entrySet().stream().sorted(Comparator.comparingDouble(value -> InventoryUtils.getSwordDamage((ItemStack)value.getKey()))).collect(Collectors.toList());
            for (Map.Entry stackIntegerEntry : sorted) {
                if (InventoryUtils.getSwordDamage((ItemStack)stackIntegerEntry.getKey()) != InventoryUtils.getSwordDamage(bestSword.get().getStack()) || (Integer)stackIntegerEntry.getValue() <= 35) continue;
                bestSword = Optional.of(ItemManager.mc.thePlayer.inventoryContainer.inventorySlots.get((Integer)stackIntegerEntry.getValue()));
            }
            bestSword.ifPresent(slot -> swords.remove(slot.getStack(), slot.slotNumber));
            armor.remove(armor.keySet().stream().filter(itemStack -> ((ItemArmor)itemStack.getItem()).armorType == 0).max(Comparator.comparingDouble(itemStack -> (double)((ItemArmor)itemStack.getItem()).damageReduceAmount + 0.01 * (double)ItemUtils.getEnchantment(itemStack, Enchantment.protection))).orElse(null));
            armor.remove(armor.keySet().stream().filter(itemStack -> ((ItemArmor)itemStack.getItem()).armorType == 1).max(Comparator.comparingDouble(itemStack -> (double)((ItemArmor)itemStack.getItem()).damageReduceAmount + 0.01 * (double)ItemUtils.getEnchantment(itemStack, Enchantment.protection))).orElse(null));
            armor.remove(armor.keySet().stream().filter(itemStack -> ((ItemArmor)itemStack.getItem()).armorType == 2).max(Comparator.comparingDouble(itemStack -> (double)((ItemArmor)itemStack.getItem()).damageReduceAmount + 0.01 * (double)ItemUtils.getEnchantment(itemStack, Enchantment.protection))).orElse(null));
            armor.remove(armor.keySet().stream().filter(itemStack -> ((ItemArmor)itemStack.getItem()).armorType == 3).max(Comparator.comparingDouble(itemStack -> (double)((ItemArmor)itemStack.getItem()).damageReduceAmount + 0.01 * (double)ItemUtils.getEnchantment(itemStack, Enchantment.protection))).orElse(null));
            bows.remove(bows.keySet().stream().max(Comparator.comparingDouble(bow -> (double)ItemUtils.getEnchantment((ItemStack) bow, Enchantment.power) + (double)ItemUtils.getEnchantment((ItemStack) bow, Enchantment.punch) * 0.1)).orElse(null));
            tools.remove(tools.keySet().stream().filter(itemStack -> itemStack.getItem() instanceof ItemPickaxe).max(Comparator.comparingDouble(itemStack -> ((ItemPickaxe)itemStack.getItem()).getToolMaterial().getHarvestLevel())).orElse(null));
            tools.remove(tools.keySet().stream().filter(itemStack -> itemStack.getItem() instanceof ItemAxe).max(Comparator.comparingDouble(itemStack -> (double)((ItemAxe)itemStack.getItem()).getToolMaterial().getHarvestLevel() + 0.1 * (double)ItemUtils.getEnchantment(itemStack, Enchantment.sharpness))).orElse(null));
            tools.remove(tools.keySet().stream().filter(itemStack -> itemStack.getItem() instanceof ItemSpade).max(Comparator.comparingDouble(itemStack -> ((ItemSpade)itemStack.getItem()).getToolMaterial().getHarvestLevel())).orElse(null));
            trashes.addAll(tools.values());
            trashes.addAll(bows.values());
            trashes.addAll(swords.values());
            trashes.addAll(armor.values());
        }
        boolean hasDrop = false;
        trashes.removeIf(integer -> integer > 35);
        if (!(trashes.isEmpty() || this.onlyInvOpen && !(ItemManager.mc.currentScreen instanceof GuiInventory) || this.openScreen != null || this.screenContainer != null)) {
            if (this.trashTimer.hasPassed(this.trashDelay) || this.trashDelay == 0) {
                this.trashTimer.reset();
                trashes.forEach(integer -> {
                    if (this.midWayTimer.hasPassed(this.trashDelay) || this.trashDelay == 0) {
                        this.windowClick(ItemManager.mc.thePlayer.openContainer.windowId, (int)integer, 1, 4, ItemManager.mc.thePlayer);
                        this.midWayTimer.reset();
                    }
                });
            }
            if (trashes.isEmpty()) {
                hasDrop = true;
            } else {
                return;
            }
        }
        if (hasDrop) {
            this.lagTimer.reset();
        }
        if (!this.lagTimer.hasPassed((long)this.lagDelay + ItemManager.mc.getCurrentServerData().pingToServer) && this.lagDelay != 0) {
            return;
        }
        if (this.sortHotBar && (!this.onlyInvOpen || ItemManager.mc.currentScreen instanceof GuiInventory) && this.openScreen == null && this.screenContainer == null) {
            AtomicBoolean atomicBoolean2 = new AtomicBoolean(false);
            ArrayList<Integer> hasSorted = new ArrayList<Integer>();
            for (int i = 44; i >= 36; --i) {
                Slot slot3 = ItemManager.mc.thePlayer.inventoryContainer.getSlot(i);
                if (this.sortTimer.hasPassed(this.sortDelay) || this.sortDelay == 0) {
                    switch (this.types(i - 36)) {
                        case "Sword": {
                            if (!bestSword.isPresent() || slot3.getHasStack() && slot3.getStack().getItem() instanceof ItemSword && slot3.slotNumber == bestSword.get().slotNumber || !this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, bestSword.get().slotNumber, i - 36, 2, ItemManager.mc.thePlayer)) break;
                            atomicBoolean2.set(true);
                            break;
                        }
                        case "AppleGold": {
                            if (slot3.getHasStack() && slot3.getStack().getItem() instanceof ItemAppleGold || !this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, InventoryUtils.findItem(0, i, Items.golden_apple, hasSorted), i - 36, 2, ItemManager.mc.thePlayer)) break;
                            atomicBoolean2.set(true);
                            break;
                        }
                        case "Bow": {
                            if (slot3.getHasStack() && slot3.getStack().getItem() instanceof ItemBow || !this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, InventoryUtils.findItem(0, i, Items.bow, hasSorted), i - 36, 2, ItemManager.mc.thePlayer)) break;
                            atomicBoolean2.set(true);
                            break;
                        }
                        case "Peal": {
                            if (slot3.getHasStack() && slot3.getStack().getItem() instanceof ItemEnderPearl || !this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, InventoryUtils.findItem(0, i, Items.ender_pearl, hasSorted), i - 36, 2, ItemManager.mc.thePlayer)) break;
                            atomicBoolean2.set(true);
                            break;
                        }
                        case "Projectiles": {
                            if (slot3.getHasStack() && (slot3.getStack().getItem() instanceof ItemEgg || slot3.getStack().getItem() instanceof ItemSnowball)) break;
                            if (this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, InventoryUtils.findItem(0, i, Items.egg, hasSorted), i - 36, 2, ItemManager.mc.thePlayer)) {
                                atomicBoolean2.set(true);
                            }
                            if (!this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, InventoryUtils.findItem(0, i, Items.snowball, hasSorted), i - 36, 2, ItemManager.mc.thePlayer) || atomicBoolean2.get()) break;
                            atomicBoolean2.set(true);
                            break;
                        }
                        case "Potion": {
                            if (slot3.getHasStack() && slot3.getStack().getItem() instanceof ItemPotion || !this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, InventoryUtils.findItem(0, i, hasSorted, ItemPotion.class), i - 36, 2, ItemManager.mc.thePlayer)) break;
                            atomicBoolean2.set(true);
                            break;
                        }
                        case "Axe": {
                            if (slot3.getHasStack() && slot3.getStack().getItem() instanceof ItemAxe || !this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, InventoryUtils.findItem(0, i, hasSorted, ItemAxe.class), i - 36, 2, ItemManager.mc.thePlayer)) break;
                            atomicBoolean2.set(true);
                            break;
                        }
                        case "Pickaxe": {
                            if (slot3.getHasStack() && slot3.getStack().getItem() instanceof ItemPickaxe || !this.checkClick(ItemManager.mc.thePlayer.openContainer.windowId, InventoryUtils.findItem(0, i, hasSorted, ItemPickaxe.class), i - 36, 2, ItemManager.mc.thePlayer)) break;
                            atomicBoolean2.set(true);
                        }
                    }
                } else {
                    return;
                }
                hasSorted.add(i);
            }
            if (atomicBoolean2.get() && this.lagDelay != 0) {
                this.aShitForSort = true;
                return;
            }
            if (this.aShitForSort) {
                this.lagTimer.reset();
                this.aShitForSort = false;
            }
        }
        if (!this.lagTimer.hasPassed((long)this.lagDelay + ItemManager.mc.getCurrentServerData().pingToServer) && this.lagDelay != 0) {
            return;
        }
        if (this.autoArmor && !(ItemManager.mc.currentScreen instanceof GuiChest) && (!this.onlyInvOpen || ItemManager.mc.currentScreen instanceof GuiInventory) && this.openScreen == null && this.screenContainer == null) {
            AtomicBoolean notDone = new AtomicBoolean(false);
            AtomicBoolean hasDone = new AtomicBoolean(false);
            for (int i = 5; i < 9; ++i) {
                if (ItemManager.mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null) continue;
                int finalI = i;
                ItemManager.mc.thePlayer.inventoryContainer.inventorySlots.stream().filter(slot -> slot.getStack() != null && slot.getStack().getItem() instanceof ItemArmor && ((ItemArmor)slot.getStack().getItem()).armorType == finalI - 5).max(Comparator.comparingDouble(itemStack -> (double)((ItemArmor)itemStack.getStack().getItem()).damageReduceAmount + 0.01 * (double)ItemUtils.getEnchantment(itemStack.getStack(), Enchantment.protection))).ifPresent(s -> {
                    if (this.midWayTimer.hasPassed(this.autoArmorDelay) || this.autoArmorDelay == 0) {
                        this.windowClick(ItemManager.mc.thePlayer.openContainer.windowId, s.slotNumber, 0, 1, ItemManager.mc.thePlayer);
                        this.midWayTimer.reset();
                        hasDone.set(true);
                    } else {
                        notDone.set(true);
                    }
                });
            }
            if (notDone.get()) {
                return;
            }
            if (hasDone.get()) {
                this.lagTimer.reset();
            }
        }
        this.lagStack.clear();
        this.totalTimer.reset();
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (event.getPacket() instanceof S2EPacketCloseWindow) {
            this.openScreen = null;
            this.screenContainer = null;
            this.lagTimer.reset();
            this.lagStack.clear();
        }
    }

    @Override
    public void onPostUpdate() {
    }

    private boolean checkClick(int windowId, int slot, int clicked, int mode, EntityPlayer entityPlayer) {
        if (slot != -1) {
            if (this.sortDelay != 0) {
                this.midWayTimer.reset();
                this.sortTimer.reset();
            } else {
                this.lagTimer.reset();
            }
            this.windowClick(windowId, slot, clicked, mode, entityPlayer);
        }
        return slot != -1;
    }

    @Override
    public void onWorldLoad() {
        this.openScreen = null;
        this.screenContainer = null;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (this.silent) {
            if (this.screenContainer != null || this.openScreen != null) {
                if (this.silentCircle) {
                    DecimalFormat decimalFormat = new DecimalFormat("###.0%");
                    ScaledResolution scaledResolution = new ScaledResolution(mc);
                    RenderUtils.drawLoadingCircle((float)scaledResolution.getScaledWidth() / 2.0f, (float)scaledResolution.getScaledHeight() / 2.0f + 30.0f);
                    FontLoaders.Bold18.drawCenteredString(this.openScreen.getClass().getSimpleName(), (float)scaledResolution.getScaledWidth() / 2.0f, (float)scaledResolution.getScaledHeight() / 2.0f + 6.0f, new Color(255, 255, 255).getRGB());
                    FontLoaders.Bold18.drawCenteredString(decimalFormat.format(this.openTimer.getPassed() < 100L ? (double)((float)this.openTimer.getPassed() / ((float)this.chestCloseDelay - 50.0f)) : 1.0), (float)scaledResolution.getScaledWidth() / 2.0f, (float)scaledResolution.getScaledHeight() / 2.0f + 45.0f, new Color(255, 255, 255).getRGB());
                }
                if (this.silentShow) {
                    ScaledResolution sr = new ScaledResolution(mc);
                    int x = sr.getScaledWidth() / 2 - 87;
                    int y = sr.getScaledHeight() / 2 + this.boxY;
                    FontRenderer font = ItemManager.mc.fontRendererObj;
                    int guiMod = 0;
                    if (this.openScreen instanceof GuiFurnace || this.openScreen instanceof GuiBrewingStand) {
                        guiMod = 2;
                    } else if (this.openScreen instanceof GuiChest) {
                        guiMod = 1;
                    }
                    Color color2 = RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, 255), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, 255), ((double)(ItemManager.mc.thePlayer.ticksExisted * 4) + 2.0) % 400.0 / 100.0, 255);
                    Color color3 = RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, 255), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, 255), ((double)(ItemManager.mc.thePlayer.ticksExisted * 4) + 2.0 + 65.0) % 400.0 / 100.0, 255);
                    switch (guiMod) {
                        case 1: {
                            StencilUtil.initStencilToWrite();
                            RenderUtils.drawRoundedRect(x, y, 174.0f, 66.0f, 4.0f, new Color(0, 0, 0, 100).getRGB(), 1.0f, new Color(0, 0, 0, 100).getRGB());
                            StencilUtil.readStencilBuffer(1);
                            RenderUtils.drawRoundedRect(x, y, 174.0f, 66.0f, 4.0f, new Color(0, 0, 0, 200).getRGB(), 1.0f, new Color(0, 0, 0, 200).getRGB());
                            RenderUtils.drawHGradientRect(x, (float)y - 1.0f, 174.0, 4.0, color2.getRGB(), color3.getRGB());
                            StencilUtil.uninitStencilBuffer();
                            GL11.glPushMatrix();
                            RenderHelper.enableGUIStandardItemLighting();
                            this.renderInv(0, 8, x + 6, y + 6, font);
                            this.renderInv(9, 17, x + 6, y + 24, font);
                            this.renderInv(18, 26, x + 6, y + 42, font);
                            RenderHelper.disableStandardItemLighting();
                            GlStateManager.enableAlpha();
                            GlStateManager.disableBlend();
                            GlStateManager.disableLighting();
                            GlStateManager.disableCull();
                            GL11.glPopMatrix();
                            break;
                        }
                        case 2: 
                        case 3: {
                            StencilUtil.initStencilToWrite();
                            RenderUtils.drawRoundedRect(x, y, 66.0f, 26.0f, 4.0f, new Color(0, 0, 0, 100).getRGB(), 1.0f, new Color(0, 0, 0, 100).getRGB());
                            StencilUtil.readStencilBuffer(1);
                            RenderUtils.drawShadow(x, y, 66.0f, 26.0f);
                            RenderUtils.drawRoundedRect(x, y, 66.0f, 26.0f, 4.0f, new Color(0, 0, 0, 100).getRGB(), 1.0f, new Color(0, 0, 0, 100).getRGB());
                            RenderUtils.drawHGradientRect(x, (float)y - 1.0f, 66.0, 4.0, color2.getRGB(), color3.getRGB());
                            StencilUtil.uninitStencilBuffer();
                            GL11.glPushMatrix();
                            RenderHelper.enableGUIStandardItemLighting();
                            this.renderInv(0, 2, x + 6, y + 6, font);
                            RenderHelper.disableStandardItemLighting();
                            GlStateManager.enableAlpha();
                            GlStateManager.disableBlend();
                            GlStateManager.disableLighting();
                            GlStateManager.disableCull();
                            GL11.glPopMatrix();
                        }
                    }
                }
            } else {
                this.openTimer.reset();
            }
        }
    }

    public void windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn) {
        ItemStack stack;
        short short1 = 0;
        if (this.screenContainer != null && this.screenContainer.windowId == windowId && this.screenContainer.getSlot(slotId).getHasStack()) {
            stack = this.screenContainer.getSlot(slotId).getStack();
            if (this.lagStack.remove((Object)slotId)) {
                return;
            }
            this.lagStack.add(slotId);
        } else if (windowId == 0 && ItemManager.mc.thePlayer.inventoryContainer.getSlot(slotId).getHasStack()) {
            stack = ItemManager.mc.thePlayer.inventoryContainer.getSlot(slotId).getStack();
            if (this.lagStack.remove((Object)slotId)) {
                return;
            }
            this.lagStack.add(slotId);
        }
        if (this.windowClick) {
            ItemManager.mc.playerController.windowClick(windowId, slotId, mouseButtonClicked, mode, playerIn);
        } else {
            ItemStack itemstack = null;
            if (mode == 1) {
                if (slotId < 0) {
                    itemstack = null;
                } else {
                    ItemStack itemstack8;
                    Slot slot6 = ItemManager.mc.thePlayer.inventoryContainer.getSlot(slotId);
                    if (slot6 != null && slot6.canTakeStack(playerIn) && (itemstack8 = ItemManager.mc.thePlayer.inventoryContainer.getSlot(slotId).getStack()) != null) {
                        Item item = itemstack8.getItem();
                        itemstack = itemstack8.copy();
                    }
                }
            }
            if (mode == 0) {
                if (slotId < 0) {
                    itemstack = null;
                } else {
                    ItemStack itemstack9;
                    Slot slot7 = ItemManager.mc.thePlayer.inventoryContainer.getSlot(slotId);
                    if (slot7 != null && (itemstack9 = slot7.getStack()) != null) {
                        itemstack = itemstack9.copy();
                    }
                }
            }
            if (itemstack == null) {
                itemstack = new ItemStack(Block.getBlockById(166));
            }
            if (Disabler.INSTANCE.grimPost) {
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(windowId, 0, true));
            }
            if (Disabler.INSTANCE.hytFastClick) {
                mc.getNetHandler().addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, mode, itemstack, (short) 1));
            } else {
                mc.getNetHandler().addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, mode, itemstack, short1));
            }
        }
    }

    private void renderInv(int slot, int endSlot, int x, int y, FontRenderer font) {
        int xOffset = x;
        for (int i = slot; i <= endSlot; ++i) {
            ItemStack stack = this.screenContainer.getSlot(i).getStack();
            RenderItem renderItem = mc.getRenderItem();
            renderItem.renderItemAndEffectIntoGUI(stack, (xOffset += 18) - 18, y);
            renderItem.renderItemOverlays(font, stack, xOffset - 18, y);
        }
    }

    private int getWindowId() {
        int id = 0;
        if (ItemManager.mc.thePlayer != null && ItemManager.mc.thePlayer.openContainer != null) {
            id = ItemManager.mc.thePlayer.openContainer.windowId;
        }
        return id;
    }
}

