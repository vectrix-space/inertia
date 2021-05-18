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

/**
 * Represents the version of an object within the universe.
 *
 * @since 0.2.0
 */
public interface Version {
  /**
   * Returns a new {@link Version} for the specified {@code index},
   * {@code version}, {@code universe}.
   *
   * @param index the index
   * @param version the version
   * @param universe the universe
   * @return the version
   * @since 0.2.0
   */
  static Version version(final int index, final int version, final int universe) {
    return new VersionImpl(index, version, universe);
  }

  /**
   * The index of the object.
   *
   * @return the object index
   * @since 0.2.0
   */
  int index();

  /**
   * The version of the object.
   *
   * @return the object version
   * @since 0.2.0
   */
  int version();

  /**
   * The universe index the object belongs to.
   *
   * @return the object universe index
   * @since 0.2.0
   */
  int universe();
}
