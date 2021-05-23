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

import com.google.common.collect.Lists;
import net.kyori.coffee.math.range.i.IntRange;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.component.ComponentContainerImpl;
import space.vectrix.inertia.component.ComponentType;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.holder.HolderContainerImpl;
import space.vectrix.inertia.holder.HolderFunction;
import space.vectrix.inertia.injection.DummyInjectionMethodFactory;
import space.vectrix.inertia.injection.DummyInjectionStructureFactory;
import space.vectrix.inertia.injection.InjectionMethod;
import space.vectrix.inertia.injection.InjectionStructure;
import space.vectrix.inertia.processor.Processing;
import space.vectrix.inertia.processor.ProcessingImpl;
import space.vectrix.inertia.processor.Processor;
import space.vectrix.inertia.processor.ProcessorContainerImpl;

import java.util.Collection;
import java.util.Optional;

/* package */ final class UniverseImpl implements Universe {
  private final ProcessorContainerImpl processorContainer;
  private final ComponentContainerImpl componentContainer;
  private final HolderContainerImpl holderContainer;
  private final Processing processing;
  private final int index;

  /* package */ UniverseImpl(final int index, final BuilderImpl builder) {
    this.componentContainer = new ComponentContainerImpl(this, builder.structureFactory, builder.methodFactory);
    this.holderContainer = new HolderContainerImpl(this);
    this.processorContainer = new ProcessorContainerImpl();
    this.processing = builder.processing.create(this);
    this.index = index;
  }

  @Override
  public int index() {
    return this.index;
  }

  @Override
  public void tick() {
    this.processing.process(Lists.newArrayList(
      this.processorContainer,
      this.componentContainer,
      this.holderContainer
    ));
  }

  @Override
  public <T extends Processor> @Nullable T getProcessor(final @NonNull Class<T> type) {
    return this.processorContainer.getProcessor(type);
  }

  @Override
  public <T extends Processor> void addProcessor(final @NonNull T processor) {
    this.processorContainer.addProcessor(processor);
  }

  @Override
  public @NonNull Collection<? extends Processor> processors(final int priority) {
    return this.processorContainer.processors(priority);
  }

  @Override
  public @NonNull Collection<? extends Processor> processors(final @NonNull IntRange priorities) {
    return this.processorContainer.processors(priorities);
  }

  @Override
  public @NonNull Collection<? extends Processor> processors() {
    return this.processorContainer.processors();
  }

  @Override
  public boolean valid(final @NonNull Holder holder) {
    return this.holderContainer.valid(holder);
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
  public boolean removeHolder(final @NonNull Holder holder) {
    return this.holderContainer.removeHolder(holder);
  }

  @Override
  public @NonNull Collection<Holder> holders() {
    return this.holderContainer.holders();
  }

  @Override
  public @Nullable ComponentType getType(final int index) {
    return this.componentContainer.getType(index);
  }

  @Override
  public @Nullable ComponentType getType(final @NonNull Class<?> type) {
    return this.componentContainer.getType(type);
  }

  @Override
  public @Nullable ComponentType getType(final @NonNull String identifier) {
    return this.componentContainer.getType(identifier);
  }

  @Override
  public @Nullable ComponentType resolveType(final @NonNull Class<?> type) {
    return this.componentContainer.resolveType(type);
  }

  @Override
  public @NonNull Collection<ComponentType> types() {
    return this.componentContainer.types();
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

  @Override
  public void clearComponents(final @NonNull Holder holder) {
    this.componentContainer.clearComponents(holder);
  }

  @Override
  public @NonNull Collection<Object> components(final @NonNull Holder holder) {
    return this.componentContainer.components(holder);
  }

  public static class BuilderImpl implements Universe.Builder {
    private InjectionMethod.Factory methodFactory = new DummyInjectionMethodFactory();
    private InjectionStructure.Factory structureFactory = new DummyInjectionStructureFactory();
    private Processing.Factory processing = new ProcessingImpl.Factory();

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

    @Override
    public @NonNull Builder processing(final Processing.@NonNull Factory processing) {
      this.processing = processing;
      return this;
    }

    protected Universe build(final int index) {
      return new UniverseImpl(index, this);
    }
  }
}
