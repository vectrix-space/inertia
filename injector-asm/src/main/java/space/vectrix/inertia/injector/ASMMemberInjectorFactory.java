package space.vectrix.inertia.injector;

import static java.util.Objects.requireNonNull;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import space.vectrix.inertia.util.DelegateClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class ASMMemberInjectorFactory<T, M> implements MemberInjector.Factory<T, M> {
  private static final String PACKAGE = "space.vectrix.inertia.asm.generated";
  private static final String SUPER_NAME = "java/lang/Object";
  private static final String INJECTOR_DESCRIPTION = "(Ljava/lang/Object;Ljava/lang/Object;)V";
  private static final String[] GENERATED_INJECTOR_NAME = new String[] { Type.getInternalName(MemberInjector.class) };

  private final AtomicInteger identifier = new AtomicInteger();

  private final LoadingCache<Field, Class<? extends MemberInjector<T, M>>> cache;
  private final DelegateClassLoader classLoader;
  private final String session;

  {
    this.session = UUID.randomUUID().toString().substring(26);
  }

  public ASMMemberInjectorFactory(final @NonNull ClassLoader parent) {
    requireNonNull(parent, "parent");

    this.classLoader = new DelegateClassLoader(parent);

    this.cache = CacheBuilder.newBuilder()
      .initialCapacity(16)
      .weakValues()
      .build(CacheLoader.from(field -> {
        requireNonNull(field, "field");

        final Class<?> target = field.getDeclaringClass();
        final Class<?> type = field.getType();
        final String targetName = Type.getInternalName(target);
        final String typeName = Type.getInternalName(type);

        final String name = this.createClasspath(target, field, type);

        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_FINAL, name.replace('.', '/'), null,
          ASMMemberInjectorFactory.SUPER_NAME, ASMMemberInjectorFactory.GENERATED_INJECTOR_NAME);

        MethodVisitor methodVisitor;
        {
          methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
          methodVisitor.visitCode();
          methodVisitor.visitVarInsn(ALOAD, 0);
          methodVisitor.visitMethodInsn(INVOKESPECIAL, SUPER_NAME, "<init>", "()V", false);
          methodVisitor.visitInsn(RETURN);
          methodVisitor.visitMaxs(0, 0);
          methodVisitor.visitEnd();
        }

        {
          methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "member", ASMMemberInjectorFactory.INJECTOR_DESCRIPTION, null, null);
          methodVisitor.visitCode();
          methodVisitor.visitVarInsn(ALOAD, 1);
          methodVisitor.visitTypeInsn(CHECKCAST, targetName);
          methodVisitor.visitVarInsn(ALOAD, 2);
          methodVisitor.visitTypeInsn(CHECKCAST, typeName);
          methodVisitor.visitFieldInsn(PUTFIELD, targetName, field.getName(), typeName);
          methodVisitor.visitInsn(RETURN);
          methodVisitor.visitMaxs(0, 0);
          methodVisitor.visitEnd();
        }

        classWriter.visitEnd();
        return this.classLoader.defineClass(name, classWriter.toByteArray());
      }));
  }

  @Override
  public @NonNull MemberInjector<T, M> create(final @NonNull Object object, final @NonNull Field field) throws Exception {
    if (!Modifier.isPublic(object.getClass().getModifiers())) throw new IllegalStateException(String.format("Target class '%s' must be public!", object.getClass().getName()));
    if (!Modifier.isPublic(field.getModifiers())) throw new IllegalStateException(String.format("Injectable field '%s' must be public!", field));
    if (Modifier.isFinal(field.getModifiers())) throw new IllegalStateException(String.format("Injectable field '%s' must NOT be final!", field));

    return this.cache.getUnchecked(field).newInstance();
  }

  private String createClasspath(final Class<?> target, final Field field, final Class<?> type) {
    return String.format("%s.%s.%s-%s-%s-%d",
      ASMMemberInjectorFactory.PACKAGE,
      this.session,
      target.getSimpleName(),
      field.getName(),
      type.getSimpleName(),
      this.identifier.getAndIncrement()
    );
  }
}
