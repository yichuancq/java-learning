package com.example.leetcode.printlistreverse;

import com.example.leetcode.node.ListNode;
import com.example.leetcode.node.ListNodeBuilder;

/**
 * 输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）
 */
public class Solution {

    /**
     * 输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）。
     *
     * @param head
     * @return
     */
    public int[] reversePrint(ListNode head) {
        ListNode p = head;
        ListNode font = null;
        ListNode q = null;
        //计算需要数组的大小
        int number = 0;
        while (p != null) {
            font = p.next;
            p.next = font;
            p.next = q;
            q = p;
            p = font;
            number++;
        }
        int[] result = print(q, number);
        return result;
    }

    /**
     * 生成返回的数组
     *
     * @param node
     * @param number
     * @return
     */
    private int[] print(ListNode node, int number) {
        int[] result = new int[number];
        System.out.println("" + node.toString());
        int i = 0;
        while (node != null) {
            result[i] = node.val;
            i++;
            node = node.next;
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arrays = {1, 2, 3, 4, 5};
        Solution solution=new Solution();
        ListNode listNode=new ListNodeBuilder(arrays).buildListNode();
        System.out.println("array:" + listNode);
        System.out.println();
        //
        System.out.println("翻转打印");
        int[] result = solution.reversePrint(listNode);
        for (Integer integer : result) {
            System.out.print("\t" + integer);
        }

    }

}
