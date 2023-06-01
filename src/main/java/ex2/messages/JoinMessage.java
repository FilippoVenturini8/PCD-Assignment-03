package ex2.messages;

public class JoinMessage extends Message{
    private int color;
    public JoinMessage(){}
    public JoinMessage(String id, int color) {
        super(id);
        this.color = color;
    }
    public int getColor(){
        return this.color;
    }
    public void setColor(int color){
        this.color = color;
    }
}
