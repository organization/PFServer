package net.minecraft.client.gui.spectator;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface ISpectatorMenuView
{
    List<ISpectatorMenuObject> getItems();

    ITextComponent getPrompt();
}