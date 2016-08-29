### 题目：在 O(1) 时间内删除指定节点

注意是指定节点，另外用覆盖的思想，复杂度的计算很有意思

``` java
public class ex14 {

    public static class Node{
        int value;
        Node next;
    }

    public static boolean deleteNode(Node head,Node delete){

        if (head == null || delete == null)
            return false;
        // 如果要删除的节点是尾节点
        if (delete.next == null){
            while (head.next != delete){
                head = head.next;
            }
            head.next = null;
            delete = null;
            return true;
        }else if (delete.next != null){
            // 覆盖的思想
            Node deleteNext = delete.next;
            delete.value = deleteNext.value;
            delete.next = deleteNext.next;
            deleteNext = null;
            return true;
        }else{
            if (head == delete){
                head = delete = null;
            }
            return true;
        }
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
        list1.next.next.next.next = new Node();
        list1.next.next.next.next.value = 7;

        System.out.println(deleteNode(list1, list1.next));

       while (list1 != null){
           System.out.println(list1.value);
           list1 = list1.next;
       }

    }
}
```

