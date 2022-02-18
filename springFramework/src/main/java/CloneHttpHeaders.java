import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CloneHttpHeaders implements MultiValueMap<String, String>, Serializable {

    private static final long serialVersionUID = -8578554704772377436L;

    @Override
    public String getFirst(String key) {
        return null;
    }

    @Override
    public void add(String key, String value) {

    }

    @Override
    public void addAll(String key, List<? extends String> values) {

    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {

    }

    @Override
    public void set(String key, String value) {

    }

    @Override
    public void setAll(Map<String, String> values) {

    }

    @Override
    public Map<String, String> toSingleValueMap() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public List<String> get(Object key) {
        return null;
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return null;
    }

    @Override
    public List<String> remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<List<String>> values() {
        return null;
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return null;
    }
}
