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
package space.vectrix.inertia;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.component.ComponentContainer;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.holder.HolderContainer;
import space.vectrix.inertia.holder.HolderFunction;
import space.vectrix.inertia.injection.DummyInjectionMethodFactory;
import space.vectrix.inertia.injection.DummyInjectionStructureFactory;
import space.vectrix.inertia.injection.InjectionMethod;
import space.vectrix.inertia.injection.InjectionStructure;
import space.vectrix.inertia.internal.InternalComponentContainer;
import space.vectrix.inertia.internal.InternalHolderContainer;

import java.util.Optional;

/* package */ final class UniverseImpl implements Universe {
  private final InjectionStructure.Factory structureFactory;
  private final InjectionMethod.Factory methodFactory;
  private final ComponentContainer componentContainer;
  private final HolderContainer holderContainer;
  private final int index;

  /* package */ UniverseImpl(final int index, final BuilderImpl builder) {
    this.index = index;
    this.methodFactory = builder.methodFactory;
    this.structureFactory = builder.structureFactory;
    this.componentContainer = new InternalComponentContainer(this, this.structureFactory, this.methodFactory);
    this.holderContainer = new InternalHolderContainer(this);
  }

  @Override
  public int index() {
    return this.index;
  }

  @Override
  public @NonNull Holder createHolder() {
    return this.holderContainer.createHolder();
  }

  @Override
  public <T extends Holder> @NonNull T createHolder(final @NonNull HolderFunction<T> function) {
    return this.holderContainer.createHolder(function);
  }

  @Override
  public boolean valid(final @NonNull Holder holder) {
    return this.holderContainer.valid(holder);
  }

  @Override
  public boolean containsComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    return this.componentContainer.containsComponent(holder, componentType);
  }

  @Override
  public <T> @Nullable T getComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    return this.componentContainer.getComponent(holder, componentType);
  }

  @Override
  public @NonNull <T> Optional<T> getPresentComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    return this.componentContainer.getPresentComponent(holder, componentType);
  }

  @Override
  public <T> @NonNull T addComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    return this.componentContainer.addComponent(holder, componentType);
  }

  @Override
  public <T> @Nullable T removeComponent(final @NonNull Holder holder, final @NonNull ComponentType componentType) {
    return this.componentContainer.removeComponent(holder, componentType);
  }

  public static class BuilderImpl implements Universe.Builder {
    private InjectionMethod.Factory methodFactory = new DummyInjectionMethodFactory();
    private InjectionStructure.Factory structureFactory = new DummyInjectionStructureFactory();

    @Override
    public @NonNull Builder injectionStructure(final InjectionStructure.@NonNull Factory factory) {
      this.structureFactory = factory;
      return this;
    }

    @Override
    public @NonNull Builder injectionMethod(final InjectionMethod.@NonNull Factory factory) {
      this.methodFactory = factory;
      return this;
    }

    protected Universe build(final int index) {
      return new UniverseImpl(index, this);
    }
  }
}
