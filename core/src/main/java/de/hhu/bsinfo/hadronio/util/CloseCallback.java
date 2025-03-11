package de.hhu.bsinfo.hadronio.util;

@FunctionalInterface
public interface CloseCallback<T> {
    void onClose(final T obj);
}
