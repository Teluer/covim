import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class City {
    private final int cityPopulation;

    private int immune = 0;
    private int dead = 0;

    public int getHealthyCount() {
        return cityPopulation - immune - dead;
    }

    public int infect(int rawInfectionsAmount, double ifr) {
        int realInfections = rawInfectionsAmount;
        realInfections *= 1 - immune * 1.0 / cityPopulation;
        realInfections = Math.min(realInfections, getHealthyCount());

        int survived = (int) Math.round(realInfections * (1 - ifr));
        addImmune(survived);
        addDead(realInfections - survived);

        return realInfections;
    }

    private void addImmune(int immune) {
        this.immune += Math.round(immune * Constants.LASTING_IMMUNITY_CHANCE);
    }

    private void addDead(int dead) {
        this.dead += dead;
    }
}
