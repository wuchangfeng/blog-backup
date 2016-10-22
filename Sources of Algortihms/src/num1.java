/**
 * Created by allen on 2016/9/24.
 */
public class num1 {

    // 定义二叉树节点
    public static class TreeNode{
        TreeNode left;
        TreeNode right;
        int val;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    // 找出第 K 大的数值
    public static TreeNode FindtheK(TreeNode root,int k){

        TreeNode target = null;
        if (root == null || k < 0)
            return root;

        if (root.left != null)
            target = FindtheK(root.left,k);

        if (target == null){
            if (k == 1)
                target = root;
                k--;
        }

        if (target == null && root.right != null)
            target = FindtheK(root.right,k);

        return target;
    }


    public static void main(String[] args) {

        // 初始化二叉树
        TreeNode root = new TreeNode(5);
        TreeNode n1 = new TreeNode(3);
        TreeNode n2 = new TreeNode(7);
        TreeNode n3 = new TreeNode(2);
        TreeNode n4 = new TreeNode(4);
        TreeNode n5 = new TreeNode(6);
        TreeNode n6 = new TreeNode(8);
        root.left = n1;
        root.right = n2;
        n1.left = n3;
        n1.right = n4;
        n2.left = n5;
        n2.right = n6;
        System.out.println(FindtheK(root,6).val);

    }

}
