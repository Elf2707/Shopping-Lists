package lorien.ua.shoppinglist.service;


import java.util.List;

import ua.lorien.shoppinglist.model.dao.ShoppingList;

/**
 * Created by Elf on 25.03.2016.
 */
public interface ShoppingListService {
    ShoppingList add(ShoppingList shoppingList);

    void delete(Long id);

    ShoppingList update(ShoppingList shoppingList);

    ShoppingList findByName(String name);

    ShoppingList findById(Long id);

    List<ShoppingList> findAll();
}
