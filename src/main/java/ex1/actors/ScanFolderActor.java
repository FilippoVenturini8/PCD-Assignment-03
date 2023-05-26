package ex1.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.commands.documentanalyzer.CountLines;
import ex1.commands.documentanalyzer.DocumentAnalyzerCommand;
import ex1.commands.root.RootCommand;
import ex1.commands.root.Stop;
import ex1.commands.scanfolder.ChildTerminated;
import ex1.commands.scanfolder.Scan;
import ex1.commands.scanfolder.ScanFolderCommand;
import ex1.utils.Document;
import ex1.utils.Folder;

import java.util.LinkedList;
import java.util.List;

public class ScanFolderActor extends AbstractBehavior<ScanFolderCommand> {
    private int numberOfChildAlive = 0;
    private boolean allChildStarted = false;
    private List<ActorRef<ScanFolderCommand>> scanFolderActors = new LinkedList<>();
    private List<ActorRef<DocumentAnalyzerCommand>> documentAnalyzerActors = new LinkedList<>();
    private ScanFolderActor(ActorContext<ScanFolderCommand> context) {
        super(context);
    }

    public static Behavior<ScanFolderCommand> create(){
        return Behaviors.setup(ScanFolderActor::new);
    }

    @Override
    public Receive<ScanFolderCommand> createReceive() {
        ReceiveBuilder<ScanFolderCommand> builder = newReceiveBuilder();

        builder.onMessage(Scan.class, this::onScan);
        builder.onMessage(ChildTerminated.class, this::onChildTerminated);
        builder.onMessage(Stop.class, this::onStop);

        return builder.build();
    }

    private Behavior<ScanFolderCommand> onStop(Stop stop) {
        this.scanFolderActors.forEach(a -> {
            a.tell(new Stop());
            getContext().stop(a);
        });

        this.documentAnalyzerActors.forEach(a -> {
            getContext().stop(a);
        });
        return this;
    }

    private Behavior<ScanFolderCommand> onScan(Scan scan) {
        Folder folder = scan.getStartDirectory();

        for(Folder subfolder : folder.getSubFolders()){
            ActorRef<ScanFolderCommand> scanFolderActor = getContext().spawn(ScanFolderActor.create(), subfolder.getName().replaceAll("[^A-Za-z0-9]", "_"));
            scanFolderActor.tell(new Scan(subfolder.getPath()));
            this.scanFolderActors.add(scanFolderActor);
            this.numberOfChildAlive++;
        }

        for(Document document : folder.getDocuments()){
            ActorRef<DocumentAnalyzerCommand> documentAnalyzerActor = getContext().spawn(DocumentAnalyzerActor.create(), document.getName().replaceAll("[^A-Za-z0-9]", "_"));
            documentAnalyzerActor.tell(new CountLines(document.getPath()));
            this.documentAnalyzerActors.add(documentAnalyzerActor);
            this.numberOfChildAlive++;
        }

        if(this.numberOfChildAlive == 0){
            this.terminate();
        }

        this.allChildStarted = true;

        return this;
    }

    private void terminate(){
        getContext().classicActorContext().parent().tell(
                new ChildTerminated(),
                getContext().classicActorContext().self());
    }

    private Behavior<ScanFolderCommand> onChildTerminated(ChildTerminated childTerminated) {
        this.numberOfChildAlive--;

        if(this.allChildStarted && this.numberOfChildAlive == 0){
            this.terminate();
        }

        return this;
    }
}
