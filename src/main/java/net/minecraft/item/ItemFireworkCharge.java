package net.minecraft.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFireworkCharge extends Item
{
    @SideOnly(Side.CLIENT)
    public static NBTBase getExplosionTag(ItemStack stack, String key)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");

            if (nbttagcompound != null)
            {
                return nbttagcompound.getTag(key);
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");

            if (nbttagcompound != null)
            {
                addExplosionInfo(nbttagcompound, tooltip);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip)
    {
        byte b0 = nbt.getByte("Type");

        if (b0 >= 0 && b0 <= 4)
        {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.type." + b0).trim());
        }
        else
        {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.type").trim());
        }

        int[] aint = nbt.getIntArray("Colors");

        if (aint.length > 0)
        {
            boolean flag = true;
            StringBuilder s = new StringBuilder();

            for (int i : aint)
            {
                if (!flag)
                {
                    s.append(", ");
                }

                flag = false;
                boolean flag1 = false;

                for (int j = 0; j < ItemDye.DYE_COLORS.length; ++j)
                {
                    if (i == ItemDye.DYE_COLORS[j])
                    {
                        flag1 = true;
                        s.append(I18n.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName()));
                        break;
                    }
                }

                if (!flag1)
                {
                    s.append(I18n.translateToLocal("item.fireworksCharge.customColor"));
                }
            }

            tooltip.add(s.toString());
        }

        int[] aint1 = nbt.getIntArray("FadeColors");

        if (aint1.length > 0)
        {
            boolean flag2 = true;
            StringBuilder s1 = new StringBuilder(I18n.translateToLocal("item.fireworksCharge.fadeTo") + " ");

            for (int l : aint1)
            {
                if (!flag2)
                {
                    s1.append(", ");
                }

                flag2 = false;
                boolean flag5 = false;

                for (int k = 0; k < 16; ++k)
                {
                    if (l == ItemDye.DYE_COLORS[k])
                    {
                        flag5 = true;
                        s1.append(I18n.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(k).getUnlocalizedName()));
                        break;
                    }
                }

                if (!flag5)
                {
                    s1.append(I18n.translateToLocal("item.fireworksCharge.customColor"));
                }
            }

            tooltip.add(s1.toString());
        }

        boolean flag3 = nbt.getBoolean("Trail");

        if (flag3)
        {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.trail"));
        }

        boolean flag4 = nbt.getBoolean("Flicker");

        if (flag4)
        {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.flicker"));
        }
    }
}