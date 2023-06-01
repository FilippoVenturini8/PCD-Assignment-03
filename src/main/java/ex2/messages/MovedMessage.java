package ex2.messages;

public class MovedMessage extends Message{
    private int x;
    private int y;

    public MovedMessage(){}
    public MovedMessage(String id, int x, int y){
        super(id);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
