package com.fpl.menus

import com.fpl.data.Faction
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class FactionMainMenu(private val faction: Faction, private val player: Player) {
    
    fun open() {
        val inventory = Bukkit.createInventory(null, 54, "§6Меню фракции: ${faction.displayName}")
        
        // Информация о фракции
        addItem(inventory, 13, Material.PAPER, "§eИнформация о фракции", listOf(
            "§7Название: §f${faction.displayName}",
            "§7Лидер: §f${faction.getLeaderName()}",
            "§7Участников: §f${faction.members.size}",
            "§7Баланс казны: §f${faction.balance} монет"
        ))
        
        // Управление участниками (только для лидера и заместителей)
        if (faction.isLeader(player.uniqueId) || faction.isDeputy(player.uniqueId)) {
            addItem(inventory, 21, Material.PLAYER_HEAD, "§aУправление участниками", listOf(
                "§7Приглашать новых участников",
                "§7Увольнять участников",
                "§7Повышать и понижать по рангам"
            ))
        }
        
        // Казна фракции
        addItem(inventory, 23, Material.GOLD_INGOT, "§6Казна фракции", listOf(
            "§7Баланс: §f${faction.balance} монет",
            "§7Нажмите для управления"
        ))
        
        // Настройки фракции (только для лидера)
        if (faction.isLeader(player.uniqueId)) {
            addItem(inventory, 31, Material.REDSTONE, "§cНастройки фракции", listOf(
                "§7Изменить настройки фракции",
                "§7Управлять рангами",
                "§7Модули фракции"
            ))
        }
        
        // Закрыть меню
        addItem(inventory, 49, Material.BARRIER, "§cЗакрыть", listOf(
            "§7Нажмите для закрытия меню"
        ))
        
        player.openInventory(inventory)
    }
    
    private fun addItem(inventory: Inventory, slot: Int, material: Material, name: String, lore: List<String>) {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        meta?.lore = lore
        item.itemMeta = meta
        inventory.setItem(slot, item)
    }
}