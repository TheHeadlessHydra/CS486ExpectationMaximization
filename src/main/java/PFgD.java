/**
 * Created by Serj on 24/11/2015.
 */
public class PFgD {
    private double D0Ft = .05;
    private double D1Ft = .02;
    private double D2Ft = .08;

    public double getD0Ft() {
        return D0Ft;
    }

    public void setD0Ft(double d0Ft) {
        D0Ft = d0Ft;
    }

    public double getD1Ft() {
        return D1Ft;
    }

    public void setD1Ft(double d1Ft) {
        D1Ft = d1Ft;
    }

    public double getD2Ft() {
        return D2Ft;
    }

    public void setD2Ft(double d2Ft) {
        D2Ft = d2Ft;
    }

    public double getD0Ff() {
        return (1d - D0Ft);
    }

    public double getD1Ff() {
        return (1d - D1Ft);
    }

    public double getD2Ff() {
        return (1d - D2Ft);
    }
}
