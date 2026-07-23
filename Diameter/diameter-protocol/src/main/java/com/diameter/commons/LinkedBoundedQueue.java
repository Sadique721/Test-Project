package com.diameter.commons;

import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedBoundedQueue<E> extends AbstractQueue<E> implements BoundedQueue<E> {
  private final int capacity;
  
  static class Node<E> {
    E item;
    
    Node<E> next;
    
    Node(E x) {
      this.item = x;
    }
  }
  
  private int count = 0;
  
  private transient Node<E> head;
  
  private transient Node<E> last;
  
  private void enqueue(E x) {
    this.last = this.last.next = new Node<>(x);
  }
  
  private E dequeue() {
    Node<E> h = this.head;
    Node<E> first = h.next;
    h.next = h;
    this.head = first;
    E x = first.item;
    first.item = null;
    return x;
  }
  
  public LinkedBoundedQueue() {
    this(2147483647);
  }
  
  public LinkedBoundedQueue(int capacity) {
    if (capacity <= 0)
      throw new IllegalArgumentException(); 
    this.capacity = capacity;
    this.last = this.head = new Node<>(null);
  }
  
  public LinkedBoundedQueue(Collection<? extends E> c) {
    this(2147483647);
    int n = 0;
    for (E e : c) {
      if (e == null)
        throw new NullPointerException(); 
      if (n == this.capacity)
        throw new IllegalStateException("Queue full"); 
      enqueue(e);
      n++;
    } 
    this.count = n;
  }
  
  public int size() {
    return this.count;
  }
  
  public int remainingCapacity() {
    return this.capacity - this.count;
  }
  
  public boolean offer(E e) {
    if (e == null)
      throw new NullPointerException(); 
    if (this.count == this.capacity)
      return false; 
    int c = -1;
    if (this.count < this.capacity) {
      enqueue(e);
      c = this.count;
      this.count++;
    } 
    return (c >= 0);
  }
  
  public E poll() {
    if (this.count == 0)
      return null; 
    E x = null;
    if (this.count > 0) {
      x = dequeue();
      this.count--;
    } 
    return x;
  }
  
  public E peek() {
    if (this.count == 0)
      return null; 
    Node<E> first = this.head.next;
    if (first == null)
      return null; 
    return first.item;
  }
  
  private void unlink(Node<E> p, Node<E> trail) {
    p.item = null;
    trail.next = p.next;
    if (this.last == p)
      this.last = trail; 
  }
  
  public Object[] toArray() {
    int size = this.count;
    Object[] a = new Object[size];
    int k = 0;
    for (Node<E> p = this.head.next; p != null; p = p.next)
      a[k++] = p.item; 
    return a;
  }
  
  public <T> T[] toArray(T[] a) {
    int size = this.count;
    if (a.length < size)
      a = (T[])Array.newInstance(a.getClass().getComponentType(), size); 
    int k = 0;
    for (Node<E> p = this.head.next; p != null; p = p.next)
      a[k++] = (T)p.item; 
    if (a.length > k)
      a[k] = null; 
    return a;
  }
  
  public Iterator<E> iterator() {
    return new Itr();
  }
  
  private class Itr implements Iterator<E> {
    private LinkedBoundedQueue.Node<E> current = LinkedBoundedQueue.this.head.next;
    
    private LinkedBoundedQueue.Node<E> lastRet;
    
    private E currentElement;
    
    Itr() {
      if (this.current != null)
        this.currentElement = this.current.item; 
    }
    
    public boolean hasNext() {
      return (this.current != null);
    }
    
    private LinkedBoundedQueue.Node<E> nextNode(LinkedBoundedQueue.Node<E> p) {
      while (true) {
        LinkedBoundedQueue.Node<E> s = p.next;
        if (s == p)
          return LinkedBoundedQueue.this.head.next; 
        if (s == null || s.item != null)
          return s; 
        p = s;
      } 
    }
    
    public E next() {
      if (this.current == null)
        throw new NoSuchElementException(); 
      E x = this.currentElement;
      this.lastRet = this.current;
      this.current = nextNode(this.current);
      this.currentElement = (this.current == null) ? null : this.current.item;
      return x;
    }
    
    public void remove() {
      if (this.lastRet == null)
        throw new IllegalStateException(); 
      LinkedBoundedQueue.Node<E> node = this.lastRet;
      this.lastRet = null;
      LinkedBoundedQueue.Node<E> trail = LinkedBoundedQueue.this.head, p = trail.next;
      for (; p != null; 
        trail = p, p = p.next) {
        if (p == node) {
          LinkedBoundedQueue.this.unlink(p, trail);
          break;
        } 
      } 
    }
  }
}
