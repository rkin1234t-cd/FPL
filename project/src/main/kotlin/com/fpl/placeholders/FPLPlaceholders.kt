package com.fpl.placeholders

import com.fpl.FPLPlugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class FPLPlaceholders : PlaceholderExpansion() {
    
    override fun getIdentifier(): String = "fpl"
    
    override fun getAuthor(): String = "FPL"
    
    override fun getVersion(): String = "1.0.0"
    
    override fun persist(): Boolean = true
    
    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null || !player.isOnline) {
            return null
        }
        
        val factionManager = FPLPlugin.instance.factionManager
        val faction = factionManager.getPlayerFaction(player.uniqueId)
        
        return when (params.lowercase()) {
            // %fpl_fraction_player% - плейсхолдер фракции где находится игрок
            "fraction_player" -> {
                faction?.displayName ?: "Нет фракции"
            }
            
            // %fpl_fraction_leader% - лидер фракции
            "fraction_leader" -> {
                faction?.getLeaderName() ?: "Нет лидера"
            }
            
            // %fpl_fraction_rang% - ранг игрока или специальность
            "fraction_rang" -> {
                if (faction == null) {
                    return "Нет ранга"
                }
                
                val member = faction.getMember(player.uniqueId)
                if (member == null) {
                    return "Нет ранга"
                }
                
                // Если есть специализация, показываем её
                member.specialization?.displayName ?: member.rank.displayName
            }
            
            else -> null
        }
    }
    
    /**
     * Получить плейсхолдер для конкретного игрока
     * Использование: %fpl_fraction_player_<player_name>%
     */
    fun getFractionForPlayer(playerName: String): String {
        val player = org.bukkit.Bukkit.getOfflinePlayer(playerName)
        val faction = FPLPlugin.instance.factionManager.getPlayerFaction(player.uniqueId)
        return faction?.displayName ?: "Нет фракции"
    }
    
    /**
     * Получить ранг для конкретного игрока
     * Использование: %fpl_fraction_rang_<player_name>%
     */
    fun getRangForPlayer(playerName: String): String {
        val player = org.bukkit.Bukkit.getOfflinePlayer(playerName)
        val faction = FPLPlugin.instance.factionManager.getPlayerFaction(player.uniqueId)
        
        if (faction == null) {
            return "Нет ранга"
        }
        
        val member = faction.getMember(player.uniqueId)
        if (member == null) {
            return "Нет ранга"
        }
        
        return member.specialization?.displayName ?: member.rank.displayName
    }
}