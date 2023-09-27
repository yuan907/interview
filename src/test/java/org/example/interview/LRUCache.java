package org.example.interview;


import java.util.HashMap;
import java.util.Map;

//58同城一面
public class LRUCache {

//    int get(int key) 如果关键字 key 存在于缓存中，则返回关键字的值，否则返回 -1 。
//    void put(int key, int value) 如果关键字 key 已经存在，则变更其数据值 value ；如果不存在，则向缓存中插入该组 key-value 。如果插入操作导致关键字数量超过 capacity ，则应该 逐出 最久未使用的关键字。
//    函数 get 和 put 必须以 O(1) 的平均时间复杂度运行。


    private int capacity;
    private int size;
    private Map<Integer, Node> cache;

    //定义头尾
    private Node head;
    private Node tail;


    //进行初始化操作
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.cache = new HashMap<>();

    }

    public int get(int key) {

        //判断当前缓存中是否存在这个key，不存在返回-1
        if (!cache.containsKey(key)) {
            return -1;
        }
        //存在情况下
        Node node = cache.get(key);
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            if (size == capacity) {
                cache.remove(tail.key);
                removeNode(tail);
            } else {
                size++;
            }

            Node node = new Node(key, value);
            addNode(node);
            cache.put(key, node);
        }
    }

    public void moveToHead(Node node) {
        removeNode(node);
        addNode(node);
    }

    //增加
    private void addNode(Node node) {
        node.prev = null;
        node.next = head;
        if (head != null) {
            head.prev = node;
        }
        head = node;
        if (tail == null) {
            tail = node;
        }
    }

    //移除
    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }


    private static class Node {
        int key;
        int value;

        Node prev;

        Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}

