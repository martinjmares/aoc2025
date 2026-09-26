package name.mjm.aoc.model;

import java.util.Arrays;

/**
 * Stack for primitive {@code int}s with predefined max size. It can throw exception if size is exceeded.
 */
public class IntStack {

  private final int[] stack;
  private int writePointer;

  public IntStack(int size) {
    stack = new int[size];
    writePointer = 0;
  }

  public void push(int value) {
    stack[writePointer++] = value;
  }

  public int pop() {
    if (writePointer == 0) {
      throw new IllegalStateException("Stack is empty");
    }
    return stack[--writePointer];
  }

  public int peek() {
    if (writePointer == 0) {
      throw new IllegalStateException("Stack is empty");
    }
    return stack[writePointer - 1];
  }

  public int size() {
    return writePointer;
  }

  public String toString() {
    int[] content = Arrays.copyOf(stack, writePointer);
    return Arrays.toString(content);
  }

}
