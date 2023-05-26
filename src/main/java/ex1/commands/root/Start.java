package ex1.commands.root;

import ex1.utils.SetupInfo;

public class Start implements RootCommand{
    private final SetupInfo setupInfo;

    public Start(SetupInfo setupInfo){
        this.setupInfo = setupInfo;
    }

    public SetupInfo getSetupInfo() {
        return this.setupInfo;
    }
}
