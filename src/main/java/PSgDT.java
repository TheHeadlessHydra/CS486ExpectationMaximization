/**
 * Created by Serj on 24/11/2015.
 */
public class PSgDT {
    private double D0TtSt = .01;
    private double D1TtSt = .01;
    private double D2TtSt = .01;

    private double D0TfSt = .05;
    private double D1TfSt = .8;
    private double D2TfSt = .8;

    public double getD0TtSt() {
        return D0TtSt;
    }

    public void setD0TtSt(double d0TtSt) {
        D0TtSt = d0TtSt;
    }

    public double getD1TtSt() {
        return D1TtSt;
    }

    public void setD1TtSt(double d1TtSt) {
        D1TtSt = d1TtSt;
    }

    public double getD2TtSt() {
        return D2TtSt;
    }

    public void setD2TtSt(double d2TtSt) {
        D2TtSt = d2TtSt;
    }

    public double getD0TfSt() {
        return D0TfSt;
    }

    public void setD0TfSt(double d0TfSt) {
        D0TfSt = d0TfSt;
    }

    public double getD1TfSt() {
        return D1TfSt;
    }

    public void setD1TfSt(double d1TfSt) {
        D1TfSt = d1TfSt;
    }

    public double getD2TfSt() {
        return D2TfSt;
    }

    public void setD2TfSt(double d2TfSt) {
        D2TfSt = d2TfSt;
    }

    public double getD0TtSf() {
        return (1d - D0TtSt);
    }

    public double getD1TtSf() {
        return (1d - D1TtSt);
    }

    public double getD2TtSf() {
        return (1d - D2TtSt);
    }

    public double getD0TfSf() {
        return (1d - D0TfSt);
    }

    public double getD1TfSf() {
        return (1d - D1TfSt);
    }

    public double getD2TfSf() {
        return (1d - D2TfSt);
    }
}
