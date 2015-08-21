package enumPackage;

public enum OrangeOptions {
    RoxburyCrossing, Ruggles, MassAve, BackBay, TuftsMedicalCente, Chinatown;

    public static String val(int x) {
        switch(x) {
            case 0:
                return "Roxbury Crossing";
            case 1:
                return "Ruggles";
            case 2:
                return "Mass. Ave.";
            case 3:
                return "Back Bay";
            case 4:
                return "Tufts Medical Center";
            case 5:
                return "中國城";
        }
        return "Back Bay";
    }

    public static String[] getStringArray() {
        String[] array = new String[] {"Roxbury Crossing", "Ruggles", "Mass. Ave.", "Back Bay", "Tufts Medical Center", "中國城"};
        return array;
    }
}