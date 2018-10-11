package net.minecraft.client.renderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.image.BufferedImage;

@SideOnly(Side.CLIENT)
public interface IImageBuffer
{
    BufferedImage parseUserSkin(BufferedImage image);

    void skinAvailable();
}