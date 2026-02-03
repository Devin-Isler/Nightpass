// AttackTree class implements a AVL tree of AttackNodes which is sorted by the attack of the cards.
// The class act as the outer tree.
public class AttackTree {
    AttackNode root;
    int size;

    public AttackTree() {
        this.root = null;
        this.size = 0;
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

    private int height(AttackNode node) {
        if (node == null)
            return -1;
        return node.height;
    }

    // Updating the max-min values when there is insertion-deletion
    public void updateMaxValues(AttackNode node) {
        if (node != null) {
            int innerMaxHp;
            if (node.healthTree.root != null) {
                innerMaxHp = node.healthTree.root.maxHp;
            } else {
                innerMaxHp = -1;
            }

            int leftMaxHp;
            if (node.left != null) {
                leftMaxHp = node.left.maxHp;
            } else {
                leftMaxHp = -1;
            }

            int rightMaxHp;
            if (node.right != null) {
                rightMaxHp = node.right.maxHp;
            } else {
                rightMaxHp = -1;
            }
            // Check for the right, the left and also the whole tree inside the node's health tree.
            node.maxHp = Math.max(innerMaxHp, Math.max(leftMaxHp, rightMaxHp));

            // Checking right is sufficient since the left's att is always lower than the node itself.
            int rightMaxAtt;
            if (node.right != null) {
                rightMaxAtt = node.right.maxAtt;
            } else {
                rightMaxAtt = -1;
            }
            
            node.maxAtt = Math.max(node.nodeAtt, rightMaxAtt);
            // Checking left is sufficient since the right's att is always greater than the node itself.
            int leftMinAtt;
            if (node.left != null) {
                leftMinAtt = node.left.minAtt;
            } else {
                leftMinAtt = Integer.MAX_VALUE;
            }
            
            node.minAtt = Math.min(node.nodeAtt, leftMinAtt);

        }
    }

    private int getBalance(AttackNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    private AttackNode rotateRight(AttackNode y) {
        if (y == null || y.left == null) {
            return y;
        }

        AttackNode x = y.left;
        AttackNode temp = x.right;

        x.right = y;
        y.left = temp;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        // Update max values
        updateMaxValues(y);
        updateMaxValues(x);

        return x;
    }

    private AttackNode rotateLeft(AttackNode x) {
        if (x == null || x.right == null) {
            return x;
        }
        AttackNode y = x.right;
        AttackNode temp = y.left;

        y.left = x;
        x.right = temp;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        // Update max values
        updateMaxValues(x);
        updateMaxValues(y);

        return y;
    }

    public void insert(Card card) {
        root = insertNode(root, card);
        increaseSize();
    }
    // Locate the right location, then go for inner
    private AttackNode insertNode(AttackNode node, Card card) {
        if (node == null)
            return new AttackNode(card);

        if (card.curAtt < node.nodeAtt)
            node.left = insertNode(node.left, card);
        else if (card.curAtt > node.nodeAtt)
            node.right = insertNode(node.right, card);
        else {
            node.healthTree.insert(card);
            // Updating for the node inserted
            updateMaxValues(node);
            return node;
        }

        return rebalance(node);
    }

    public void delete(Card card) {
        root = deleteNode(root, card);
        decreaseSize();
    }
    // Locate the node, extract the card, if node is completely empty, delete the node itself
    private AttackNode deleteNode(AttackNode node, Card card) {
        if (node == null)
            return null;
     
        if (card.curAtt < node.nodeAtt) {
            node.left = deleteNode(node.left, card);
        } 
        else if (card.curAtt > node.nodeAtt) {
            node.right = deleteNode(node.right, card);
        } 
        else {
            node.healthTree.delete(card);
    
            if (node.healthTree.root != null) {
            } 
            else {
                if (node.left == null)
                    return node.right;
                else if (node.right == null)
                    return node.left;
                else {
                    // Finding the smallest of left
                    AttackNode successor = minValueNode(node.right);
    
                    node.nodeAtt = successor.nodeAtt;
                    node.healthTree = successor.healthTree;
                    // Delete the node completely
                    node.right = deleteNodeCompletely(node.right, successor.nodeAtt);
                }
            }
        }
        return rebalance(node);
    }
    
    private AttackNode deleteNodeCompletely(AttackNode node, int nodeAtt) {
        if (node == null)
            return null;
    
        if (nodeAtt < node.nodeAtt) {
            node.left = deleteNodeCompletely(node.left, nodeAtt);
        } else if (nodeAtt > node.nodeAtt) {
            node.right = deleteNodeCompletely(node.right, nodeAtt);
        } else {
            // Deleting the node completely
            if (node.left == null)
                return node.right;
            else if (node.right == null)
                return node.left;
            else {
                // Find the smallest of right, get it here
                AttackNode succ = minValueNode(node.right);
                node.nodeAtt = succ.nodeAtt;
                node.healthTree = succ.healthTree;
                node.right = deleteNodeCompletely(node.right, succ.nodeAtt);
            }
        }
        return rebalance(node);
    }
    
    // Rebalancing the height and the stats of the node, so that AVL tree structure is preserved
    private AttackNode rebalance(AttackNode node) {
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        updateMaxValues(node);
    
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

    // Method to find the node with minimum attack value in a subtree
    private AttackNode minValueNode(AttackNode node) {
        AttackNode current = node;
        // Go to the leftmost node
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public Card firstPriority(int att, int hp){
        Card card = firstPriorityOrSteal(root, att, hp, false);
        if (card == null){
            return null;
        }
        delete(card);
        return card;
    }
    // Try to find the card according to the first priority/stealing:
    // Checking left-node-right order since we want minAtt
    // Stealing is similar, just changing the hp-att and the equity condition is sufficent, so one function is sufficent for both.
    private Card firstPriorityOrSteal(AttackNode node, int att, int hp, boolean steal){
        // For base condition check att and hp stats for every step
        if (!steal){
            if (node == null || node.maxHp <= att || node.maxAtt < hp){
                return null;
            }
        }
        else{
            if (node == null || node.maxHp <= att || node.maxAtt <= hp){
                return null;
            }
        }
        // Search for left, if don't satisfy, don't go further left.
        Card leftResult = firstPriorityOrSteal(node.left, att, hp, steal);
        if (leftResult != null) {
            return leftResult;
        }
        // Search for the node, if don't satisfy go right.
        if ((!steal && node.healthTree.getHpMax() > att && node.nodeAtt >= hp) || (steal && node.healthTree.getHpMax() > att && node.nodeAtt > hp) ) {
            // If there is a card possible, get it from the innertree
            Card currentResult = node.healthTree.getMinHpSurviving(att);
            if (currentResult != null) {
                return currentResult;
            }
        }

        Card rightResult = firstPriorityOrSteal(node.right, att, hp, steal);
        return rightResult;
    }

    public Card secondPriority(int att, int hp){
        Card card = secondPriority(root, att, hp);
        if (card == null){
            return null;
        }
        delete(card);
        return card;
    }
    // Try to find the card according to the second priority:
    // First look for right, since we want maximum attack possible, then look for the node, and then the left
    private Card secondPriority(AttackNode node, int att, int hp){
         // For base condition check att and hp stats for every step
        if (node == null || node.maxHp <= att || node.minAtt >= hp){
            return null;
        }

        Card rightResult = secondPriority(node.right, att, hp);
        if (rightResult != null) {
            return rightResult;
        }

        if (node.healthTree.getHpMax() > att && node.nodeAtt < hp){
            // If there is a card possible, get it from the innertree
            Card currentResult = node.healthTree.getMinHpSurviving(att);
            if (currentResult != null) {
                return currentResult;
            }
        }

        Card leftResult = secondPriority(node.left, att, hp);
        return leftResult;
    }


    public Card thirdPriority(int att, int hp){
        Card card = thirdPriority(root, att, hp);
        if (card == null){
            return null;
        }
        delete(card);
        return card;
    }
    // No need to check hp, just checking for correct att is enough, first look for right
    private Card thirdPriority(AttackNode node, int att, int hp){

        if (node == null || node.maxAtt < hp){
            return null;
        }
        Card leftResult = thirdPriority(node.left, att, hp);
        if (leftResult != null) {
            return leftResult;
        }

        if (node.nodeAtt >= hp){
            // If there is a card possible, get it from the innertree
            Card currentResult = node.healthTree.getMinHp();
            if (currentResult != null) {
                return currentResult;
            }
        }

        Card rightResult = thirdPriority(node.right, att, hp);
        return rightResult;
    }

    // Since there is no need to check health, and to find maxAtt, just going right as possible is sufficient
    public Card fourthPriority(int att, int hp) {
        Card card = findMaxAtt(root);
        if (card == null){
            return null;
        }
        delete(card);
        return card;
    }

    // Method to find the node with maximum attack value in a subtree
    private Card findMaxAtt(AttackNode node){
        if (node == null) {
            return null;
        }
        // Go to the rigth if possible
        while (node.right != null) {
            node = node.right;
        }
        // If there is a card possible, get it from the innertree
        return node.healthTree.getMinHp();
    }

    // Finding the card which has stats more than the limits given
    public Card stealCard(int attackLimit, int healthLimit) {
        Card card = firstPriorityOrSteal(root, healthLimit, attackLimit, true);
        if (card == null) {
            return null;
        }
        delete(card);
        return card;
    }
}
