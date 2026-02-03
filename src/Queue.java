// Queue to store cards with the same att and hp.
// We want the first drawn card to be played first (FIFO).
public class Queue {
    int size;
    QueueNode front;
    QueueNode back;

    Queue(){
        this.size = 0;
        this.front = null;
        this.back = null;
    }

    // Inserting to the end
    public void enqueue(Card card){
        QueueNode node = new QueueNode(card);
        if(size == 0){
            front = node;
            back = node;
        }
        else{
            back.next = node;
            back = node;
        }
        size ++;
    }

    // Removing the front element
    public void dequeue(){
        if(size == 0){
        }
        else{
            front = front.next;
            size --;
            if(front == null){
                back = null;
            }
        }
    }

    // Getting the first element
    public Card peek(){
        if(size == 0){
            return null;
        }
        else{
            return front.card;
        }
    }
}
