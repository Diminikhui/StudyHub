from __future__ import annotations
from typing import List, Optional, Tuple


class BinaryHeapWithLocators:
    def __init__(self) -> None:
        self._items: List[Tuple[int, int]] = []
        self._locators: List[int] = []

    def peek(self) -> Optional[int]:
        return self._items[0][0] if self._items else None

    def push(self, value: int) -> int:
        locator = len(self._locators)
        self._locators.append(len(self._items))
        self._items.append((value, locator))
        self._sift_up(len(self._items) - 1)
        return locator

    def pop(self) -> Optional[int]:
        if not self._items:
            return None
        top_value, _ = self._items[0]
        last = len(self._items) - 1
        if last != 0:
            self._swap(0, last)
        self._items.pop()
        if self._items:
            self._sift_down(0)
        return top_value

    def decrease(self, locator: int, delta: int) -> None:
        i = self._locators[locator]
        current, _ = self._items[i]
        self._items[i] = (current - delta, locator)
        self._sift_up(i)

    def _sift_up(self, i: int) -> None:
        while i > 0:
            parent = (i - 1) // 2
            if self._items[parent][0] <= self._items[i][0]:
                return
            self._swap(i, parent)
            i = parent

    def _sift_down(self, i: int) -> None:
        while True:
            child = self._min_child(i)
            if child is None or self._items[i][0] <= self._items[child][0]:
                return
            self._swap(i, child)
            i = child

    def _swap(self, i: int, j: int) -> None:
        li = self._items[i][1]
        lj = self._items[j][1]
        self._items[i], self._items[j] = self._items[j], self._items[i]
        self._locators[li] = j
        self._locators[lj] = i

    def _min_child(self, i: int) -> Optional[int]:
        left = i * 2 + 1
        right = left + 1
        n = len(self._items)

        if left >= n:
            return None
        if right >= n or self._items[left][0] < self._items[right][0]:
            return left
        return right
