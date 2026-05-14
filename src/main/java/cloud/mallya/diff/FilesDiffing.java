package cloud.mallya.diff;

import cloud.mallya.model.FileMeta;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * This is for computing 3 things from the source and the destination file lists.
 * 1. Files that are present in source but not in destination. -> Files need to be sent to remote
 * 2. Files that are present in destination but not in source. -> Files need to be deleted on remote
 * 3. Files that have differing modified time or size. -> Files need to be replaced from local to remote
 */

public record FilesDiffing(Map<String, FileMeta> toUpload,
                           Map<String, FileMeta> toDelete,
                           Map<String, FileMeta> toOverwrite) {
    public static FilesDiffing between(Map<String, FileMeta> sourceFiles,
                                       Map<String, FileMeta> destinationFiles) {

        MapDifference<String, FileMeta> diff = Maps.difference(sourceFiles, destinationFiles);

        Map<String, FileMeta> toUpload = new HashMap<>(diff.entriesOnlyOnLeft());
        Map<String, FileMeta> toDelete = new HashMap<>(diff.entriesOnlyOnRight());

        Map<String, FileMeta> toOverwrite = new HashMap<>();

        diff.entriesDiffering().forEach((k, v) -> {
            toOverwrite.put(k, v.leftValue()); // Always give preference to the local file version. We are copying from local to remote
            // TODO Might have to rethink this in future if needs change or provide users option to decide which version to keep.
        });

        return new FilesDiffing(toUpload, toDelete, toOverwrite);
    }
}