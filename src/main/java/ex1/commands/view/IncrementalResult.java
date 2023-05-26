package ex1.commands.view;

import ex1.model.Report;

public class IncrementalResult implements ViewCommand{
    private final Report midReport;

    public IncrementalResult(Report midReport){
        this.midReport = midReport;
    }

    public Report getMidReport() {
        return this.midReport;
    }
}
