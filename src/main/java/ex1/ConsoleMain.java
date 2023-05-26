package ex1;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import ex1.actors.RootActor;

public class ConsoleMain {

    public static Behavior<Void> create(){
        return Behaviors.setup(
                context -> {
                    //context.spawn(RootActor.create(RootActor.ViewType.CONSOLE), "rootActor");
                    context.getLog().info("Log prova");
                    return Behaviors.receive(Void.class)
                            .onSignal(Terminated.class, sig -> Behaviors.stopped())
                            .build();
                });
    }

    public static void main(String[] args){
        ActorSystem.create(ConsoleMain.create(), "SourceAnalyzer");
    }
}
