package game;

public class Health {
    private int healthPool;     // Max health
    private int healthAmount;   // Current health

    public Health(int healthPool) {
        if (healthPool <= 0) {
            throw new IllegalArgumentException("Health pool must be positive.");
        }
        this.healthPool = healthPool;
        this.healthAmount = healthPool; // Start at full health
    }

    public int getHealthPool() {
        return healthPool;
    }

    public int getHealthAmount() {
        return healthAmount;
    }

    public void increaseHealth(int amount) {
        if (amount < 0) return;
        healthAmount = Math.min(healthPool, healthAmount + amount);
    }

    public void SetHealthAmount(int amount) {
         healthAmount = amount;
    }

    public void decreaseHealth(int amount) {
        if (amount < 0) return;
        healthAmount = Math.max(0, healthAmount - amount);
    }

    public boolean isAlive() {
        return healthAmount > 0;
    }

    public void setHealthPool(int newHealthPool) {
        if (newHealthPool <= 0) return;
        healthPool = newHealthPool;
        if (healthAmount > healthPool) {
            healthAmount = healthPool;
        }
    }

    @Override
    public String toString() {
        return "Health: " + healthAmount + "/" + healthPool;
    }
}

