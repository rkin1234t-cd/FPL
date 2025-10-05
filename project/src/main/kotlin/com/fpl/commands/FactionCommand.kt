package com.fpl.commands

import com.fpl.FPLPlugin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class FactionCommand : CommandExecutor, TabCompleter {
    
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
        
        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }
        
        when (args[0].lowercase()) {
            "invite" -> handleInvite(sender, args)
            "kick" -> handleKick(sender, args)
            "deputy" -> handleDeputy(sender, args)
            "promote" -> handlePromote(sender, args)
            "demote" -> handleDemote(sender, args)
            "info" -> handleInfo(sender)
            "list" -> handleList(sender)
            "leaders" -> handleLeaders(sender)
            "quit" -> handleQuit(sender)
            "help" -> sendHelp(sender)
            else -> sender.sendMessage("§cНеизвестная подкоманда. Используйте /f help")
        }
        
        return true
    }
    
    private fun handleInvite(sender: Player, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("§cИспользование: /f invite <ник>")
            return
        }
        
        // TODO: Implement invite logic
        sender.sendMessage("§aПриглашение отправлено игроку ${args[1]}")
    }
    
    private fun handleKick(sender: Player, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("§cИспользование: /f kick <ник>")
            return
        }
        
        // TODO: Implement kick logic
        sender.sendMessage("§aИгрок ${args[1]} уволен из фракции")
    }
    
    private fun handleDeputy(sender: Player, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("§cИспользование: /f deputy <ник>")
            return
        }
        
        // TODO: Implement deputy logic
        sender.sendMessage("§aИгрок ${args[1]} назначен заместителем")
    }
    
    private fun handlePromote(sender: Player, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("§cИспользование: /f promote <ник>")
            return
        }
        
        // TODO: Implement promote logic
        sender.sendMessage("§aИгрок ${args[1]} повышен по рангу")
    }
    
    private fun handleDemote(sender: Player, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("§cИспользование: /f demote <ник>")
            return
        }
        
        // TODO: Implement demote logic
        sender.sendMessage("§aИгрок ${args[1]} понижен по рангу")
    }
    
    private fun handleInfo(sender: Player) {
        val faction = FPLPlugin.instance.factionManager.getPlayerFaction(sender.uniqueId)
        
        if (faction == null) {
            sender.sendMessage("§cВы не состоите ни в одной фракции!")
            return
        }
        
        sender.sendMessage("§6=== Информация о фракции ===")
        sender.sendMessage("§eНазвание: §f${faction.displayName}")
        sender.sendMessage("§eЛидер: §f${faction.getLeaderName()}")
        sender.sendMessage("§eУчастников: §f${faction.members.size}")
        sender.sendMessage("§eБаланс казны: §f${faction.balance}")
    }
    
    private fun handleList(sender: Player) {
        val factions = FPLPlugin.instance.factionManager.getAllFactions()
        
        sender.sendMessage("§6=== Список фракций ===")
        factions.forEach { faction ->
            sender.sendMessage("§e${faction.displayName} §7- §f${faction.getLeaderName()} §7(${faction.members.size} участников)")
        }
    }
    
    private fun handleLeaders(sender: Player) {
        val factions = FPLPlugin.instance.factionManager.getAllFactions()
        
        sender.sendMessage("§6=== Лидеры фракций ===")
        factions.forEach { faction ->
            val leaderName = faction.getLeaderName()
            val status = if (faction.leader?.let { Bukkit.getPlayer(it)?.isOnline } == true) "§aОнлайн" else "§cОффлайн"
            sender.sendMessage("§e${faction.displayName} §7- §f$leaderName §7($status§7)")
        }
    }
    
    private fun handleQuit(sender: Player) {
        val factionManager = FPLPlugin.instance.factionManager
        
        if (factionManager.removePlayerFromFaction(sender.uniqueId)) {
            sender.sendMessage("§aВы успешно покинули фракцию!")
        } else {
            sender.sendMessage("§cВы не состоите ни в одной фракции!")
        }
    }
    
    private fun sendHelp(sender: Player) {
        sender.sendMessage("§6=== Команды фракций ===")
        sender.sendMessage("§e/f invite <ник> §7- Пригласить игрока")
        sender.sendMessage("§e/f kick <ник> §7- Уволить игрока")
        sender.sendMessage("§e/f deputy <ник> §7- Назначить заместителем")
        sender.sendMessage("§e/f promote <ник> §7- Повысить по рангу")
        sender.sendMessage("§e/f demote <ник> §7- Понизить по рангу")
        sender.sendMessage("§e/f info §7- Информация о фракции")
        sender.sendMessage("§e/f list §7- Список всех фракций")
        sender.sendMessage("§e/f leaders §7- Список лидеров")
        sender.sendMessage("§e/f quit §7- Покинуть фракцию")
    }
    
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            val subcommands = listOf(
                "invite", "kick", "deputy", "promote", "demote", 
                "info", "list", "leaders", "quit", "help"
            )
            return subcommands.filter { it.startsWith(args[0].lowercase()) }
        }
        
        if (args.size == 2 && args[0].lowercase() in listOf("invite", "kick", "deputy", "promote", "demote")) {
            return Bukkit.getOnlinePlayers().map { it.name }
        }
        
        return emptyList()
    }
}