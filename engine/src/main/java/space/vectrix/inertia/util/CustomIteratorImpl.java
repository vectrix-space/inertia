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
import space.vectrix.inertia.util.functional.ThrowableConsumer;
import space.vectrix.inertia.util.functional.ThrowableFunction;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/* package */ final class CustomIteratorImpl<T, E> implements CustomIterator<E> {
  private final Iterator<T> backingIterator;
  private final Function<T, E> mapper;
  private final Consumer<E> remove;
  private Predicate<E> filter = ignored -> true;
  private E next;
  private E current;

  /* package */ CustomIteratorImpl(final @NonNull Iterator<@NonNull T> backingIterator,
                                   final @NonNull ThrowableFunction<@NonNull T, @Nullable E, ? extends Throwable> mapper,
                                   final @NonNull ThrowableConsumer<@NonNull E, ? extends Throwable> remove) {
    this.backingIterator = backingIterator;
    this.mapper = mapper;
    this.remove = remove;
    this.next = this.nextValue();
  }

  @Override
  public @NonNull CustomIterator<E> with(final @NonNull Predicate<? super E> predicate) {
    requireNonNull(predicate, "predicate");
    this.filter = this.filter.and(predicate);
    this.next = this.retryValue();
    return this;
  }

  @Override
  public @NonNull CustomIterator<E> without(final @NonNull Predicate<? super E> predicate) {
    requireNonNull(predicate, "predicate");
    this.filter = this.filter.and(predicate.negate());
    this.next = this.retryValue();
    return this;
  }

  @Override
  public boolean hasNext() {
    return this.next != null;
  }

  @Override
  public @NonNull E next() {
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
    if(this.next != null && this.filter.test(this.next)) return this.next;
    return this.nextValue();
  }
}
