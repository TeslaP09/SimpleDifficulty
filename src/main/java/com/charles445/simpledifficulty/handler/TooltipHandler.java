package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDEnchantments;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;
import java.util.List;

public class TooltipHandler
{
	//CLIENT only
	
	private final DecimalFormat df = new DecimalFormat("#.##");

	private float processStackJSON(ItemStack stack)
	{
		List<JsonTemperatureIdentity> armorList = JsonConfig.armorTemperatures.get(stack.getItem().getRegistryName().toString());

		if(armorList!=null)
		{
			for(JsonTemperatureIdentity jtm : armorList)
			{
				if(jtm==null)
					continue;

				if(jtm.matches(stack))
				{
					return jtm.temperature;
				}
			}
		}

		return 0.0f;
	}

	
	@SubscribeEvent
	public void onItemTooltipEvent(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();

			//Has armor temperature tag
			float sum = TemperatureUtil.getArmorTemperatureTag(stack);
			sum += processStackJSON(stack);

			if(ModConfig.server.miscellaneous.registerEnchantments)
			{
				if(EnchantmentHelper.getEnchantmentLevel(SDEnchantments.chilling, stack) > 0)
				{
					sum -= ModConfig.server.temperature.enchantmentTemperature;
				}
				else if(EnchantmentHelper.getEnchantmentLevel(SDEnchantments.heating, stack) > 0)
				{
					sum += ModConfig.server.temperature.enchantmentTemperature;
				}
			}

		if(sum!=0.0f)
		{
			if(sum>0.0f)
			{
				event.getToolTip().add(TextFormatting.DARK_RED + " Temperature +" + df.format(sum));
			}
			else
			{
				event.getToolTip().add(TextFormatting.DARK_BLUE + " Temperature " + df.format(sum));
			}
		}
	}
}
