public class Wrapper {
    private Object current;
    private Wrapper previous;
    private Wrapper next;

    Wrapper(Wrapper previous, Object current, Wrapper next) {
        this.previous = previous;
        this.current = current;
        this.next = next;
    }

    Wrapper getNext() {
        return next;
    }

    void setNext(Wrapper next) {
        this.next = next;
    }

    Wrapper getPrevious() {
        return previous;
    }

    void setPrevious(Wrapper previous) {
        this.previous = previous;
    }

    Object getCurrent() {
        return current;
    }

    void setCurrent(Object current) {
        this.current = current;
    }
}
