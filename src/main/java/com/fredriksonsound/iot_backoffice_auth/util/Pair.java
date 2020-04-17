package com.fredriksonsound.iot_backoffice_auth.util;

/**
 * Pair....
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {
    public final K first;
    public final V second;

    public Pair(K f, V s) {
        this.first = f;
        this.second = s;
    }
}

