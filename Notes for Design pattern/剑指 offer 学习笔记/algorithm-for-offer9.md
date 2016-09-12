### 题目：合并两个有序链表，使得合并之后的链表也是有序的

这题，首先要弄懂链表的结构，次要考虑时间复杂度，还有递归知识点。

``` java
public class ex11 {

    public static class Node{
        int value;
        Node next;
    }

    public static Node Merge(Node list1,Node list2){

        if(list1 == null)
            return list2;
        if (list2 == null)
            return list1;

        Node mergeHead = null;
        if (list1.value < list2.value){
            mergeHead = list1;
            mergeHead.next = Merge(list1.next,list2);
        }else{
            mergeHead = list2;
            mergeHead.next = Merge(list2.next,list1);
        }
        return mergeHead;
    }

    public static void main(String[] args) {

        Node list1 = new Node();
        list1.value = 1;
        list1.next = new Node();
        list1.next.value = 3;
        list1.next.next = new Node();
        list1.next.next.value = 5;

        Node list2 = new Node();
        list2.value = 2;
        list2.next = new Node();
        list2.next.value = 7;
        list2.next.next = new Node();
        list2.next.next.value = 9;

        Node list3 = Merge(list1,list2);
        while (list3 != null){
            System.out.println(list3.value);
            list3 = list3.next;
        }

    }
}
```

