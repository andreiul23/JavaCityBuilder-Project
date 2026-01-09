package main;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Stivă generică simplă limitată folosită pentru instantanee de anulare.
 */
public class SnapshotStack<T> {
	private final Deque<T> delegate = new ArrayDeque<>();
	private final int capacity;

	public SnapshotStack(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity must be positive");
		}
		this.capacity = capacity;
	}

	public void pushSnapshot(T value) {
		Objects.requireNonNull(value, "Snapshot cannot be null");
		delegate.push(value);
		while (delegate.size() > capacity) {
			delegate.removeLast();
		}
	}

	public T pollSnapshot() {
		return delegate.poll();
	}

	public int size() {
		return delegate.size();
	}
}
