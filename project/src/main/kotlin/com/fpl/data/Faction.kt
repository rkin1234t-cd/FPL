package com.fpl.data

import java.util.*

data class Faction(
    val id: String,
    val displayName: String,
    val type: FactionType,
    var leader: UUID?,
    val members: MutableMap<UUID, FactionMember> = mutableMapOf(),
    val ranks: MutableList<FactionRank> = mutableListOf(),
    var motd: String = "",
    var balance: Double = 0.0,
    val modules: MutableMap<String, Boolean> = mutableMapOf(),
    val baseLocation: Location? = null
) {
    fun addMember(player: UUID, rank: FactionRank) {
        members[player] = FactionMember(player, rank)
    }
    
    fun removeMember(player: UUID) {
        members.remove(player)
    }
    
    fun getMember(player: UUID): FactionMember? {
        return members[player]
    }
    
    fun getLeaderName(): String {
        return leader?.let { 
            org.bukkit.Bukkit.getOfflinePlayer(it).name ?: "Unknown"
        } ?: "None"
    }
    
    fun getRankById(id: Int): FactionRank? {
        return ranks.firstOrNull { it.id == id }
    }
    
    fun getNextRank(currentRank: FactionRank): FactionRank? {
        val currentIndex = ranks.indexOf(currentRank)
        return if (currentIndex > 0) ranks[currentIndex - 1] else null
    }
    
    fun getPreviousRank(currentRank: FactionRank): FactionRank? {
        val currentIndex = ranks.indexOf(currentRank)
        return if (currentIndex < ranks.size - 1) ranks[currentIndex + 1] else null
    }
    
    fun isLeader(player: UUID): Boolean {
        return leader == player
    }
    
    fun isDeputy(player: UUID): Boolean {
        return members[player]?.rank?.status == RankStatus.DEPUTY
    }
    
    fun isMp(player: UUID): Boolean {
        return members[player]?.rank?.status == RankStatus.MP
    }
}

data class FactionMember(
    val uuid: UUID,
    var rank: FactionRank,
    var specialization: FactionSpecialization? = null,
    var personalSalary: Double? = null,
    var joinDate: Date = Date()
)

data class FactionRank(
    val id: Int,
    val name: String,
    val displayName: String,
    val status: RankStatus,
    val permissions: MutableList<String> = mutableListOf(),
    val salary: Double = 0.0,
    val specializations: MutableList<FactionSpecialization> = mutableListOf()
) {
    fun getLuckPermsGroupName(factionId: String): String {
        return "${factionId}_${name}_${id}"
    }
}

data class FactionSpecialization(
    val id: Int,
    val name: String,
    val displayName: String,
    val permissions: MutableList<String> = mutableListOf(),
    val salary: Double = 0.0
) {
    fun getLuckPermsGroupName(factionId: String, rankName: String, rankId: Int): String {
        return "${factionId}_${rankName}_${rankId}_spec${id}"
    }
}

data class Location(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0f,
    val pitch: Float = 0f
)

enum class FactionType {
    USUALLY, PARLIAMENT
}

enum class RankStatus {
    DEFAULT, DEPUTY, LEADER, MP
}