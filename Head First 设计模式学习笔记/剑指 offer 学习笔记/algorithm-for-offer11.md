### 题目：返回链表的中间节点

这题跟求链表第 k 个节点很像，简单不做过多分析。只是考虑常规做法，先遍历一遍链表，然后再依次去到中间，这样效率很不好。

``` java
public class ex13 {

    public static class Node{
        int value;
        Node next;
    }



    public static Node middleNode(Node head){

        if (head == null)
            return null;
        Node fast;
        Node slow;
        fast = slow = head;
        while(fast != null && fast.next != null){

            fast = fast.next.next;
            slow = slow.next;
        }
        // 返回慢指针，此时他正在中间节点
        return slow;
    }



    public static void main(String[] args) {

        Node list1 = new Node();
        list1.value = 1;
        list1.next = new Node();
        list1.next.value = 3;
        list1.next.next = new Node();
        list1.next.next.value = 5;
        list1.next.next.next = new Node();
        list1.next.next.next.value = 6;
        //list1.next.next.next.next = new Node();
        //list1.next.next.next.next.value = 7;

        System.out.println(middleNode(list1).value);

    }
}
```

