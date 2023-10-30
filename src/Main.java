import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Observer Pattern
interface Observer {
    void update(String event);
}

class ArmyObserver implements Observer {
    private String name;

    public ArmyObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String event) {
        System.out.println(name + ": " + event);
    }
}

class BattleManager {
    private List<Observer> observers = new ArrayList<>();

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void notify(String event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }
}

class Army extends BattleManager {
    private String name;
    List<Soldier> soldiers = new ArrayList<>();

    public Army(String name) {
        this.name = name;
    }

    public void addSoldier(Soldier soldier) {
        soldiers.add(soldier);
    }

    public void attack(Army targetArmy) {
        String event = name + " is attacking " + targetArmy.name + "!";
        notify(event);
        for (Soldier attacker : soldiers) {
            if (targetArmy.soldiers.size() != 0) {
                Soldier target = getRandomTarget(targetArmy);
                performAction(attacker, targetArmy, target);
            }
        }
    }

    private Soldier getRandomTarget(Army targetArmy) {
        List<Soldier> possibleTargets = new ArrayList<>(targetArmy.soldiers);
        int randomIndex = new Random().nextInt(targetArmy.soldiers.size());
        return possibleTargets.get(randomIndex);
    }

    private void performAction(Soldier attacker, Army targetArmy, Soldier target) {
        int random = new Random().nextInt(100);
        
        if (attacker instanceof Rifle) {
            if (random < 10) {
                // Rifle kills
                System.out.println(attacker.getClass().getSimpleName() + " from " + name + " kills " + target.getClass().getSimpleName() + " from the enemy.");
                targetArmy.soldiers.remove(target);
            }
        } else if (attacker instanceof Sniper) {
            if (random < 20) {
                // Sniper kills
                System.out.println(attacker.getClass().getSimpleName() + " from " + name + " kills " + target.getClass().getSimpleName() + " from the enemy.");
                targetArmy.soldiers.remove(target);
            }
        }
    }
}

// Factory Pattern
interface Soldier {
    void attack(Soldier target);

    void defend(Soldier attacker);
}

class Sniper implements Soldier {
    @Override
    public void attack(Soldier target) {
        // Sniper-specific action
        System.out.println("Sniper shoots at " + target.getClass().getSimpleName());
    }

    @Override
    public void defend(Soldier attacker) {
        // Sniper-specific defense
        System.out.println("Sniper takes cover from " + attacker.getClass().getSimpleName());
    }
}

class Rifle implements Soldier {
    @Override
    public void attack(Soldier target) {
        // Rifle-specific action
        System.out.println("Rifleman fires at " + target.getClass().getSimpleName());
    }

    @Override
    public void defend(Soldier attacker) {
        // Rifle-specific defense
        System.out.println("Rifleman braces for attack from " + attacker.getClass().getSimpleName());
    }
}

interface SoldierFactory {
    Soldier createSoldier();
}

class SniperFactory implements SoldierFactory {
    @Override
    public Soldier createSoldier() {
        return new Sniper();
    }
}

class RifleFactory implements SoldierFactory {
    @Override
    public Soldier createSoldier() {
        return new Rifle();
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Army army1 = new Army("Army 1");
        Army army2 = new Army("Army 2");

        // Attach armies as observers to the battle manager
        ArmyObserver battleManager = new ArmyObserver("Battle Manager");
        army1.attach(battleManager);
        army2.attach(battleManager);

        // Create soldiers using factories
        SoldierFactory[] soldierFactories = {new SniperFactory(), new RifleFactory()};
        Random random = new Random();

        for (int i = 0; i < 50; i++) {
            SoldierFactory factory = soldierFactories[random.nextInt(soldierFactories.length)];
            Soldier soldier = factory.createSoldier();
            army1.addSoldier(soldier);
        }

        for (int i = 0; i < 50; i++) {
            SoldierFactory factory = soldierFactories[random.nextInt(soldierFactories.length)];
            Soldier soldier = factory.createSoldier();
            army2.addSoldier(soldier);
        }

        // Perform the battle
        while (true) {
            Army[] armies = {army1, army2}; 
            Army attackerArmy = armies[random.nextInt(armies.length)];
            Army defenderArmy;

            do {
                int defenderIndex = random.nextInt(armies.length);
                defenderArmy = armies[defenderIndex];
            } while (defenderArmy == attackerArmy);

            attackerArmy.attack(defenderArmy);

            System.out.println("Army 1 have: " + army1.soldiers.size());
            System.out.println("Army 2 have: " + army2.soldiers.size());
            System.out.println();
            Thread.sleep(1000);

            if (army1.soldiers.size() == 0) {
                battleManager.update("Army 2 Won the Battle!!!");
                break;
            } else if (army2.soldiers.size() == 0) {
                battleManager.update("Army 1 Won the Battle!!!");
                break;
            }
        }
    }
}
