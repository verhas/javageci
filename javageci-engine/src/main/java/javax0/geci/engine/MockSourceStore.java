package javax0.geci.engine;

import javax0.geci.api.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MockSourceStore implements SourceStore {

    private final Map<SetFileNameTuple, Source> setSources = new HashMap<>();
    private final Map<String, Source> thisSetSources = new HashMap<>();

    public void add(Source.Set set, String fileName, Source source) {
        setSources.put(new SetFileNameTuple(set, fileName), source);
    }

    public void add(String fileName, Source source) {
        thisSetSources.put(fileName, source);
    }

    @Override
    public Source get(String fileName) {
        return thisSetSources.get(fileName);
    }

    @Override
    public Source get(Source.Set set, String fileName) {
        return setSources.get(new SetFileNameTuple(set, fileName));
    }

    private static class SetFileNameTuple {
        private final Source.Set set;
        private final String fileName;

        private SetFileNameTuple(Source.Set set, String fileName) {
            this.set = set;
            this.fileName = fileName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SetFileNameTuple that = (SetFileNameTuple) o;
            return set.equals(that.set) &&
                fileName.equals(that.fileName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(set, fileName);
        }
    }
}
