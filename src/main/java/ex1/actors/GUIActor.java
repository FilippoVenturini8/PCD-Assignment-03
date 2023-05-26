package ex1.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import ex1.commands.root.Start;
import ex1.commands.root.Stop;
import ex1.commands.view.FinalResult;
import ex1.commands.view.IncrementalResult;
import ex1.commands.view.ViewCommand;
import ex1.utils.AnalyzedFile;
import ex1.utils.SetupInfo;
import ex1.utils.Toast;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class GUIActor extends AbstractBehavior<ViewCommand> {

    public static Behavior<ViewCommand> create(){
        return Behaviors.setup(GUIActor::new);
    }

    @Override
    public Receive<ViewCommand> createReceive() {
        ReceiveBuilder<ViewCommand> builder = newReceiveBuilder();

        builder.onMessage(FinalResult.class, this::onFinalResult);
        builder.onMessage(IncrementalResult.class, this::onIncrementalResult);

        return builder.build();
    }

    private Behavior<ViewCommand> onIncrementalResult(IncrementalResult incrementalResult) {
        DefaultListModel<AnalyzedFile> rankingModel = new DefaultListModel<>();
        rankingModel.addAll(incrementalResult.getMidReport().ranking());

        SwingUtilities.invokeLater(() -> rankingList.setModel(rankingModel));

        DefaultListModel<String> distributionModel = new DefaultListModel<>();
        distributionModel.addAll(incrementalResult.getMidReport().distribution().entrySet().stream()
                .map(e -> e.getKey() + " : " + e.getValue()).
                collect(Collectors.toList()));

        SwingUtilities.invokeLater(() -> distributionList.setModel(distributionModel));
        return this;
    }

    private Behavior<ViewCommand> onFinalResult(FinalResult finalResult) {
        this.btnStart.setEnabled(true);
        this.btnStop.setEnabled(false);

        getContext().classicActorContext().parent().tell(
                new Stop(),
                getContext().classicActorContext().self());

        return this;
    }

    private final JFrame frame = new JFrame();
    private final JList<AnalyzedFile> rankingList = new JList<>();
    private final JList<String> distributionList = new JList<>();
    private final JButton btnStart = new JButton("Start");
    private final JButton btnStop = new JButton("Stop");

    private GUIActor(ActorContext<ViewCommand> context){
        super(context);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        this.frame.setTitle("Source Tracker");
        this.frame.setSize(800, 500);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);

        final JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        final JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        final JLabel lblDirectory = new JLabel("Start directory: ");
        final JTextField txtDirectory = new JTextField(20);
        txtDirectory.setMinimumSize(txtDirectory.getPreferredSize());

        final JLabel lblNFiles = new JLabel("Number of files: ");
        final JTextField txtNFiles = new JTextField(3);
        txtDirectory.setMinimumSize(txtDirectory.getPreferredSize());

        final JLabel lblIntervals = new JLabel("Number of intervals: ");
        final JTextField txtIntervals = new JTextField(3);
        txtDirectory.setMinimumSize(txtDirectory.getPreferredSize());

        final JLabel lblLastInterval = new JLabel("Last interval: ");
        final JTextField txtLastInterval = new JTextField(3);
        txtDirectory.setMinimumSize(txtDirectory.getPreferredSize());

        btnStop.setEnabled(false);

        btnStart.addActionListener(e -> {
            if(txtDirectory.getText().isEmpty()){
                Toast.makeToast(frame, "Insert path of initial directory.", new Color(255,0,0,170), 3);
                return;
            }
            if(txtNFiles.getText().isEmpty() || !utils.Strings.isNumeric(txtNFiles.getText()) || Integer.parseInt(txtNFiles.getText()) <= 0){
                Toast.makeToast(frame, "Insert correct number of files.", new Color(255,0,0,170), 3);
                return;
            }
            if(txtIntervals.getText().isEmpty() || !utils.Strings.isNumeric(txtIntervals.getText()) || Integer.parseInt(txtNFiles.getText()) <= 0){
                Toast.makeToast(frame, "Insert correct number of intervals.", new Color(255,0,0,170), 3);
                return;
            }
            if(txtLastInterval.getText().isEmpty() || !utils.Strings.isNumeric(txtLastInterval.getText()) || Integer.parseInt(txtNFiles.getText()) <= 0){
                Toast.makeToast(frame, "Insert correct last interval value.", new Color(255,0,0,170), 3);
                return;
            }

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);

            this.rankingList.setModel(new DefaultListModel<>());
            this.distributionList.setModel(new DefaultListModel<>());

            SetupInfo setupInfo = new SetupInfo(
                    txtDirectory.getText(),
                    Integer.parseInt(txtNFiles.getText()),
                    Integer.parseInt(txtIntervals.getText()),
                    Integer.parseInt(txtLastInterval.getText()));

            getContext().classicActorContext().parent().tell(
                    new Start(setupInfo),
                    getContext().classicActorContext().self());

        });

        btnStop.addActionListener(e -> {
            getContext().classicActorContext().parent().tell(
                    new Stop(),
                    getContext().classicActorContext().self());

            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        });

        final JPanel resultsPanel = new JPanel();

        this.rankingList.setSize(100, 50);
        this.distributionList.setSize(100, 50);

        inputPanel.add(lblDirectory);
        inputPanel.add(txtDirectory);
        inputPanel.add(lblNFiles);
        inputPanel.add(txtNFiles);
        inputPanel.add(lblIntervals);
        inputPanel.add(txtIntervals);
        inputPanel.add(lblLastInterval);
        inputPanel.add(txtLastInterval);

        controlPanel.add(btnStart);
        controlPanel.add(btnStop);

        resultsPanel.add(new JScrollPane(rankingList));
        resultsPanel.add(new JScrollPane(distributionList));

        mainPanel.add(inputPanel);
        mainPanel.add(controlPanel);
        mainPanel.add(resultsPanel);

        this.frame.setContentPane(mainPanel);
        this.frame.setVisible(true);
    }
}
