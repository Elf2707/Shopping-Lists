package lorien.ua.shoppinglist.events.common;

/**
 * Created by Elf on 02.05.2016.
 * Correct element position after collapse expand
 */
public class CorrectPositionEvent {
    private int position = -1;

    public CorrectPositionEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
