package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumHandSide
{
    LEFT(new TextComponentTranslation("options.mainHand.left")),
    RIGHT(new TextComponentTranslation("options.mainHand.right"));

    private final ITextComponent handName;

    private EnumHandSide(ITextComponent nameIn)
    {
        this.handName = nameIn;
    }

    @SideOnly(Side.CLIENT)
    public EnumHandSide opposite()
    {
        return this == LEFT ? RIGHT : LEFT;
    }

    public String toString()
    {
        return this.handName.getUnformattedText();
    }
}