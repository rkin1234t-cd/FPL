package com.fpl

import com.fpl.commands.*
import com.fpl.managers.*
import com.fpl.placeholders.FPLPlaceholders
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin

class FPLPlugin : JavaPlugin() {
    
    companion object {
        lateinit var instance: FPLPlugin
            private set
        lateinit var luckPerms: LuckPerms
            private set
    }
    
    lateinit var factionManager: FactionManager
    lateinit var luckPermsManager: LuckPermsManager
    
    override fun onEnable() {
        instance = this
        
        // Инициализация LuckPerms
        val provider: RegisteredServiceProvider<LuckPerms>? = 
            Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
        if (provider != null) {
            luckPerms = provider.provider
        } else {
            logger.severe("LuckPerms не найден! Плагин отключается.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }
        
        // Создание папок и конфигов
        saveDefaultConfig()
        
        // Инициализация менеджеров
        factionManager = FactionManager(this)
        luckPermsManager = LuckPermsManager()
        
        // Регистрация команд
        registerCommands()
        
        // Регистрация PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            FPLPlaceholders().register()
            logger.info("PlaceholderAPI интеграция включена")
        }
        
        logger.info("FPL Plugin успешно загружен!")
    }
    
    override fun onDisable() {
        logger.info("FPL Plugin отключен!")
    }
    
    private fun registerCommands() {
        getCommand("fps")?.setExecutor(FpsCommand())
        getCommand("f")?.setExecutor(FactionCommand())
        getCommand("ud")?.setExecutor(AdminCommand())
    }
}