package com.narxoz.rpg.battle;
import java.util.List;
import java.util.Random;
public final class BattleEngine {
    private static BattleEngine instance;
    private Random random = new Random(1L);
    private BattleEngine() {
    }
    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }
    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }
    public void reset() {
        this.random = new Random(1L);
    }
    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        if (teamA == null || teamB == null) {
            throw new IllegalArgumentException("Teams cannot be null");
        }
        if (teamA.isEmpty() || teamB.isEmpty()) {
            throw new IllegalArgumentException("Teams cannot be empty");
        }
        EncounterResult result = new EncounterResult();
        int round = 0;
        while (hasLiving(teamA) && hasLiving(teamB)) {
            round++;
            result.addLog("=== Round " + round + " ===");
            attackPhase(teamA, teamB, result);
            removeDead(teamB);
            if (!hasLiving(teamB)) {
                break;
            }
            attackPhase(teamB, teamA, result);
            removeDead(teamA);
        }
        if (hasLiving(teamA)) {
            result.setWinner("Team A");
        } else {
            result.setWinner("Team B");
        }
        result.addLog("Battle finished. Winner: " + result.getWinner());
        return result;
    }
    private void attackPhase(List<Combatant> attackers,
                             List<Combatant> defenders,
                             EncounterResult result) {
        for (Combatant attacker : attackers) {
            if (!attacker.isAlive() || defenders.isEmpty()) {
                continue;
            }
            Combatant target = defenders.get(random.nextInt(defenders.size()));
            int damage = attacker.getAttackPower();
            result.addLog(attacker.getName()
                    + " attacks "
                    + target.getName()
                    + " for "
                    + damage + " damage");
            target.takeDamage(damage);
            if (!target.isAlive()) {
                result.addLog(target.getName() + " has died");
            }
        }
    }
    private boolean hasLiving(List<Combatant> team) {
        for (Combatant c : team) {
            if (c.isAlive()) {
                return true;
            }
        }
        return false;
    }
    private void removeDead(List<Combatant> team) {
        team.removeIf(c -> !c.isAlive());
    }
}
