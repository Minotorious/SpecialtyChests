package SpecialtyChests;

import java.io.File;
import net.risingworld.api.Plugin;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.player.inventory.PlayerInventoryToChestEvent;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerChestAccessEvent;
import net.risingworld.api.events.player.PlayerSpawnEvent;
import net.risingworld.api.objects.Player;
import net.risingworld.api.objects.Item;
import java.util.ArrayList;
import net.risingworld.api.utils.Utils;

public class SpecialtyChestsMain extends Plugin implements Listener {
    
    @Override
    public void onEnable(){
        
        registerEventListener(this);
        readChests();
    }
    
    public void readChests(){
        File ChestsTxt = new File(getPath() + "/assets/chests.txt");
        if (ChestsTxt.exists()){
            String content = Utils.FileUtils.readStringFromFile(ChestsTxt);
            if(content != null && !content.isEmpty()){
                String[] lines = content.split("\r\n|\n|\r");
                SpecialtyChests =  new ArrayList<>();
                for (String line : lines) {
                    String[] cline = line.split(";");
                    if (cline.length >= 2){
                        SpecialtyChest SC = new SpecialtyChest();
                        SC.idNo = Integer.parseInt(cline[0]);
                        SC.allowedItems = new String[cline.length-1];
                        for (int i=1;i<cline.length;i++){
                            SC.allowedItems[i-1] = cline[i];
                        }
                        SpecialtyChests.add(SC);
                    }
                }
            }
            else{
                SpecialtyChests =  new ArrayList<>();
            }
        }
        else{
            SpecialtyChests =  new ArrayList<>();
        }
    }
    
    @Override
    public void onDisable(){
        
    }
    
    public class SpecialtyChest{
        public int idNo;
        public String[] allowedItems;
    }
    
    public static ArrayList<SpecialtyChest> SpecialtyChests;
    
    @EventMethod
    public void onInventoryToChest(PlayerInventoryToChestEvent event){
        Player player = event.getPlayer();
        int chestID = event.getChestID();
        
        for (SpecialtyChest chest : SpecialtyChests){
            if (chestID == chest.idNo){
                Item item = event.getItem();
                boolean itemAllowed = false;
                if (item != null){
                    if (item.getName() != null){
                        for (String itemName : chest.allowedItems){
                            if (item.getName().equals(itemName)){
                                itemAllowed = true;
                                break;
                            }
                        }
                        if (!itemAllowed){
                            event.setCancelled(true);
                            player.sendTextMessage("[#00AEE1]" + item.getName() + " is not allowed in this chest!");
                        }
                    }
                }
                break;
            }
        }
    }
    
    @EventMethod
    public void onPlayerCommand(PlayerCommandEvent event){
        Player player = event.getPlayer();
        if (player.isAdmin()){
            String command = event.getCommand();
            
            if (command.equals("/sc")){
                if(player.hasAttribute("MinoShowChestIDs")){
                    boolean ShowChestIDs = (boolean) player.getAttribute("MinoShowChestIDs");
                    if (ShowChestIDs){
                        player.setAttribute("MinoShowChestIDs", false);
                        player.sendTextMessage("[#00AEE1]Chest IDs not showing anymore");
                    }
                    else{
                        player.setAttribute("MinoShowChestIDs", true);
                        player.sendTextMessage("[#00AEE1]Chest IDs now showing when opening chests");
                    }
                }
            }
            else if (command.equals("/sc reload")){
                readChests();
                player.sendTextMessage("[#00AEE1]chests.txt file has been reloaded successfully");
            }
        }
    }
    
    @EventMethod
    public void onChestAccess(PlayerChestAccessEvent event){
        Player player = event.getPlayer();
        if (player.isAdmin()){
            if(player.hasAttribute("MinoShowChestIDs")){
                boolean ShowChestIDs = (boolean) player.getAttribute("MinoShowChestIDs");
                if (ShowChestIDs){
                    player.sendTextMessage("[#00AEE1]This chest's unique ID is: " + Integer.toString(event.getChestID()));
                }
            }
        }
    }
    
    @EventMethod
    public void onSpawn(PlayerSpawnEvent event){
        Player player = event.getPlayer();
        if (player.isAdmin()){
            player.setAttribute("MinoShowChestIDs", false);
        }
    }
}
