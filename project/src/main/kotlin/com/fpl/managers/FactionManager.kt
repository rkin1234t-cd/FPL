package com.fpl.managers

import com.fpl.FPLPlugin
import com.fpl.data.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class FactionManager(private val plugin: FPLPlugin) {
    
    private val factions = mutableMapOf<String, Faction>()
    private val playerFactions = mutableMapMap<UUID, String>()
    private val factionsFile: File = File(plugin.dataFolder, "factions.yml")
    private lateinit var factionsConfig: FileConfiguration
    
    init {
        loadFactions()
    }
    
    fun getFaction(id: String): Faction? {
        return factions[id]
    }
    
    fun getPlayerFaction(player: UUID): Faction? {
        val factionId = playerFactions[player] ?: return null
        return factions[factionId]
    }
    
    fun createFaction(
        id: String,
        displayName: String,
        type: FactionType,
        leader: UUID
    ): Boolean {
        if (factions.containsKey(id)) {
            return false
        }
        
        val faction = Faction(
            id = id,
            displayName = displayName,
            type = type,
            leader = leader
        )
        
        // Создаем базовые ранги
        val leaderRank = FactionRank(1, "leader", "Лидер", RankStatus.LEADER)
        val memberRank = FactionRank(2, "member", "Сотрудник", RankStatus.DEFAULT)
        
        faction.ranks.add(leaderRank)
        faction.ranks.add(memberRank)
        
        // Добавляем лидера
        faction.addMember(leader, leaderRank)
        
        factions[id] = faction
        playerFactions[leader] = id
        
        // Создаем LuckPerms структуру
        plugin.luckPermsManager.createFactionStructure(faction)
        
        saveFactions()
        return true
    }
    
    fun deleteFaction(id: String): Boolean {
        val faction = factions[id] ?: return false
        
        // Удаляем всех участников из карты
        faction.members.keys.forEach { player ->
            playerFactions.remove(player)
        }
        
        // Удаляем LuckPerms структуру
        plugin.luckPermsManager.deleteFactionStructure(faction)
        
        factions.remove(id)
        saveFactions()
        return true
    }
    
    fun addPlayerToFaction(player: UUID, factionId: String, rank: FactionRank): Boolean {
        val faction = factions[factionId] ?: return false
        
        // Проверяем, нет ли игрока уже в фракции
        if (playerFactions.containsKey(player)) {
            return false
        }
        
        faction.addMember(player, rank)
        playerFactions[player] = factionId
        
        // Обновляем LuckPerms
        plugin.luckPermsManager.setPlayerFactionRank(player, faction, rank)
        
        saveFactions()
        return true
    }
    
    fun removePlayerFromFaction(player: UUID): Boolean {
        val factionId = playerFactions[player] ?: return false
        val faction = factions[factionId] ?: return false
        
        faction.removeMember(player)
        playerFactions.remove(player)
        
        // Убираем из LuckPerms
        plugin.luckPermsManager.removePlayerFromFaction(player, faction)
        
        saveFactions()
        return true
    }
    
    fun promotePlayer(player: UUID): Boolean {
        val faction = getPlayerFaction(player) ?: return false
        val member = faction.getMember(player) ?: return false
        val nextRank = faction.getNextRank(member.rank) ?: return false
        
        member.rank = nextRank
        plugin.luckPermsManager.setPlayerFactionRank(player, faction, nextRank)
        
        saveFactions()
        return true
    }
    
    fun demotePlayer(player: UUID): Boolean {
        val faction = getPlayerFaction(player) ?: return false
        val member = faction.getMember(player) ?: return false
        val prevRank = faction.getPreviousRank(member.rank) ?: return false
        
        member.rank = prevRank
        plugin.luckPermsManager.setPlayerFactionRank(player, faction, prevRank)
        
        saveFactions()
        return true
    }
    
    fun setPlayerSpecialization(player: UUID, specialization: FactionSpecialization?): Boolean {
        val faction = getPlayerFaction(player) ?: return false
        val member = faction.getMember(player) ?: return false
        
        member.specialization = specialization
        
        if (specialization != null) {
            plugin.luckPermsManager.setPlayerSpecialization(player, faction, member.rank, specialization)
        } else {
            plugin.luckPermsManager.removePlayerSpecialization(player, faction, member.rank)
        }
        
        saveFactions()
        return true
    }
    
    fun getAllFactions(): List<Faction> {
        return factions.values.toList()
    }
    
    private fun loadFactions() {
        if (!factionsFile.exists()) {
            plugin.saveResource("factions.yml", false)
        }
        
        factionsConfig = YamlConfiguration.loadConfiguration(factionsFile)
        
        // Загрузка фракций из конфига
        // Тут будет логика загрузки
        plugin.logger.info("Фракции загружены")
    }
    
    private fun saveFactions() {
        // Логика сохранения в YAML
    }
}