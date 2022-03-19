package me.drawethree.ultraprisoncore.currency.api;

import me.drawethree.ultraprisoncore.currency.BananaPrisonBananaCurrency;
import org.bukkit.inventory.ItemStack;

public class BananaPrisonBananaCurrencyAPIImpl implements IBananaPrisonBananaCurrencyAPI {

    private final BananaPrisonBananaCurrency module;

    public BananaPrisonBananaCurrencyAPIImpl(BananaPrisonBananaCurrency plugin) {
        this.module = plugin;
    }


    @Override
    public boolean isValid(ItemStack item) {
        return module.checkValidBanana(item);
    }
}
