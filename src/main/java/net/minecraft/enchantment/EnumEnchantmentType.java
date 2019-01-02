package net.minecraft.enchantment;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;

public enum EnumEnchantmentType
{
    ALL {
        public boolean canEnchantItem(Item itemIn)
        {
            for (EnumEnchantmentType enumenchantmenttype : EnumEnchantmentType.values())
            {
                if (enumenchantmenttype != EnumEnchantmentType.ALL && enumenchantmenttype.canEnchantItem(itemIn))
                {
                    return true;
                }
            }

            return false;
        }
    },
    ARMOR {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor;
        }
    },
    ARMOR_FEET {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).armorType == EntityEquipmentSlot.FEET;
        }
    },
    ARMOR_LEGS {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).armorType == EntityEquipmentSlot.LEGS;
        }
    },
    ARMOR_CHEST {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).armorType == EntityEquipmentSlot.CHEST;
        }
    },
    ARMOR_HEAD {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).armorType == EntityEquipmentSlot.HEAD;
        }
    },
    WEAPON {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemSword;
        }
    },
    DIGGER {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemTool;
        }
    },
    FISHING_ROD {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemFishingRod;
        }
    },
    BREAKABLE {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn.isDamageable();
        }
    },
    BOW {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemBow;
        }
    },
    WEARABLE {
        public boolean canEnchantItem(Item itemIn)
        {
            boolean flag = itemIn instanceof ItemBlock && ((ItemBlock)itemIn).getBlock() instanceof BlockPumpkin;
            return itemIn instanceof ItemArmor || itemIn instanceof ItemElytra || itemIn instanceof ItemSkull || flag;
        }
    };

    private EnumEnchantmentType()
    {
    }

    private com.google.common.base.Predicate<Item> delegate = null;
    private EnumEnchantmentType(com.google.common.base.Predicate<Item> delegate)
    {
        this.delegate = delegate;
    }
    public boolean canEnchantItem(Item itemIn)
    {
        return this.delegate != null && this.delegate.apply(itemIn);
    }
}