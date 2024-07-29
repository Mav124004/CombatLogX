package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public final class SubCommandUntag extends CombatLogCommand {
    public SubCommandUntag(@NotNull ICombatLogX plugin) {
        super(plugin, "untag");
        setPermissionName("combatlogx.command.combatlogx.untag");
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> valueSet = new HashSet<>(getOnlinePlayerNames());
            valueSet.add("*");
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length < 1) {
            return false;
        }

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();

        if (args[0].equals("*")) {
            List<Player> playersInCombat = combatManager.getPlayersInCombat();
            for (Player player : playersInCombat) {
                combatManager.untag(player, UntagReason.EXPIRE);
            }
            sendMessageWithPrefix(sender, "command.combatlogx.untag-all");
            return true;
        }

        Player target = findTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = target.getName();
        Replacer replacer = new StringReplacer("{target}", targetName);

        if (!combatManager.isInCombat(target)) {
            sendMessageWithPrefix(sender, "error.target-not-in-combat", replacer);
            return true;
        }

        combatManager.untag(target, UntagReason.EXPIRE);
        sendMessageWithPrefix(sender, "command.combatlogx.untag-player", replacer);
        return true;
    }
}
