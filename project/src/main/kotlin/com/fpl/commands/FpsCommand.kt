package com.fpl.commands

import com.fpl.FPLPlugin
import com.fpl.menus.FactionMainMenu
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FpsCommand : CommandExecutor {
    
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        
        if (sender !is Player) {
            sender.sendMessage("§cЭта команда доступна только игрокам!")
            return true
        }
        
        val factionManager = FPLPlugin.instance.factionManager
        val faction = factionManager.getPlayerFaction(sender.uniqueId)
        
        if (faction == null) {
            sender.sendMessage("§cВы не состоите ни в одной фракции!")
            return true
        }
        
        // Открываем главное меню фракции
        FactionMainMenu(faction, sender).open()
        
        return true
    }
}