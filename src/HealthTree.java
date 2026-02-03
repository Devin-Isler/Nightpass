// HealthTree class implements a AVL tree of HealthNodes, which is sorted by the health/missing health of the cards.
// The class act as the inner tree for the deck.
public class HealthTree {
    HealthNode root;
    boolean useMissingHp;
    int size;
    
    public HealthTree(boolean useMissingHp) {
        this.root = null;
        this.useMissingHp = useMissingHp;
    }

    public int size() {
        return size;
    }

    private void increaseSize() {
        size++;
    }

    private void decreaseSize() {
        size--;
    }

    public int getHpMax(){
        if (root == null){
            return 0;
        }
        return root.maxHp;
    }

    private int height(HealthNode node) {
        if (node == null)
            return -1;
        return node.height;
    }

    // Updating the max-min values
    private void updateHpStats(HealthNode node) {
        if (node != null) {
            // Checking right is enough
            int rightMax;
            if (node.right != null) {
                rightMax = node.right.maxHp;
            } else {
                rightMax = -1;
            }
            node.maxHp = Math.max(node.nodeHp, rightMax);

            // Checking left is enough
            int leftMin;
            if (node.left != null) {
                leftMin = node.left.minHp;
            } else {
                leftMin = Integer.MAX_VALUE;
            }

            node.minHp = Math.min(node.nodeHp, leftMin);

        }
    }

    private int getBalance(HealthNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    private HealthNode rotateRight(HealthNode y) {
        if (y == null || y.left == null) {
            return y;
        }

        HealthNode x = y.left;
        HealthNode temp = x.right;

        x.right = y;
        y.left = temp;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        // Update max values
        updateHpStats(y);
        updateHpStats(x);

        return x;
    }

    private HealthNode rotateLeft(HealthNode x) {
        if (x == null || x.right == null) {
            return x;
        }
        HealthNode y = x.right;
        HealthNode temp = y.left;

        y.left = x;
        x.right = temp;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        // Update max values
        updateHpStats(y);
        updateHpStats(x);

        return y;
    }

    public void insert(Card card) {
        root = insertNode(root, card);
        increaseSize();
    }

    // Locate the correct location, then add to Queue
    public HealthNode insertNode(HealthNode node, Card card) {
        if (node == null)
            return new HealthNode(card, useMissingHp);

        int cardValue = useMissingHp ? card.missingHp : card.curHp;
        if (cardValue < node.nodeHp)
            node.left = insertNode(node.left, card);
        else if (cardValue > node.nodeHp)
            node.right = insertNode(node.right, card);
        else {

            node.cards.enqueue(card);
            return node;
        }
        return rebalance(node);
    }

    // Method to find the node with minimum health value in a subtree
    public HealthNode minValueNode(HealthNode node) {
        HealthNode current = node;
        while (current.left != null)
        {
            current = current.left;
        }
        return current;
    }
    
    public void delete(Card card) {
        if (useMissingHp) {
            root = deleteNode(root, card.missingHp);
        } 
        else {
            root = deleteNode(root, card.curHp);
        }
        decreaseSize();
    }
    
    // Locate the node, extract the card, if Queue is completely empty, delete the node itself
    private HealthNode deleteNode(HealthNode node, int nodeHp) {
        if (node == null)
            return null;
    
        if (nodeHp < node.nodeHp) {
            node.left = deleteNode(node.left, nodeHp);
        } 
        else if (nodeHp > node.nodeHp) {
            node.right = deleteNode(node.right, nodeHp);
        } 
        else {
            // When find, extract the card
            node.cards.dequeue();
    
            if (node.cards.size == 0) {
                if (node.left == null)
                    return node.right;
                else if (node.right == null)
                    return node.left;
    
                HealthNode minNode = minValueNode(node.right);

                node.nodeHp = minNode.nodeHp;
                node.cards = minNode.cards;
    
                node.right = deleteNodeCompletely(node.right, minNode.nodeHp);
            }
        }
        return rebalance(node);
    }

    private HealthNode deleteNodeCompletely(HealthNode node, int nodeHp) {
        if (node == null)
            return null;
    
        if (nodeHp < node.nodeHp) {
            node.left = deleteNodeCompletely(node.left, nodeHp);
        } else if (nodeHp > node.nodeHp) {
            node.right = deleteNodeCompletely(node.right, nodeHp);
        } else {
            if (node.left == null)
                return node.right;
            else if (node.right == null)
                return node.left;
    
            HealthNode minNode = minValueNode(node.right);
            node.nodeHp = minNode.nodeHp;
            node.cards = minNode.cards;
            node.right = deleteNodeCompletely(node.right, minNode.nodeHp);
        }
    
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        updateHpStats(node);
    
        return node;
    }
    
    // Updating stats and rebalancing the tree to satify AVL tree condition
    private HealthNode rebalance(HealthNode node) {
        if (node == null)
            return null;
        
        // Update the height and necessary stats
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        updateHpStats(node);
    
        int balance = getBalance(node);
    
        if (balance > 1 && getBalance(node.left) >= 0)
            return rotateRight(node);
    
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0)
            return rotateLeft(node);
  
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
    
        return node;
    }

    public Card getMinHpSurviving(int att){
        return getMinHpSurviving(root, att);
    }

    // Return the minimum hp that will survive the attack
    private Card getMinHpSurviving(HealthNode node, int att) {
        Card candidate = null;
        while (node != null) {
            if (node.nodeHp > att) {
                candidate = node.cards.peek();
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return candidate;
    }

    public Card getMinHp(){
        return getMinHp(root);
    }

    // Return the card with minimum hp
    private Card getMinHp(HealthNode node) {
        if (node == null) {
            return null;
        }
        while(node.left != null){
            node = node.left;
        }
        return node.cards.peek();
    }

    public Card getMaxHpSmaller(int hp){
        return getMaxHpSmaller(root, hp);
    }   

    // Getting the maximum hp smaller than the heal, checking right-node-left order, since we want maximum
    private Card getMaxHpSmaller(HealthNode node, int heal) {
        // Base condition
        if (node == null || node.minHp > heal){
            return null;
        }

        Card rightResult = getMaxHpSmaller(node.right, heal);
        if (rightResult != null) {
            return rightResult;
        }

        if (node.nodeHp <= heal){
            Card currentResult = node.cards.peek();
            if (currentResult != null) {
                return currentResult;
            }
        }

        Card leftResult = getMaxHpSmaller(node.left, heal);
        return leftResult;
    }
    
}
