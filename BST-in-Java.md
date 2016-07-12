---
title: Java 实现二叉排序树
date: 2015-09-05 10:28:45
tags: java
categories: About Java
---
java 实现二叉排序树

* 二叉树的引入
* 二叉树的基本操作
* 二叉树的**删除操作**

<!--more-->

## 二叉排序树


### 引入


二叉排序树或者是一棵空树，或者是具有下列性质的二叉树：

（1）若左子树不空，则左子树上所有结点的值均小于它的根结点的值；

（2）若右子树不空，则右子树上所有结点的值均大于它的根结点的值；

（3）左、右子树也分别为二叉排序树；

（4）没有键值相等的节点。


### 查找

``` java

	public Node find(int key){
        Node current = root;
        while (current.value!=key){
            if (key < current.value){
                current = current.leftChild;
            }else{
                current = current.rightChild;
            }
            if (current==null)
                return null;
        }
        return current;
    }
```	

### 插入

``` java
	 public void insert(int id) {
        Node newNode = new Node();
        newNode.value = id;
        if (root == null)
            root = newNode;
        else {
            Node current = root;
            Node parent;
            while (true) {
                parent = current;
                if (id < current.value) {
                    current = current.leftChild;
                    if (current == null) {
                        parent.leftChild = newNode;
                        return ;
                    }
                    }
                    else {
                       current = current.rightChild;
                       if (current == null) {
                          parent.rightChild = newNode;
                        return ;
                    }
                }

            }
        }
        return ;
    }
```

### 删除


### 过程解析

* **若删除节点为叶子节点**则直接删除即可，其父节点的左孩子或者右孩子为 NULL
* **若删除节点右一个子节点**可删除，并讲删除节点的父节点的左孩子或者右孩子指向被删除节点的左孩子或者右孩子
* **若删除节点有两个子节点**一般找到(a)的右子树中key最小的节点(c)代替它，同样分 c 是否为叶子节点还是有没有后代节点的情况。

![](http://7xrl8j.com1.z0.glb.clouddn.com/BST!.jpg)

如上图，我们一般都是找到6来替代5这个节点，并且6不可能有左孩子节点，我们需要更新指定5的父节点的指向以及6的父节点指向。

### 代码实现
``` java
	 public boolean delete(int key) {
        Node current = root;
        Node parent = root;
        boolean isLeftChild = true;

        while (current.value != key) {
            parent = current;
            if (key < current.value) {
                isLeftChild = true;
                current = current.leftChild;
            } else {
                isLeftChild = false;
                current = current.rightChild;
            }
            if (current == null)
                return false;
        }
        // 叶子节点
        if (current.leftChild == null && current.rightChild == null) {
            if (current == root)
                root = null;
            else if (isLeftChild)
                parent.leftChild = null;
            else
                parent.rightChild = null;
        // 只有一个子节点
        } else if (current.rightChild == null) {
            if (current == root)
                root = current.leftChild;
            else if (isLeftChild)
                parent.leftChild = current.leftChild;
            else
                parent.rightChild = current.leftChild;
        } else if (current.leftChild == null) {
            if (current == root)
                root = current.rightChild;
            else if (isLeftChild)
                parent.leftChild = current.rightChild;
            else
                parent.rightChild = current.rightChild;
        }
        // 两个子节点
        else {
            Node successor = getSuccessor(current);
            //根节点的话树置空
            if (current == root) {
                root = successor;
            } else if (isLeftChild) {
                parent.leftChild = successor;
            } else
                parent.rightChild = successor;
            //将找到的节点替代原节点，更新左孩子节点指向
            successor.leftChild = current.leftChild;
        }

        return true;
    }
    // 寻找替代节点
    private Node getSuccessor(Node delNode) {
        Node successorParent = delNode;
        Node successor = delNode;
        Node current = delNode.rightChild;

        while (current != null) {
            successorParent = successor;
            successor = current;
            current = current.leftChild;
        }
		//若替代节点不是删除节点的右孩子即替代节点是删除节点的后代
        if (successor != delNode.rightChild) {

            successorParent.leftChild = successor.rightChild;
            //更新替代节点的右孩子指向即为原来要删除节点的右孩子节点指向
            successor.rightChild = delNode.rightChild;
        }
        return successor;
    }
```


### 参考



[二叉树的删除操作](http://www.cnblogs.com/xunmengyoufeng/archive/2012/10/01/BityTree.html)

