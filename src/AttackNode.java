// AttackNode class represents a node in the AttackTree. It stores another AVL tree, HealthTree so that when same attack card is inserted, it goes to the inner tree. 
// Also every node stores its subtrees maximum attack, maximum health and min attack. 
// This makes it easier to search, since it allows us to make decision for both health and attack faster. 
public class AttackNode {
    HealthTree healthTree;
    int maxHp;
    int maxAtt;
    int minAtt;
    int nodeAtt;
    AttackNode right;
    AttackNode left;
    int height;

    AttackNode(Card card) {
        this.healthTree = new HealthTree(false); // For card deck
        healthTree.insert(card);
        this.nodeAtt = card.curAtt;
        this.maxAtt = card.curAtt;
        this.minAtt = card.curAtt;
        this.maxHp = card.curHp;
        this.right = null;
        this.left = null;
        this.height = 0;
    }
}
