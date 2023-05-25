package ex1.commands.result;

import ex1.model.AnalyzedFile;

public class AddNewResult implements ResultCommand{
    private final AnalyzedFile newResult;

    public AddNewResult(AnalyzedFile newResult){
        this.newResult = newResult;
    }

    public AnalyzedFile getNewResult(){
        return this.newResult;
    }
}
