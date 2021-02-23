package me.fckml.crates.managers;

import com.battlehcf.chatcolor.CC;
import me.fckml.crates.Crates;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Key {

	private Crates crates;
	private String crateName = "";
	private Material material = Material.CHEST;
	private String name = "";
	private boolean enchanted = false;
	private List<String> keyLore;

	public Key(String crateName, Material material, String name, boolean enchanted, Crates crates, List<String> keyLore) {
		this.crates = crates;
		this.crateName = crateName;
		if (material == null)
			material = Material.TRIPWIRE_HOOK;
		this.material = material;
		this.name = name;
		this.enchanted = enchanted;
		this.keyLore = keyLore;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnchanted() {
		return enchanted;
	}

	public void setEnchanted(boolean enchanted) {
		this.enchanted = enchanted;
	}

	public ItemStack getKeyItem(Integer amount) {
		ItemStack keyItem = new ItemStack(getMaterial());
		if (isEnchanted())
			keyItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta keyItemMeta = keyItem.getItemMeta();
		String title = getName().replaceAll("%type%", getCrate().getName(true));
		keyItemMeta.setDisplayName(title);
		List<String> lore = new ArrayList<>();

		for (String loreLines : keyLore) {
			lore.add(CC.translate(loreLines).replace("<crate_name>", getCrate().getName(true)));
		}

		keyItemMeta.setLore(lore);
		keyItem.setItemMeta(keyItemMeta);
		if (amount > 1)
			keyItem.setAmount(amount);
		return keyItem;
	}

	public String getCrateName() {
		return crateName;
	}

	public Crate getCrate() {
		return crates.getConfigHandler().getCrate(getCrateName().toLowerCase());
	}

}
