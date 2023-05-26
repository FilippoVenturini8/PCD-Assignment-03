package ex1.actors;

import akka.actor.ActorSelection;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.commands.documentanalyzer.CountLines;
import ex1.commands.documentanalyzer.DocumentAnalyzerCommand;
import ex1.commands.result.AddNewResult;
import ex1.commands.result.ResultCommand;
import ex1.commands.root.RootCommand;
import ex1.commands.root.Start;
import ex1.commands.scanfolder.ChildTerminated;
import ex1.model.AnalyzedFile;
import ex1.model.Document;

public class DocumentAnalyzerActor extends AbstractBehavior<DocumentAnalyzerCommand> {
    private DocumentAnalyzerActor(ActorContext<DocumentAnalyzerCommand> context) {
        super(context);
    }

    public static Behavior<DocumentAnalyzerCommand> create(){
        return Behaviors.setup(DocumentAnalyzerActor::new);
    }

    @Override
    public Receive<DocumentAnalyzerCommand> createReceive() {
        ReceiveBuilder<DocumentAnalyzerCommand> builder = newReceiveBuilder();

        builder.onMessage(CountLines.class, this::onCountLines);

        return builder.build();
    }

    private Behavior<DocumentAnalyzerCommand> onCountLines(CountLines countLines) {
        AnalyzedFile newResult = new AnalyzedFile(countLines.getDocument().getPath(), countLines.getDocument().countLines());

        getContext().classicActorContext()
                .actorSelection(getContext().getSystem().path()+"/rootActor/resultActor")
                .tell(new AddNewResult(newResult), getContext().classicActorContext().self());

        getContext().classicActorContext().parent().tell(
                new ChildTerminated(),
                getContext().classicActorContext().self());
        return this;
    }
}
