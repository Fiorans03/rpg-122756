package it.unicam.cs.mpgc.rpg122756.model;

import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import it.unicam.cs.mpgc.rpg122756.model.items.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(10);
    }

    @Test
    void testInitialCapacity() {
        assertEquals(10, inventory.getMaxSlots());
        assertEquals(0, inventory.getUsedSlots());
        assertEquals(10, inventory.getAvailableSlots());
    }

    @Test
    void testAddItem() {
        Item item = new Ingredient("Test Item", "Description", 10, 1);
        boolean added = inventory.addItem(item);

        assertTrue(added);
        assertEquals(1, inventory.getUsedSlots());
        assertEquals(9, inventory.getAvailableSlots());
    }

    @Test
    void testAddItem_InventoryFull() {
        for (int i = 0; i < 10; i++) {
            inventory.addItem(new Ingredient("Item " + i, "Desc", 10, 1));
        }

        Item extraItem = new Ingredient("Extra", "Desc", 10, 1);
        boolean added = inventory.addItem(extraItem);

        assertFalse(added);
        assertEquals(10, inventory.getUsedSlots());
    }

    @Test
    void testRemoveItem() {
        Item item = new Ingredient("Test Item", "Description", 10, 1);
        inventory.addItem(item);
        boolean removed = inventory.removeItem(item);

        assertTrue(removed);
        assertEquals(0, inventory.getUsedSlots());
    }

    @Test
    void testRemoveItem_NotPresent() {
        Item item = new Ingredient("Test Item", "Description", 10, 1);
        boolean removed = inventory.removeItem(item);

        assertFalse(removed);
    }

    @Test
    void testClear() {
        inventory.addItem(new Ingredient("Item 1", "Desc", 10, 1));
        inventory.addItem(new Ingredient("Item 2", "Desc", 10, 1));
        inventory.clear();

        assertEquals(0, inventory.getUsedSlots());
        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    void testIncreaseCapacity() {
        inventory.increaseCapacity(5);
        assertEquals(15, inventory.getMaxSlots());
        assertEquals(15, inventory.getAvailableSlots());
    }

    @Test
    void testContainsItem() {
        assertFalse(inventory.containsItem("Test Item"));
        inventory.addItem(new Ingredient("Test Item", "Desc", 10, 1));
        assertTrue(inventory.containsItem("Test Item"));
    }
}