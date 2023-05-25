package ex1.commands.result;

import akka.actor.typed.ActorRef;
import ex1.commands.view.ViewCommand;

public class Subscribe implements ResultCommand{
    private final ActorRef<ViewCommand> subscriber;

    public Subscribe(ActorRef<ViewCommand> subscriber){
        this.subscriber = subscriber;
    }

    public ActorRef<ViewCommand> getSubscriber(){
        return this.subscriber;
    }
}
