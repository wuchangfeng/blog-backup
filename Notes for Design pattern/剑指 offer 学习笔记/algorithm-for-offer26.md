## 题目：删除链表中重复的节点

这个题目，非常有意思，值得多做几遍。很难想到的就是要定义一个临时头结点。

我们要确保prev要始终与下一个没有重复的结点连接在一起。

``` java 
/**
 * Created by allen on 2016/9/10.
 */
public class ex25 {

    public static class Node{
        private Node next;
        private int val;

        public Node(int val) {
            this.val = val;
        }

        public Node() {
        }
    }

    public static Node deleteSameNode(Node head){

        if (head == null)
            return null;
        if (head.next == null)
            return head;
        // 定义临时头结点
        Node root = new Node();
        Node preve = root;
        // 当前处理的节点
        Node node = head;
        root.next = node;

        while(node != null && node.next != null){
            if(node.val == node.next.val){
                System.out.println(node.val);
                while (node.next != null && node.next.val == node.val){
                    node = node.next;
                }
                // 这个画图就明白了，直接跳过中间很多重复得节点
                // node 节点已经跳过当前重复的所有节点之后了
                preve.next = node.next;
            }
            else{
                preve.next = node;
                preve.next = preve;
            }
            node = node.next;
        }
        return null;
    }

    public static void main(String[] args) {
        Node head = new Node(2);
        Node n1 = new Node(3);
        Node n2 = new Node(4);
        Node n3 = new Node(6);
        Node n4 = new Node(6);
        Node n5 = new Node(7);
        Node n6 = new Node(7);
        head.next = n1;
        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n5;
        n5.next = n6;
        deleteSameNode(n1);
    }
}

```

