package cok.SK2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public class main extends JavaPlugin implements Listener{

	public java.util.Random random = new java.util.Random();
	public ItemMeta meta;
	public int ConfigLuck ;
	
	@Override
	public void onEnable() {
		
		// Allows plugin to be registered in the server's Plugin Manager
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);

		// Retrieves the "Luck" set in the config.yml file
		
		ConfigLuck = getConfig().getInt("Luck");
		getLogger().info("SilkTouch2 Luck : " + ConfigLuck+" %");
		this.saveConfig();
	}

	// Randomly giving the Silk Touch II enchantment, according to the Luck set in the config.yml file
	
	@EventHandler
	public void testEnchant(EnchantItemEvent e) {

		// Checking that Silk Touch I is to be added to the item and that the item is not a book 
		
		if(e.getEnchantsToAdd().containsKey(Enchantment.SILK_TOUCH) & e.getItem().getType().equals(Material.BOOK)==false) { 
			int luck = random.nextInt(100);

			if(luck <= ConfigLuck) { // If True, Silk Touch II is to be given instead of Silk Touch I

				for(Enchantment enc : e.getEnchantsToAdd().keySet()) {

					if(enc.getKey().toString().toUpperCase().contains("SILK_TOUCH")) {
						e.getEnchantsToAdd().put(enc, 2);
					}
				}

			}
		}
	}
	
	
	
	// Avoid people getting the Silk Touch II enchantment simply by combining 2 Silk Touch I books
	
	@EventHandler
	public void testAnvil(PrepareAnvilEvent e) {

		// Must use a try /catch structure here because if e.getResults.hasItemMeta() == False an exception is raised
		
		try {
			if(e.getResult().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {

				int amp1 = 0;
				int amp2 = 0;
				
				if(e.getInventory().getItem(0).getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
					amp1 = e.getInventory().getItem(0).getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH);
				}
				
				if(e.getInventory().getItem(1).getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
					amp2 = e.getInventory().getItem(1).getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH);
				}

				if(amp1+amp2 > 2) {
					
					ItemMeta meta = e.getResult().getItemMeta();
					meta.addEnchant(Enchantment.SILK_TOUCH, 2, true);
					e.getResult().setItemMeta(meta);
				}
				
				if(amp1==amp2 & amp1 != 0) {
					
					e.getViewers().get(0).sendMessage(ChatColor.RED+"[Warning] Silk Touch II can only be obtain via the enchantement table");
					}
			}


		}catch(Exception ex) {}


	}
	
	
	
// Allow people to harvest spawners when using a Silk Touch II tool
	
	@EventHandler
	public void SpawnerBreak(BlockBreakEvent e) {

		if(e.getBlock().getType().equals(Material.SPAWNER)) {

			// Must use a try /catch structure here because if e.getPlayer().getInventory().getItemInMainHand().hasItemMeta() == False an exception is raised
			
			try {
				if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH )>1) {

					// Naming the spawner item after the Monster it spawns
					
					ItemStack sp = new ItemStack(Material.SPAWNER);
					CreatureSpawner Oldspawner = (CreatureSpawner) e.getBlock().getState();

					meta = sp.getItemMeta();
					meta.setDisplayName(ChatColor.GOLD +""+ ChatColor.BOLD + Oldspawner.getSpawnedType().toString());
					sp.setItemMeta(meta);
					
					e.getPlayer().getInventory().addItem(sp);
				}
				
				
			}catch(Exception ex) { // Canceling event to avoid trolls breaking all spawners 
				
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.BLUE +""+ ChatColor.BOLD +"Silk Touch II "+ ChatColor.BLUE +"is needed to collect spawners");

			}
			}

		}

	
	
	// Allow people to place spawners
	
	@EventHandler
	public void TestSpawnerPlace(BlockPlaceEvent e) {

		if(e.getBlockPlaced().getType().equals(Material.SPAWNER) & e.getItemInHand().hasItemMeta()) {

			CreatureSpawner spw = (CreatureSpawner) e.getBlockPlaced().getState();
			String type = e.getItemInHand().getItemMeta().getDisplayName();

			for(EntityType entTy : EntityType.values()) {

				if(type.contains(entTy.toString())) { // If True, sets spawner's type to this Monster
					spw.setSpawnedType(entTy);

					// Checking if config.yml data should be applied to placed spawners
					
					try {
						if(this.getConfig().get("Delay") != null) {
							spw.setDelay((int) this.getConfig().get("Delay"));
						}
						if(this.getConfig().get("MaxNearbyEntities") != null) {
							spw.setMaxNearbyEntities((int) this.getConfig().get("MaxNearbyEntities"));
						}
						if(this.getConfig().get("MaxSpawnDelay") != null) {
							spw.setMaxSpawnDelay((int) this.getConfig().get("MaxSpawnDelay"));
						}
						if(this.getConfig().get("MinSpawnDelay") != null) {
							spw.setMinSpawnDelay((int) this.getConfig().get("MinSpawnDelay"));
						}
						if(this.getConfig().get("RequiredPlayerRange") != null) {
							spw.setRequiredPlayerRange((int) this.getConfig().get("RequiredPlayerRange"));
						}
						if(this.getConfig().get("SpawnCount") != null) {
							spw.setSpawnCount((int) this.getConfig().get("Delay"));
						}
						if(this.getConfig().get("SpawnRange") != null) {
							spw.setSpawnRange((int) this.getConfig().get("SpawnRange"));
						}

					}catch(Exception ex) { // Sending the exception to all Admins online and to the Console

						for(Object ob : Bukkit.getOnlinePlayers().toArray()) {

							Player inter = (Player) ob;
							Player p = Bukkit.getPlayer(inter.getName());
							
							if(p.isOp()) {
								p.sendMessage(ChatColor.RED +"[ERROR] SilkTouch2's config error");
								p.sendMessage(ChatColor.RED +"[ERROR] -> " +ex.toString());
							}
						}
						getLogger().info("[ERROR] SilkTouch2's config error : "+ex.toString());
					}


					spw.update();
					break;
				}
			}

		}

	}


}






