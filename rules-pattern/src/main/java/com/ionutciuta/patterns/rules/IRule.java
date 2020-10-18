package com.ionutciuta.patterns.rules;

public interface IRule<E> {
    boolean matches(E input);
    void apply(E input);
}
