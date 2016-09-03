### 题目：判断二叉树的深度

如果只有一个根节点，为 0 。然后就取左右子树的深度 + 1 啦。想到递归。学习二叉树的定义。

``` java
/**
 * Created by allen on 2016/9/2.
 */
public class ex19 {

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

    public static int treeDepth(BinaryTreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = treeDepth(root.left);
        int right = treeDepth(root.right);
        return left > right ? (left + 1) : (right + 1);
    }
    // 测量完全二叉树的深度
    //             1
    //         /      \
    //        2        3
    //       /\       / \
    //      4  5     6   7
    //      /
    //     8
    public static void main(String[] args) {
        BinaryTreeNode n1 = new BinaryTreeNode(1);
        BinaryTreeNode n2 = new BinaryTreeNode(1);
        BinaryTreeNode n3 = new BinaryTreeNode(1);
        BinaryTreeNode n4 = new BinaryTreeNode(1);
        BinaryTreeNode n5 = new BinaryTreeNode(1);
        BinaryTreeNode n6 = new BinaryTreeNode(1);
        BinaryTreeNode n7 = new BinaryTreeNode(1);
        BinaryTreeNode n8 = new BinaryTreeNode(1);
        n1.left = n2;
        n1.right = n3;
        n2.left = n4;
        n2.right = n5;
        n3.left = n6;
        n3.right = n7;
        n4.left = n8;
        int i = treeDepth(n1);
        System.out.println(i);
    }
}

```

