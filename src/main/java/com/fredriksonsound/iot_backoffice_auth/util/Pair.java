package com.fredriksonsound.iot_backoffice_auth.util;

/**
 * Simple Pair implementation
 * @param <K> the Key type
 * @param <V> the value type
 */
public class Pair<K, V> {
    public final K first;
    public final V second;

    public Pair(K f, V s) {
        this.first = f;
        this.second = s;
    }
}

