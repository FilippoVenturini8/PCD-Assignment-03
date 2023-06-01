package ex2.messages;

public class ColorChangedMessage extends Message{
    private int newColor;

    public ColorChangedMessage(){}
    public ColorChangedMessage(String id, int newColor){
        super(id);
        this.newColor = newColor;
    }

    public int getNewColor() {
        return newColor;
    }

    public void setNewColor(int newColor) {
        this.newColor = newColor;
    }
}
