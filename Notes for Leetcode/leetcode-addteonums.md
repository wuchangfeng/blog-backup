> 
>
> You are given two linked lists representing two non-negative numbers. The digits are stored in reverse order and each of their nodes contain a single digit. Add the two numbers and return it as a linked list. Input: (2 -> 4 -> 3) + (5 -> 6 -> 4) Output: 7 -> 0 -> 8class Solution(object):
>

``` java
/**
 * Created by allen on 2016/11/3.
 */
public class leetcodeaddtwonums {

    public static class Node{
        int val;
        Node next;

        public Node(int val) {
            this.val = val;
        }
    }

    public static Node solution(Node list1,Node list2){
        if(list1 == null || list2 == null)
            return null;
        Node head = new Node(0);
        Node p = head;
        int carry = 0;
        while(list1!=null && list2!= null){
            int sum = carry + list1.val + list2.val;
            // sum % 取的余数
            p.next = new Node(sum%10);
            list1 = list1.next;
            list2 = list2.next;
            p = p.next;
            carry = sum/10;
        }
        while(list1!=null){
            int sum = carry + list1.val + list2.val;
            p.next = new Node(sum%10);
            p = p.next;
            list1 = list1.next;
            carry = sum/10;
        }
        while(list2!= null){
            int sum = carry + list1.val + list2.val;
            p.next = new Node(sum%10);
            p = p.next;
            list2 = list2.next;
            carry = sum/10;
        }
        // 注意这里，最后一位，对于长短不一的表虽然不大可能
        if (carry != 0)
            p.next = new Node(carry);
        return head.next;
    }
    public static void main(String[] args) {
        /*
        * Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
        * Output: 7 -> 0 -> 8
        * */
        Node list1 = new Node(2);
        list1.next = new Node(4);
        list1.next.next = new Node(3);

        Node list2 = new Node(5);
        list2.next = new Node(6);
        list2.next.next = new Node(4);

        Node list3 = solution(list1,list2);
        while (list3!=null) {
            System.out.println(list3.val);
            list3 = list3.next;
        }
    }
}

```

