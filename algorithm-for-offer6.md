## 题目：

输入一个链表,输出该链表中倒数第k 个结点.为了符合大多数人的习惯,本题从 1 开始计数,即链表的 尾结点是倒数第 1 个结点.例如一个链表有 6 个结点,从头结点开始它们的值依次是 1 、2、3、4、5 、6。这个链表的倒数第 3 个结点是值为 4 的结点。

思路：

正常的思路是从头至尾遍历，然后又折回来 k 个位置。但是这里是单链表，行不通。

第二种想法，从头再开始遍历 n-k+1 次？k = 1？k = 2 ？....所带来的开销是不固定的。

最好的做法还是设立两个指针。相距 k 的距离。

代码如下啦：

``` java
public class ex8 {

    public static class Node{
        int value;
        Node next;
    }

    public static Node getKnode(Node head,int k){
		// 重视这里
        if (k < 0 || head == null){
            return null;
        }
        Node pointer = head;
        for (int i = 1;i < k;i++){

            if (pointer.next == null){
                return null;
            }else{
                pointer = pointer.next;
            }
        }
		// pointer 已经指向 k-1 了
        while (pointer.next != null){
            pointer = pointer.next;
            head = head.next;
        }
        return head;
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

        root.next.next.next.next = new Node();
        root.next.next.next.next.value = 6;

        System.out.println(getKnode(root,2).value);
    }
}
```

