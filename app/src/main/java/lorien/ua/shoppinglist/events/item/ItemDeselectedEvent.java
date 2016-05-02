package lorien.ua.shoppinglist.events.item;

/**
 * Created by Elf on 26.04.2016.
 * List item deselect event
 */
public class ItemDeselectedEvent {
    private int position = -1;

    public ItemDeselectedEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
