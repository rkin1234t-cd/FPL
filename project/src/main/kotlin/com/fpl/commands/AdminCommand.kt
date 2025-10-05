package com.fpl.commands

import com.fpl.FPLPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AdminCommand : CommandExecutor {
    
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        
        if (!sender.hasPermission("fpl.admin")) {
            sender.sendMessage("§cУ вас нет прав для использования этой команды!")
            return true
        }
        
        if (args.isEmpty()) {
            sender.sendMessage("§6=== Админские команды FPL ===")
            sender.sendMessage("§e/ud §7- Меню управления всеми фракциями")
            sender.sendMessage("§e/ud <фракция> <команда> §7- Выполнить команду от лица лидера")
            return true
        }
        
        // TODO: Implement admin menu and commands
        sender.sendMessage("§aАдминская функция в разработке")
        
        return true
    }
}