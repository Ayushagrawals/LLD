+----------------------+
|  InventoryManager   |
+----------------------+
| - allItems: List<ItemDetails>  |
| - inventoryByCategory: Map<ProductCategory, List<ItemDetails>> |
+----------------------+
| + getItemsByCategory(category: ProductCategory, limit: int): List<ItemDetails> |
| + updateItemStock(itemDetail: ItemDetails, newStock: int): void |
| + findItemsByCompany(company: Company): List<ItemDetails> |
| + getLowStockItems(threshold: int): List<ItemDetails> |
+----------------------+
           |
           | Calls `getItemsByCategory()`
           v
+--------+                  +----------------+
| Block  |----------------->| ItemDetails    |
+--------+                  +----------------+
      |                         ▲
      | Calls `getItemsByCategory()` for filling itself
      v
+--------+
| Company|
+--------+
