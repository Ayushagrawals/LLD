public class Player {
    int playerid;
    Character symbol;

    public Player(int playerid, Character symbol) {
        this.playerid = playerid;
        this.symbol = symbol;
    }

    public Character getSymbol() {
        return this.symbol;
    }
}