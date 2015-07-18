package enumPackage;

/**
 * Created by lubron on 7/18/15.
 */
public enum DirectionOptions {
    Inbound,Outbound;

    public static String val(int x) {
        switch(x) {
            case 0:
                return enumPackage.DirectionOptions.Inbound.name();
            case 1:
                return enumPackage.DirectionOptions.Outbound.name();
        }
        return enumPackage.DirectionOptions.Inbound.name();
    }
}
