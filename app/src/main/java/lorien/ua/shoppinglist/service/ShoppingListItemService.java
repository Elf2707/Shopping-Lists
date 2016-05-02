package lorien.ua.shoppinglist.service;

import java.util.List;

import ua.lorien.shoppinglist.model.dao.ShoppingListItem;

/**
 * Created by Elf on 25.03.2016.
 * Shopping List Item Service interface
 */
public interface ShoppingListItemService {
    ShoppingListItem add(ShoppingListItem item);

    void delete(Long id);

    ShoppingListItem update(ShoppingListItem item);

    ShoppingListItem findByName(String name);

    ShoppingListItem findById(Long id);

    List<ShoppingListItem> findAll();

    List<ShoppingListItem> findAllByListId(Long id);
}
