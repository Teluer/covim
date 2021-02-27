import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class DayStats {

    private final double riskToContract;
    private final int infections;

    private int cumulativeFutureInfections = 0;

    public void addCumulativeFutureInfections(int infections) {
        cumulativeFutureInfections += infections;
    }

    public double getStupidityConsequences() {
        return infections == 0 ? 0 : cumulativeFutureInfections * riskToContract * Constants.IFR / infections;
    }

    public double getPossibleDeaths() {
        return cumulativeFutureInfections * Constants.IFR / infections;
    }
}
