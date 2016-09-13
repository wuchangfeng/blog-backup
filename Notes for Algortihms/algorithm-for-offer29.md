## 题目：删除无序链表中的重复节点

首先，链表式无序的，不能当成前面那题有序的一样考虑。而对于无序，我们采取的是一种空间换时间的做法。因为在 Hash 中，每次查询一次，所耗费的时间都是 o(1)。然后我们注意，对于链表的头节点，始终在开始的时候不会重复，先把它丢进集合中再说。另外，我们要始终记得，我们操作的对象，是链表，无论如何，都要保证链表不能断。嗯，底下这种做法，时间复杂度是 o(n),当然啦，空间复杂度也是 o(n)。不过，我们事先声明，对于空间复杂度问题，从最优的角度去看。



``` java
import java.util.HashSet;

/**
 * Created by allen on 2016/9/13.
 */
public class ex28 {

    public static class NodeList{
        private int val;
        private NodeList next;

        public NodeList(int val) {
            this.val = val;
        }
    }

    public static void deleteSame(NodeList head){
        if (head == null)
            return;
        NodeList pre = head;
        NodeList cur = head.next;
        HashSet<Integer> set = new HashSet<>();
        set.add(head.val);
        while (cur != null){
            // 当前节点不存在于 set 中，则插入
            if (!set.contains(cur.val)){
                pre = cur;
                set.add(cur.val);
            }
            // 当前节点存在于 set 中，跳过当前节点
            else{
                // 打印出重复节点的数值
                System.out.println(cur.val);
                pre.next = cur.next;
            }
                cur = cur.next;
        }

    }

    public static void main(String[] args) {
        NodeList n1 = new NodeList(2);
        NodeList n2 = new NodeList(1);
        NodeList n3 = new NodeList(1);
        NodeList n4 = new NodeList(3);
        NodeList n5 = new NodeList(4);
        NodeList n6 = new NodeList(3);
        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n5;
        n5.next = n6;
        deleteSame(n1);
    }
}

```

