package cloud.mallya.scan;

import cloud.mallya.model.FileMeta;

import java.util.Map;

public interface Scanner {
    Map<String, FileMeta> scan();
}
