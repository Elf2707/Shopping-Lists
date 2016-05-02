package lorien.ua.shoppinglist.events.list;

/**
 * Created by Elf on 19.04.2016.
 * delete list event
 */
public class ListDeleteEvent {
    private int position;

    public ListDeleteEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
