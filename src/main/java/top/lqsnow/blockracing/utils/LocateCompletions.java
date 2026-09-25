package top.lqsnow.blockracing.utils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Tab completion for /locatebiome and /locatestructure, read from Paper's live
 * registries so new vanilla biomes, structures and tags appear automatically.
 * Typing "#" switches to tag suggestions (e.g. #village, #abandoned_camp), which
 * vanilla /locate accepts to find the nearest member of a group.
 */
public final class LocateCompletions {
    private LocateCompletions() {
    }

    public static <T extends Keyed> List<String> complete(RegistryKey<T> registryKey, String input) {
        Registry<T> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
        String prefix = input.toLowerCase();
        if (prefix.startsWith("#")) {
            Stream<String> tags;
            try {
                tags = registry.getTags().stream()
                        .map(tag -> vanillaPath(tag.tagKey().key().namespace(), tag.tagKey().key().value()));
            } catch (UnsupportedOperationException e) {
                return List.of();
            }
            return filter(tags.filter(Objects::nonNull).map(tag -> "#" + tag), prefix);
        }
        return filter(registry.keyStream()
                .map(key -> vanillaPath(key.getNamespace(), key.getKey()))
                .filter(Objects::nonNull), prefix);
    }

    static List<String> filter(Stream<String> candidates, String prefix) {
        return candidates
                .filter(candidate -> candidate.startsWith(prefix))
                .sorted()
                .toList();
    }

    private static String vanillaPath(String namespace, String path) {
        return NamespacedKey.MINECRAFT.equals(namespace) ? path : null;
    }
}
