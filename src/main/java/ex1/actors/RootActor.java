package ex1.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.commands.result.ResultCommand;
import ex1.commands.result.Subscribe;
import ex1.commands.root.RootCommand;
import ex1.commands.root.Start;
import ex1.commands.view.ViewCommand;
import ex1.view.ConsoleActor;

public class RootActor extends AbstractBehavior<RootCommand> {
    public enum ViewType{
        CONSOLE, GUI;
    }

    private ActorRef<ViewCommand> viewActor;
    private RootActor(ActorContext<RootCommand> context, ViewType viewType) {
        super(context);
        if(viewType == ViewType.CONSOLE){
            viewActor = context.spawn(ConsoleActor.create(), "consoleActor");
        }else{
            viewActor = context.spawn(GUIActor.create(), "GUIActor");
        }
    }

    public static Behavior<RootCommand> create(ViewType viewType){
        return Behaviors.setup(context -> new RootActor(context, viewType));
    }

    @Override
    public Receive<RootCommand> createReceive() {
        ReceiveBuilder<RootCommand> builder = newReceiveBuilder();

        builder.onMessage(Start.class, this::onStart);

        return builder.build();
    }

    private Behavior<RootCommand> onStart(Start startCommand) {
        ActorRef<ResultCommand> resultActor = getContext().spawn(ResultActor.create(startCommand.getSetupInfo()), "resultActor");
        resultActor.tell(new Subscribe(this.viewActor));
        return this;
    }
}
