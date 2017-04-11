package DataSource;

import java.util.function.Function;

public enum FileFormat {
    SPACE_SEPARATED(s -> s.split("\\s+")), COMMA_SEPARATED(s -> s.split(","));

    public Function<String, String[]> split;

    private FileFormat(Function<String, String[]> split) {
        this.split = split;
    }
}
