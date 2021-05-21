package space.vectrix.inertia.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.function.Function;

/**
 * Useful map functions.
 *
 * @since 0.2.0
 */
public interface MapFunctions {
  /**
   * Returns the existing {@code V} value for the specified {@code K} key
   * otherwise returns (but does not store) the result from the specified
   * {@link Function}.
   *
   * @param map the map
   * @param key the key
   * @param function the function
   * @param <K> the key type
   * @param <V> the value type
   * @return the value
   * @since 0.2.0
   */
  static <K, V> V getOr(final @NonNull Map<K, V> map, final @NonNull K key, final @NonNull Function<K, V> function) {
    V value = map.get(key);
    if(value == null) value = function.apply(key);
    return value;
  }

  /**
   * Returns the existing {@code V} value for the specified {@code int} key
   * otherwise returns (but does not store) the result from the specified
   * {@link Int2ObjectFunction}.
   *
   * @param map the map
   * @param key the key
   * @param function the function
   * @param <V> the value type
   * @return the value
   * @since 0.2.0
   */
  static <V> V getOr(final @NonNull Int2ObjectMap<V> map, final int key, final @NonNull Int2ObjectFunction<V> function) {
    V value = map.get(key);
    if(value == null) value = function.apply(key);
    return value;
  }
}
