/**
 * Created by Serj on 24/11/2015.
 */
public class PT {
    private double Tt = 0.1;

    public double getTt() {
        return Tt;
    }

    public void setTt(double tt) {
        Tt = tt;
    }

    public double getTf() {
        return (1- Tt);
    }
}
