package ex1.commands.view;

import ex1.model.Report;

public class FinalResult implements ViewCommand{
    private final Report finalReport;

    public FinalResult(Report finalReport){
        this.finalReport = finalReport;
    }

    public Report getFinalReport() {
        return this.finalReport;
    }
}
