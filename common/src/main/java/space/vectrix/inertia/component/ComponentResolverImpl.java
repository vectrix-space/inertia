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
package space.vectrix.inertia.component;

import static java.util.Objects.requireNonNull;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import space.vectrix.inertia.Component;
import space.vectrix.inertia.ComponentDependency;
import space.vectrix.inertia.HolderDependency;
import space.vectrix.inertia.Universe;
import space.vectrix.inertia.holder.Holder;
import space.vectrix.inertia.injector.InjectionMethod;
import space.vectrix.inertia.injector.InjectionStructure;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("UnstableApiUsage")
public final class ComponentResolverImpl<H extends Holder<C>, C> implements ComponentResolver<H, C> {
  private final AtomicInteger index = new AtomicInteger();
  private final MutableGraph<ComponentTypeImpl<H, C>> dependencies = GraphBuilder.undirected()
    .allowsSelfLoops(false)
    .expectedNodeCount(1000)
    .build();
  private final Universe<H, C> universe;

  /* package */ ComponentResolverImpl(final Universe<H, C> universe) {
    this.universe = universe;
  }

  @Override
  public @NonNull ComponentType resolve(final @NonNull Class<?> type,
                                        final InjectionStructure.@NonNull Factory<H, C> componentStructureFactory,
                                        final InjectionMethod.@NonNull Factory<?, C> componentInjector,
                                        final InjectionMethod.@NonNull Factory<?, H> holderInjector) {
    requireNonNull(type, "type");
    requireNonNull(componentStructureFactory, "componentStructureFactory");
    return this.resolve(
      null,
      type,
      componentStructureFactory,
      componentInjector,
      holderInjector
    );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends C> @NonNull T create(final int holder,
                                         final @NonNull ComponentType componentType) {
    requireNonNull(componentType, "componentType");
    final Optional<H> optionalHolder = this.universe.holders().get(holder);
    return this.create(
      holder,
      optionalHolder.orElse(null),
      null,
      (ComponentTypeImpl<H, C>) componentType
    );
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends C> @NonNull T create(final @NonNull H holder,
                                         final @NonNull ComponentType componentType) {
    return this.create(
      holder.index(),
      holder,
      null,
      (ComponentTypeImpl<H, C>) componentType
    );
  }

  @SuppressWarnings("unchecked")
  private ComponentType resolve(final @Nullable ComponentTypeImpl<H, C> parent,
                                final @NonNull Class<?> type,
                                final InjectionStructure.@NonNull Factory<H, C> componentStructureFactory,
                                final InjectionMethod.Factory<?, C> componentInjector,
                                final InjectionMethod.Factory<?, H> holderInjector) {
    final ComponentTypeImpl<H, C> componentType = ((ComponentTypesImpl<H, C>) this.universe.componentTypes()).put(type, key -> {
      final Component component = type.getAnnotation(Component.class);
      if(component == null) throw new IllegalArgumentException("Target type must have a component annotation!");
      return new ComponentTypeImpl<>(this.index.getAndIncrement(), component.id(), component.name(), type, componentStructureFactory.create(
        type,
        componentInjector,
        holderInjector
      ));
    });
    if(parent != null) {
      this.dependencies.putEdge(parent, componentType);
    } else {
      this.dependencies.addNode(componentType);
    }
    final InjectionStructure<H, C> structure = componentType.structure();
    for(final Map.Entry<Class<?>, InjectionStructure.Entry<ComponentDependency, ?, C>> entry : structure.components().entrySet()) {
      componentType.dependency(new ComponentLinkImpl(
        this.resolve(componentType, entry.getKey(), componentStructureFactory, componentInjector, holderInjector),
        entry.getValue().annotation().optional()
      ));
    }
    return componentType;
  }

  @SuppressWarnings("unchecked")
  private <T extends C> T create(final int holderIndex,
                                 final @Nullable H holder,
                                 final @Nullable ComponentType parentType,
                                 final ComponentTypeImpl<H, C> componentType) {
    final AbstractComponents<H, C> components = (AbstractComponents<H, C>) this.universe.components();
    final Class<?> componentClass = componentType.type();
    final T componentInstance = (T) this.createInstance(componentClass);
    if(components.put(holderIndex, componentType, componentInstance)) {
      if(holder != null) {
        final InjectionStructure<H, C> structure = componentType.structure();
        for(final InjectionStructure.Entry<HolderDependency, ?, H> holderEntry : structure.holders().values()) {
          this.injectMember(holderEntry.method(), componentInstance, holder);
        }
        for(final ComponentTypeImpl<H, C> dependency : this.dependencies.adjacentNodes(componentType)) {
          if(parentType != null && dependency.index() == componentType.index()) continue;
          final InjectionStructure.Entry<ComponentDependency, ?, C> componentInjection = structure.components().get(dependency.type());
          final C dependencyInstance = holder.get(dependency).orElseGet(() -> {
            if(!componentInjection.annotation().optional()) return this.create(holderIndex, holder, componentType, dependency);
            return null;
          });
          if(dependencyInstance != null) {
            if(componentInjection != null) {
              this.injectMember(componentInjection.method(), componentInstance, dependencyInstance);
            }
          }
        }
      }
    }
    return componentInstance;
  }

  @SuppressWarnings("unchecked")
  private <T, M> void injectMember(final @NonNull InjectionMethod<?, M> injector, final @NonNull T target, final @NonNull M member) {
    try {
      ((InjectionMethod<T, M>) injector).member(target, member);
    } catch(final Throwable throwable) {
      throw new IllegalStateException("Unable to inject member.", throwable);
    }
  }

  private Object createInstance(final @NonNull Class<?> componentClass) {
    try {
      return componentClass.getDeclaredConstructor().newInstance();
    } catch(final Throwable exception) {
      throw new IllegalStateException("Unable to instantiate component.", exception);
    }
  }

  public static final class Factory implements ComponentResolver.Factory {
    public Factory() {}

    @Override
    public <H extends Holder<C>, C> @NonNull ComponentResolver<H, C> create(final @NonNull Universe<H, C> universe) {
      requireNonNull(universe, "universe");
      return new ComponentResolverImpl<>(universe);
    }
  }
}
