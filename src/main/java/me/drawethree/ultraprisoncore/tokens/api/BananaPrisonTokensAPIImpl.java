package me.drawethree.ultraprisoncore.tokens.api;

import me.drawethree.ultraprisoncore.api.enums.ReceiveCause;
import me.drawethree.ultraprisoncore.tokens.managers.TokensManager;
import org.bukkit.OfflinePlayer;

public class BananaPrisonTokensAPIImpl implements BananaPrisonTokensAPI {


    private TokensManager manager;

    public BananaPrisonTokensAPIImpl(TokensManager manager) {

        this.manager = manager;
    }

    @Override
    public long getPlayerTokens(OfflinePlayer p) {
        return this.manager.getPlayerTokens(p);
    }

    @Override
    public boolean hasEnough(OfflinePlayer p, long amount) {
		return this.getPlayerTokens(p) >= amount;
    }

    @Override
    public void removeTokens(OfflinePlayer p, long amount) {
        this.manager.removeTokens(p, amount, null);
    }

    @Override
    public void addTokens(OfflinePlayer p, long amount) {
		this.manager.giveTokens(p, amount, null, ReceiveCause.GIVE);
    }
}
