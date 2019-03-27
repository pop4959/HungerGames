package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;
import me.minebuilders.hg.Util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeleteCmd extends BaseCmd {

	public DeleteCmd() {
		forcePlayer = false;
		cmdName = "delete";
		forceInGame = false;
		argLength = 2;
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {
		Game g = HG.manager.getGame(args[1]);
		if (g != null) {
			try {
				Util.scm(sender, HG.lang.cmd_delete_attempt.replace("<arena>", g.getName()));

				if (g.getStatus() == Status.BEGINNING || g.getStatus() == Status.RUNNING) {
					Util.scm(sender, "  &7- &cGame running! &aStopping..");
					g.forceRollback();
					g.stop(false);
				}
				if (!g.getPlayers().isEmpty()) {
					Util.msg(sender, HG.lang.cmd_delete_kicking);
					for (UUID u : g.getPlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p != null) {
							g.leave(p, false);
						}
					}
				}
				HG.arenaconfig.getCustomConfig().set("arenas." + args[1], null);
				HG.arenaconfig.saveCustomConfig();
				Util.scm(sender, HG.lang.cmd_delete_deleted.replace("<arena>", g.getName()));
				HG.plugin.games.remove(g);
			} catch (Exception e) {
				Util.scm(sender, HG.lang.cmd_delete_failed);
			}
		} else {
			Util.scm(sender, HG.lang.cmd_delete_noexist);
		}
		return true;
	}
}