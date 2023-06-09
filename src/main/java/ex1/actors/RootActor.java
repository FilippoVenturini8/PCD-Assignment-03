package ex1.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.commands.result.ComputationTerminated;
import ex1.commands.result.ResultCommand;
import ex1.commands.result.Subscribe;
import ex1.commands.root.RootCommand;
import ex1.commands.root.Start;
import ex1.commands.root.Stop;
import ex1.commands.scanfolder.ChildTerminated;
import ex1.commands.scanfolder.Scan;
import ex1.commands.scanfolder.ScanFolderCommand;
import ex1.commands.view.ViewCommand;

public class RootActor extends AbstractBehavior<RootCommand> {
    private ActorRef<ResultCommand> resultActor;
    private ActorRef<ScanFolderCommand> rootScanFolderActor;
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
        builder.onMessage(Stop.class, this::onStop);
        builder.onMessage(ChildTerminated.class, this::onChildTerminated);

        return builder.build();
    }

    private Behavior<RootCommand> onStop(Stop stop) {
        getContext().stop(this.resultActor);
        this.rootScanFolderActor.tell(new Stop());
        getContext().stop(this.rootScanFolderActor);
        return this;
    }

    private Behavior<RootCommand> onStart(Start startCommand) {
        rootScanFolderActor = getContext().spawn(ScanFolderActor.create(), "rootScanFolderActor");
        rootScanFolderActor.tell(new Scan(startCommand.getSetupInfo().directory()));

        this.resultActor = getContext().spawn(ResultActor.create(startCommand.getSetupInfo()), "resultActor");
        resultActor.tell(new Subscribe(this.viewActor));
        return this;
    }

    private Behavior<RootCommand> onChildTerminated(ChildTerminated childTerminated) {
        this.resultActor.tell(new ComputationTerminated());
        return this;
    }
}
