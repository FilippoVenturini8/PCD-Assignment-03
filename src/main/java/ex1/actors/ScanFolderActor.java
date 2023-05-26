package ex1.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.commands.documentanalyzer.CountLines;
import ex1.commands.documentanalyzer.DocumentAnalyzerCommand;
import ex1.commands.result.ComputationTerminated;
import ex1.commands.root.RootCommand;
import ex1.commands.root.Start;
import ex1.commands.scanfolder.ChildTerminated;
import ex1.commands.scanfolder.Scan;
import ex1.commands.scanfolder.ScanFolderCommand;
import ex1.model.Document;
import ex1.model.Folder;

public class ScanFolderActor extends AbstractBehavior<ScanFolderCommand> {
    private int numberOfChildAlive = 0;
    private boolean allChildStarted = false;
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

        return builder.build();
    }

    private Behavior<ScanFolderCommand> onScan(Scan scan) {
        Folder folder = scan.getStartDirectory();

        for(Folder subfolder : folder.getSubFolders()){
            ActorRef<ScanFolderCommand> scanFolderActor = getContext().spawn(ScanFolderActor.create(), "ScanFolderActor - " + subfolder.getPath());
            scanFolderActor.tell(new Scan(subfolder.getPath()));
            this.numberOfChildAlive++;
        }

        for(Document document : folder.getDocuments()){
            ActorRef<DocumentAnalyzerCommand> documentAnalyzerActor = getContext().spawn(DocumentAnalyzerActor.create(), "DocumentAnalyzerActor - " + document.getPath());
            documentAnalyzerActor.tell(new CountLines(document.getPath()));
            this.numberOfChildAlive++;
        }

        this.allChildStarted = true;

        return this;
    }

    private Behavior<ScanFolderCommand> onChildTerminated(ChildTerminated childTerminated) {
        this.numberOfChildAlive--;

        if(this.allChildStarted && this.numberOfChildAlive == 0){
            getContext().classicActorContext().parent().tell(
                    new ChildTerminated(),
                    getContext().classicActorContext().self());
        }

        return this;
    }
}
