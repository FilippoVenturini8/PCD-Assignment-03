package ex1.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.commands.result.AddNewResult;
import ex1.commands.result.ResultCommand;
import ex1.commands.result.Subscribe;
import ex1.commands.root.RootCommand;
import ex1.commands.view.ViewCommand;
import ex1.model.SetupInfo;

import java.util.ArrayList;
import java.util.List;

public class ResultActor extends AbstractBehavior<ResultCommand> {
    private final List<ActorRef<ViewCommand>> subscribers = new ArrayList<>();
    private SetupInfo setupInfo;

    private ResultActor(ActorContext<ResultCommand> context, SetupInfo setupInfo) {
        super(context);
        this.setupInfo = setupInfo;
    }

    public static Behavior<ResultCommand> create(SetupInfo setupInfo){
        return Behaviors.setup(context -> new ResultActor(context, setupInfo));
    }

    @Override
    public Receive<ResultCommand> createReceive() {
        ReceiveBuilder<ResultCommand> builder = newReceiveBuilder();

        builder.onMessage(Subscribe.class, this::onSubscribe);
        //builder.onMessage(AddNewResult.class, this::addNewResult);


        return builder.build();
    }


    private Behavior<ResultCommand> onSubscribe(Subscribe subscribe) {
        this.subscribers.add(subscribe.getSubscriber());
        return this;
    }


}
