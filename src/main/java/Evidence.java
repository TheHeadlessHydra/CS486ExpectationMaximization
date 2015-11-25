/**
 * Created by Serj on 23/11/2015.
 */
public class Evidence {
    private final boolean sloepnea;
    private final boolean foriennditis;
    private final boolean degarSpots;
    private final boolean trimonoHTS;
    private final int dunettes;

    public Evidence(boolean sloepnea, boolean foriennditis, boolean degarSpots, boolean trimonoHTS, int dunettes) {
        this.sloepnea = sloepnea;
        this.foriennditis = foriennditis;
        this.degarSpots = degarSpots;
        this.trimonoHTS = trimonoHTS;
        this.dunettes = dunettes;
    }

    public boolean isSloepnea() {
        return sloepnea;
    }

    public boolean isForiennditis() {
        return foriennditis;
    }

    public boolean isDegarSpots() {
        return degarSpots;
    }

    public boolean isTrimonoHTS() {
        return trimonoHTS;
    }

    public int getDunettes() {
        return dunettes;
    }

    @Override
    public String toString() {
        return "Evidence{" +
                "sloepnea=" + sloepnea +
                ", foriennditis=" + foriennditis +
                ", degarSpots=" + degarSpots +
                ", trimonoHTS=" + trimonoHTS +
                ", dunettes=" + dunettes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Evidence evidence = (Evidence) o;

        if (sloepnea != evidence.sloepnea) return false;
        if (foriennditis != evidence.foriennditis) return false;
        if (degarSpots != evidence.degarSpots) return false;
        if (trimonoHTS != evidence.trimonoHTS) return false;
        return dunettes == evidence.dunettes;

    }

    @Override
    public int hashCode() {
        int result = (sloepnea ? 1 : 0);
        result = 31 * result + (foriennditis ? 1 : 0);
        result = 31 * result + (degarSpots ? 1 : 0);
        result = 31 * result + (trimonoHTS ? 1 : 0);
        result = 31 * result + dunettes;
        return result;
    }
}
