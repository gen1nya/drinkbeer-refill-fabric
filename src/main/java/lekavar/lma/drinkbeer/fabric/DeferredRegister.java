package lekavar.lma.drinkbeer.fabric;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Прослойка вместо NeoForge {@code DeferredRegister}.
 * <p>
 * На Fabric отложенных регистри-эвентов нет: реестры открыты во время инициализации мода,
 * поэтому {@link #register} регистрирует объект сразу и возвращает уже готовый
 * {@link Supplier}. Сигнатуры намеренно повторяют NeoForge-овские — так файлы
 * {@code registries/*} остаются почти дословно апстримовскими, и мерджить обновления
 * апстрима дешевле.
 * <p>
 * Порядок регистрации = порядок инициализации классов: если {@code ItemRegistry}
 * ссылается на {@code BlockRegistry.X.get()}, JVM сама инициализирует
 * {@code BlockRegistry} первым.
 */
public class DeferredRegister<T> {

    private final Registry<T> registry;
    private final String namespace;

    private DeferredRegister(Registry<T> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    @SuppressWarnings("unchecked")
    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> key, String namespace) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location());
        if (registry == null) {
            throw new IllegalStateException("Нет встроенного реестра " + key.location());
        }
        return new DeferredRegister<>(registry, namespace);
    }

    public ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(namespace, name);
    }

    public <R extends T> Supplier<R> register(String name, Supplier<? extends R> supplier) {
        R value = supplier.get();
        Registry.register(registry, id(name), value);
        return () -> value;
    }

    /** Для реестров, где апстрим держит {@link Holder} (эффекты, креатив-табы). */
    public <R extends T> Holder.Reference<T> registerForHolder(String name, Supplier<? extends R> supplier) {
        return Registry.registerForHolder(registry, id(name), supplier.get());
    }
}
