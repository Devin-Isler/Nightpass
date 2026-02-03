// Card class creates a card in the game and stores necessary information about the card.
public class Card {
    String name;
    int baseAtt;
    int curAtt;
    int baseHp;
    int curHp;
    int missingHp;

    Card(String name, int att, int hp){
        this.name = name;
        this.baseAtt = att;
        this.curAtt = att;
        this.baseHp = hp;
        this.curHp = hp;
        this.missingHp = 0;
    }

    // Partially heal
    public void partialRevive(int heal) {
        missingHp -= heal;
        baseAtt = (int) (baseAtt * 0.95);
        curAtt = baseAtt;
    }

    // Fully heal, ready to fight
    public void fullyRevive() {
        missingHp = 0;
        baseAtt = (int) (baseAtt * 0.90);
        curHp = baseHp;
        curAtt = baseAtt;
    }

    public void takeDamage(int damage) {
        this.curHp -= damage;
        if(curHp <= 0){
            curHp = 0;
            missingHp = baseHp;
        }
    }
    // Changing the attack after battle
    public void changeAtt(){
        this.curAtt = Math.max(1, (int)Math.floor(this.baseAtt * this.curHp / this.baseHp));
    }
}
