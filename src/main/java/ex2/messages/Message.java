package ex2.messages;

public class Message {
    private String id;
    public Message(){}
    public Message(String address){
        this.id = address;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
}
