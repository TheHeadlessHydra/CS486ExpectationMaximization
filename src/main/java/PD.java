/**
 * Created by Serj on 24/11/2015.
 */
public class PD {
    private double d0 = 0.5;
    private double d1 = 0.25;
    private double d2 = 0.25;

    public double getD0() {
        return d0;
    }

    public void setD0(double d0) {
        this.d0 = d0;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public double getD2() {
        return d2;
    }

    public void setD2(double d2) {
        this.d2 = d2;
    }

    @Override
    public String toString() {
        return "PD{" +
                "d0=" + d0 +
                ", d1=" + d1 +
                ", d2=" + d2 +
                '}';
    }
}
