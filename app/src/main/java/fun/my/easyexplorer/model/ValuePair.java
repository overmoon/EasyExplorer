package fun.my.easyexplorer.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * Created by admin on 2016/11/8.
 */

public class ValuePair<T, V> {
    @Expose
    T name;
    @Expose
    V value;

    public ValuePair() {

    }

    public ValuePair(@NonNull T name, @NonNull V value) {
        this.name = name;
        this.value = value;
    }

    public T getName() {
        return name;
    }

    public void setName(T name) {
        this.name = name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof ValuePair)) {
            ValuePair vp = (ValuePair) o;
            return vp.name.equals(name) && vp.value.equals(value);
        }
        return false;
    }
}
