### 题目：判断链表是否有环

根据环的定义，只要有某些节点曾今遍历过，就说明链表中存在环。于是，我的想法是，在遍历节点的过程中，把遍历过的节点存储到集合里，然后每次遍历的时候都检查一下集合中是否存在该节点。如果有，说明有环，否则环不存在。但是这样复杂度就为 n*n 了。

这个题目确实很巧妙。设立一个快指针一个慢指针，如果链表没有环，这两个指针永远不可能相遇。反之则肯定有机会相遇。具体参考可以看[知乎上关于这个问题的讨论](https://www.zhihu.com/question/23208893)



```java
/**
 * Created by allen on 2016/8/25.
 */
public class ex12 {

    public static class Node{
        int value;
        Node next;
    }



    public static boolean hasCircle(Node head){

        Node fast;
        Node slow;
        fast = slow = head;
        while(fast != null && fast.next != null){

            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow){
                return true;
            }

        }


        return false;
    }



    public static void main(String[] args) {

        Node list1 = new Node();
        list1.value = 1;
        list1.next = new Node();
        list1.next.value = 3;
        list1.next.next = new Node();
        list1.next.next.value = 5;
        list1.next.next.next = list1;
        //list1.next.next.next = new Node();
        //list1.next.next.next.value = 6;

        System.out.println(hasCircle(list1));

    }
}

```

