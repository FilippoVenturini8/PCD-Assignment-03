package ex1.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import ex1.commands.view.ViewCommand;
import ex1.view.ConsoleActor;

public class GUIActor extends AbstractBehavior<ViewCommand> {
    private GUIActor(ActorContext<ViewCommand> context) {
        super(context);
    }

    public static Behavior<ViewCommand> create(){
        return Behaviors.setup(GUIActor::new);
    }

    @Override
    public Receive<ViewCommand> createReceive() {
        return null;
    }
}
