/**
 * Created by Serj on 24/11/2015.
 */
public class PDSgD {
    private double D0DSt = .05d;
    private double D1DSt = .8d;
    private double D2DSt = .2d;

    public double getD0DSt() {
        return D0DSt;
    }

    public void setD0DSt(double d0DSt) {
        D0DSt = d0DSt;
    }

    public double getD1DSt() {
        return D1DSt;
    }

    public void setD1DSt(double d1DSt) {
        D1DSt = d1DSt;
    }

    public double getD2DSt() {
        return D2DSt;
    }

    public void setD2DSt(double d2DSt) {
        D2DSt = d2DSt;
    }

    public double getD0DSf() {
        return (1d - D0DSt);
    }

    public double getD1DSf() {
        return (1d - D1DSt);
    }

    public double getD2DSf() {
        return (1d - D2DSt);
    }

    @Override
    public String toString() {
        return "PDSgD{" +
                "D0DSt=" + D0DSt +
                ", D1DSt=" + D1DSt +
                ", D2DSt=" + D2DSt +
                '}';
    }
}
