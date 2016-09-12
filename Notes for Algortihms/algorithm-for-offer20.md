## 题目：从上往下一次遍历二叉树

刚开始还没想起来是层次遍历。想到层次遍历就好办啦。访问父节点之后，如果父节点有左右子树，就将他们存到队列中，而队列满足的特性就是先进先出啊，而先出来的那个节点我们任然可以先判断有没有孩子节点。从左往右。

``` java

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by allen on 2016/9/4.
 */
public class ex21 {

    private static class BinaryTreeNode {
        int val;
        BinaryTreeNode left;
        BinaryTreeNode right;
        public BinaryTreeNode() {
        }
        public BinaryTreeNode(int val) {
            this.val = val;
        }
    }

    public static int printNode(BinaryTreeNode root){

        if (root == null)
            return -1;
        Queue<BinaryTreeNode> queue = new LinkedList<>();
        BinaryTreeNode CurrentNode;
        //CurrentNode = root;
        queue.add(root);
        while (!queue.isEmpty()){
            // 返回队列第一个元素并删除
            CurrentNode = queue.poll();
            System.out.println(CurrentNode.val);
            if (CurrentNode.left != null)
                queue.add(CurrentNode.left);
            if (CurrentNode.right != null)
                queue.add(CurrentNode.right);
        }
        return 0;
    }
    //             1
    //         /      \
    //        2        3
    //       /\       / \
    //      4  5
    //      /
    //     8
    public static void main(String[] args) {
        BinaryTreeNode n1 = new BinaryTreeNode(1);
        BinaryTreeNode n2 = new BinaryTreeNode(2);
        BinaryTreeNode n3 = new BinaryTreeNode(3);
        BinaryTreeNode n4 = new BinaryTreeNode(4);
        BinaryTreeNode n5 = new BinaryTreeNode(5);
        BinaryTreeNode n6 = new BinaryTreeNode(8);
        n1.left = n2;
        n1.right = n3;
        n2.left = n4;
        n2.right = n5;
        n4.left = n6;
        printNode(n1);
        //System.out.println();
    }
}

```

