package com.fpl.managers

import com.fpl.FPLPlugin
import com.fpl.data.*
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.node.types.DisplayNameNode
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.PermissionNode
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.CompletableFuture

class LuckPermsManager {
    
    private val luckPerms = FPLPlugin.luckPerms
    
    /**
     * Создает структуру LuckPerms для фракции
     */
    fun createFactionStructure(faction: Faction) {
        // Создаем трек
        val trackName = "faction_${faction.id}"
        val track = luckPerms.trackManager.createAndLoadTrack(trackName).join()
        
        // Создаем группы для рангов
        faction.ranks.forEach { rank ->
            createRankGroup(faction, rank)
        }
        
        // Обновляем трек
        updateTrack(faction)
    }
    
    /**
     * Удаляет структуру LuckPerms для фракции
     */
    fun deleteFactionStructure(faction: Faction) {
        // Удаляем все группы рангов
        faction.ranks.forEach { rank ->
            deleteRankGroup(faction, rank)
        }
        
        // Удаляем трек
        val trackName = "faction_${faction.id}"
        luckPerms.trackManager.deleteTrack(trackName)
    }
    
    /**
     * Устанавливает ранг фракции игроку
     */
    fun setPlayerFactionRank(playerId: UUID, faction: Faction, rank: FactionRank): CompletableFuture<Void> {
        return luckPerms.userManager.loadUser(playerId).thenCompose { user ->
            if (user == null) {
                return@thenCompose CompletableFuture.completedFuture<Void>(null)
            }
            
            // Удаляем все старые ранги фракции
            removePlayerFromFaction(playerId, faction)
            
            // Добавляем новый ранг
            val groupName = rank.getLuckPermsGroupName(faction.id)
            val inheritanceNode = InheritanceNode.builder(groupName).build()
            user.data().add(inheritanceNode)
            
            luckPerms.userManager.saveUser(user)
        }
    }
    
    /**
     * Убирает игрока из фракции
     */
    fun removePlayerFromFaction(playerId: UUID, faction: Faction): CompletableFuture<Void> {
        return luckPerms.userManager.loadUser(playerId).thenCompose { user ->
            if (user == null) {
                return@thenCompose CompletableFuture.completedFuture<Void>(null)
            }
            
            // Удаляем все группы фракции
            faction.ranks.forEach { rank ->
                val groupName = rank.getLuckPermsGroupName(faction.id)
                user.data().remove(InheritanceNode.builder(groupName).build())
                
                // Удаляем специализации
                rank.specializations.forEach { spec ->
                    val specGroupName = spec.getLuckPermsGroupName(faction.id, rank.name, rank.id)
                    user.data().remove(InheritanceNode.builder(specGroupName).build())
                }
            }
            
            luckPerms.userManager.saveUser(user)
        }
    }
    
    /**
     * Устанавливает специализацию игроку
     */
    fun setPlayerSpecialization(
        playerId: UUID, 
        faction: Faction, 
        rank: FactionRank, 
        specialization: FactionSpecialization
    ): CompletableFuture<Void> {
        return luckPerms.userManager.loadUser(playerId).thenCompose { user ->
            if (user == null) {
                return@thenCompose CompletableFuture.completedFuture<Void>(null)
            }
            
            // Удаляем старые специализации
            removePlayerSpecialization(playerId, faction, rank)
            
            // Добавляем новую специализацию
            val specGroupName = specialization.getLuckPermsGroupName(faction.id, rank.name, rank.id)
            val inheritanceNode = InheritanceNode.builder(specGroupName).build()
            user.data().add(inheritanceNode)
            
            luckPerms.userManager.saveUser(user)
        }
    }
    
    /**
     * Убирает специализацию у игрока
     */
    fun removePlayerSpecialization(
        playerId: UUID, 
        faction: Faction, 
        rank: FactionRank
    ): CompletableFuture<Void> {
        return luckPerms.userManager.modifyUser(playerId) { user ->
            rank.specializations.forEach { spec ->
                val specGroupName = spec.getLuckPermsGroupName(faction.id, rank.name, rank.id)
                user.data().remove(InheritanceNode.builder(specGroupName).build())
            }
        }
    }
    
    /**
     * Создает группу для ранга
     */
    private fun createRankGroup(faction: Faction, rank: FactionRank) {
        val groupName = rank.getLuckPermsGroupName(faction.id)
        val group = luckPerms.groupManager.createAndLoadGroup(groupName).join()
        
        // Устанавливаем displayName
        val displayNameNode = DisplayNameNode.builder(rank.displayName).build()
        group.data().add(displayNameNode)
        
        // Добавляем права
        rank.permissions.forEach { permission ->
            val permissionNode = PermissionNode.builder(permission).build()
            group.data().add(permissionNode)
        }
        
        // Сохраняем группу
        luckPerms.groupManager.saveGroup(group)
        
        // Создаем группы для специализаций
        rank.specializations.forEach { spec ->
            createSpecializationGroup(faction, rank, spec)
        }
    }
    
    /**
     * Создает группу для специализации
     */
    private fun createSpecializationGroup(faction: Faction, rank: FactionRank, specialization: FactionSpecialization) {
        val groupName = specialization.getLuckPermsGroupName(faction.id, rank.name, rank.id)
        val group = luckPerms.groupManager.createAndLoadGroup(groupName).join()
        
        // Устанавливаем displayName
        val displayNameNode = DisplayNameNode.builder(specialization.displayName).build()
        group.data().add(displayNameNode)
        
        // Наследуем от основной группы ранга
        val rankGroupName = rank.getLuckPermsGroupName(faction.id)
        val inheritanceNode = InheritanceNode.builder(rankGroupName).build()
        group.data().add(inheritanceNode)
        
        // Добавляем дополнительные права
        specialization.permissions.forEach { permission ->
            val permissionNode = PermissionNode.builder(permission).build()
            group.data().add(permissionNode)
        }
        
        luckPerms.groupManager.saveGroup(group)
    }
    
    /**
     * Удаляет группу ранга
     */
    private fun deleteRankGroup(faction: Faction, rank: FactionRank) {
        // Первым делом удаляем специализации
        rank.specializations.forEach { spec ->
            val specGroupName = spec.getLuckPermsGroupName(faction.id, rank.name, rank.id)
            luckPerms.groupManager.deleteGroup(specGroupName)
        }
        
        // Затем удаляем основную группу
        val groupName = rank.getLuckPermsGroupName(faction.id)
        luckPerms.groupManager.deleteGroup(groupName)
    }
    
    /**
     * Обновляет трек фракции
     */
    private fun updateTrack(faction: Faction) {
        val trackName = "faction_${faction.id}"
        luckPerms.trackManager.loadTrack(trackName).thenAccept { track ->
            if (track != null) {
                // Очищаем трек
                track.groups.clear()
                
                // Добавляем группы в порядке повышения
                faction.ranks.sortedBy { it.id }.forEach { rank ->
                    val groupName = rank.getLuckPermsGroupName(faction.id)
                    track.appendGroup(groupName)
                }
                
                luckPerms.trackManager.saveTrack(track)
            }
        }
    }
}