package space.vectrix.inertia.util;

import org.checkerframework.checker.nullness.qual.NonNull;

@SuppressWarnings("unchecked")
public final class DelegateClassLoader extends ClassLoader {
  static {
    ClassLoader.registerAsParallelCapable();
  }

  public DelegateClassLoader(final ClassLoader parent) {
   super(parent);
  }

  public <T> @NonNull Class<T> defineClass(final @NonNull String name, final byte[] bytes) {
    return (Class<T>) this.defineClass(name, bytes, 0, bytes.length);
  }
}
