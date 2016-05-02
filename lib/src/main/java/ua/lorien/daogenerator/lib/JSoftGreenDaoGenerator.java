package ua.lorien.daogenerator.lib;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class JSoftGreenDaoGenerator {
    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "ua.lorien.shoppinglist.model.dao");

        //Shopping list
        Entity shoppingList = schema.addEntity("ShoppingList");
        shoppingList.addIdProperty().primaryKey();
        shoppingList.addStringProperty("name");
        shoppingList.addBooleanProperty("isDone");
        shoppingList.setHasKeepSections(true);
        shoppingList.implementsInterface("ParentListItem");
        shoppingList.implementsInterface("Serializable");

        Entity shoppingListItem = schema.addEntity("ShoppingListItem");
        shoppingListItem.addIdProperty().primaryKey();
        shoppingListItem.addStringProperty("name");
        shoppingListItem.addStringProperty("description");
        shoppingListItem.addBooleanProperty("isDone");
        shoppingListItem.addStringProperty("amount");
        shoppingListItem.addDoubleProperty("price");
        shoppingListItem.setHasKeepSections(true);
        shoppingListItem.implementsInterface("Serializable");

        Property shoppingListId = shoppingListItem.addLongProperty("shoppingListId").getProperty();
        shoppingListItem.addToOne(shoppingList, shoppingListId);

        ToMany shoppingLitToItems = shoppingList.addToMany(shoppingListItem, shoppingListId);
        shoppingLitToItems.setName("items");

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
