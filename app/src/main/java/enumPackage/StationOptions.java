package enumPackage;



/**
 * Created by lubron on 7/18/15.
 */
public enum StationOptions {
    BackBay,
    Copley;


    public static String val(int x) {
        switch(x) {
            case 0:
                return enumPackage.StationOptions.Copley.name();
            case 1:
                return enumPackage.StationOptions.BackBay.name();
        }
        return enumPackage.StationOptions.BackBay.name();
    }
}