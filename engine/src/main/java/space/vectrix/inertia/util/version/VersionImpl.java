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
package space.vectrix.inertia.util.version;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/* package */ final class VersionImpl implements Version {
  private final int index;
  private final int version;
  private final int universe;

  /* package */ VersionImpl(final int index, final int version, final int universe){
    this.index = index;
    this.version = version;
    this.universe = universe;
  }

  @Override
  public int index() {
    return this.index;
  }

  @Override
  public int version() {
    return this.version;
  }

  @Override
  public int universe() {
    return this.universe;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.index, this.version, this.universe);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if(this == other) return true;
    if(!(other instanceof Version)) return false;
    final Version that = (Version) other;
    return Objects.equals(this.index(), that.index())
      && Objects.equals(this.version(), that.version())
      && Objects.equals(this.universe(), that.universe());
  }

  @Override
  public String toString() {
    return "Version{index=" + this.index + ", version=" + this.version + ", universe=" + this.universe + "}";
  }
}
