package ex1.view;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.actors.RootActor;
import ex1.commands.root.RootCommand;
import ex1.commands.view.ViewCommand;

public class ConsoleActor extends AbstractBehavior<ViewCommand> {

    private ConsoleActor(ActorContext<ViewCommand> context) {
        super(context);
    }

    public static Behavior<ViewCommand> create(){
        return Behaviors.setup(ConsoleActor::new);
    }

    @Override
    public Receive<ViewCommand> createReceive() {
        ReceiveBuilder<ViewCommand> builder = newReceiveBuilder();

        return builder.build();
    }
}
