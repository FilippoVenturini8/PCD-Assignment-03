package ex1.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.commands.result.AddNewResult;
import ex1.commands.result.ComputationTerminated;
import ex1.commands.result.ResultCommand;
import ex1.commands.result.Subscribe;
import ex1.commands.root.RootCommand;
import ex1.commands.view.FinalResult;
import ex1.commands.view.IncrementalResult;
import ex1.commands.view.ViewCommand;
import ex1.model.AnalyzedFile;
import ex1.model.Interval;
import ex1.model.Report;
import ex1.model.SetupInfo;

import java.util.*;
import java.util.stream.Collectors;

public class ResultActor extends AbstractBehavior<ResultCommand> {
    private final List<ActorRef<ViewCommand>> subscribers = new ArrayList<>();
    private SetupInfo setupInfo;

    private final Set<AnalyzedFile> ranking = new TreeSet<>();
    private final Map<Interval, Integer> distribution = new TreeMap<>();

    private ResultActor(ActorContext<ResultCommand> context, SetupInfo setupInfo) {
        super(context);
        this.setupInfo = setupInfo;

        if(this.setupInfo.nIntervals() == 1){
            distribution.put(new Interval(0, Integer.MAX_VALUE), 0);
        }else {
            final int intervalSize = this.setupInfo.lastIntervalLowerBound() / (this.setupInfo.nIntervals() - 1);
            for (int i = 0; i < this.setupInfo.nIntervals() - 2; i++) {
                distribution.put(new Interval(intervalSize * i, intervalSize * (i + 1)), 0);
            }
            distribution.put(new Interval(intervalSize * (this.setupInfo.nIntervals() - 2), this.setupInfo.lastIntervalLowerBound()), 0);
            distribution.put(new Interval(this.setupInfo.lastIntervalLowerBound(), Integer.MAX_VALUE), 0);
        }
    }

    public static Behavior<ResultCommand> create(SetupInfo setupInfo){
        return Behaviors.setup(context -> new ResultActor(context, setupInfo));
    }

    @Override
    public Receive<ResultCommand> createReceive() {
        ReceiveBuilder<ResultCommand> builder = newReceiveBuilder();

        builder.onMessage(Subscribe.class, this::onSubscribe);
        builder.onMessage(AddNewResult.class, this::addNewResult);
        builder.onMessage(ComputationTerminated.class, this::computationTerminated);

        return builder.build();
    }

    private Behavior<ResultCommand> onSubscribe(Subscribe subscribe) {
        this.subscribers.add(subscribe.getSubscriber());
        return this;
    }

    private Behavior<ResultCommand> addNewResult(AddNewResult addNewResult) {
        for(Map.Entry<Interval, Integer> entry : distribution.entrySet()){
            if(entry.getKey().contains(addNewResult.getNewResult().lines())){
                entry.setValue(entry.getValue() + 1);
            }
        }
        this.ranking.add(addNewResult.getNewResult());

        for(ActorRef<ViewCommand> subscribed : this.subscribers){
            List<AnalyzedFile> topNFiles = ranking.stream().limit(this.setupInfo.nFiles()).collect(Collectors.toList());
            subscribed.tell(new IncrementalResult(new Report(topNFiles, this.distribution)));
        }

        return this;
    }

    private Behavior<ResultCommand> computationTerminated(ComputationTerminated computationTerminated) {
        for(ActorRef<ViewCommand> subscribed : this.subscribers){
            List<AnalyzedFile> topNFiles = ranking.stream().limit(this.setupInfo.nFiles()).collect(Collectors.toList());
            subscribed.tell(new FinalResult(new Report(topNFiles, this.distribution)));
        }
        return this;
    }


}
