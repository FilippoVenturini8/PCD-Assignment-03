package utils;

public class Strings {

    private Strings(){};

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
}
