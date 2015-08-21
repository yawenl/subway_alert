package enumPackage;

public enum GreenOptions {
    Copley, Hynes, Kenmore;


    public static String val(int x) {
        switch(x) {
            case 0:
                return "Copley";
            case 1:
                return "Hynes";
            case 2:
                return "Kenmore";
        }
        return enumPackage.GreenOptions.Copley.name();
    }

    public static String[] getStringArray() {
        String[] array = new String[] {"Copley", "Hynes", "Kenmore"};
        return array;
    }
}