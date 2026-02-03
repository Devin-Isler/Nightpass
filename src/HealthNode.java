// HealthNode class represents a node in the HealthTree.
// Its key can be missingHp(for discard) or curHp(for playable card) depending on the usage.
public class HealthNode {
    Queue cards;
    int maxHp;
    int nodeHp; // The node value, missingHp or curHp
    HealthNode right;
    HealthNode left;
    int height;
    int minHp;

    HealthNode(Card card, boolean useMissingHp) {
        this.cards = new Queue();
        this.cards.enqueue(card);
        // For the discard pile
        if (useMissingHp) {
            this.maxHp = card.missingHp;
            this.nodeHp = card.missingHp;
            this.minHp = card.missingHp;
        } 
        // For the inner tree of the deck
        else {
            this.maxHp = card.curHp;
            this.nodeHp = card.curHp;
            this.minHp = card.curHp;
        }
        this.right = null;
        this.left = null;
        this.height = 0;
    }
}
