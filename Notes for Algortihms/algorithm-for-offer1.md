### 题目：将一个链表从尾部到头输出

A。采用非递归方式来输出。很容易想到用栈来实现。

```java
public static void printNode2(Node root){

    Stack<Node> stack = new Stack<>();
    while (root != null){
        stack.push(root);
        root = root.next;
    }

    while (!stack.isEmpty()){
        System.out.print(stack.pop().value+" ");
    }
}
```

B. 采用递归方式

```java
public class ex1 {

    public static class Node{
        int value;
        Node next;
    }

    public static void printNode(Node root){
        if (root != null){
            if (root.next != null){
                printNode(root.next);
            }
            System.out.print(root.value + " ");
        }
    }

    public static void main(String[] args) {

        Node root = new Node();
        root.value = 2;

        root.next = new Node();
        root.next.value = 5;

        root.next.next = new Node();
        root.next.next.value = 1;

        root.next.next.next = new Node();
        root.next.next.next.value = 4;
        printNode(root);
    }
}
```