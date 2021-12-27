/*
 * This file is part of inertia, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package space.vectrix.inertia.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CustomIteratorImpl<T, E> implements CustomIterator<E> {
  private final Iterator<T> backingIterator;
  private Function<T, E> mapper = ignored -> (E) ignored;
  private Predicate<E> filter = ignored -> true;
  private Consumer<E> remove = ignored -> {};
  private E next;
  private E current;

  public CustomIteratorImpl(final @NonNull Iterator<T> backingIterator) {
    this.backingIterator = backingIterator;
    this.next = this.nextValue();
  }

  public CustomIteratorImpl(final @NonNull Iterator<T> backingIterator, final @NonNull Function<T, E> mapper) {
    this.backingIterator = backingIterator;
    this.mapper = mapper;
    this.next = this.nextValue();
  }

  public CustomIteratorImpl(final @NonNull Iterator<T> backingIterator, final @NonNull Function<T, E> mapper, final @NonNull Consumer<E> remove) {
    this.backingIterator = backingIterator;
    this.mapper = mapper;
    this.remove = remove;
    this.next = this.nextValue();
  }

  @Override
  public @NonNull CustomIterator<E> with(final @NonNull Predicate<? super E> predicate) {
    this.filter = this.filter.and(predicate);
    this.next = this.retryValue();
    return this;
  }

  @Override
  public @NonNull CustomIterator<E> without(final @NonNull Predicate<? super E> predicate) {
    this.filter = this.filter.and(predicate.negate());
    this.next = this.retryValue();
    return this;
  }

  @Override
  public boolean hasNext() {
    return this.next != null;
  }

  @Override
  public E next() {
    if((this.current = this.next) == null) throw new NoSuchElementException();
    this.next = this.nextValue();
    return this.current;
  }

  @Override
  public void remove() {
    if(this.current == null) throw new IllegalStateException();
    this.remove.accept(this.current);
    this.current = null;
  }

  private @Nullable E nextValue() {
    T next;
    E value;
    while(this.backingIterator.hasNext()) {
      if((next = this.backingIterator.next()) != null
        && (value = this.mapper.apply(next)) != null
        && this.filter.test(value)) {
        return value;
      }
    }
    return null;
  }

  private @Nullable E retryValue() {
    if(this.filter.test(this.next)) return this.next;
    return this.nextValue();
  }
}
