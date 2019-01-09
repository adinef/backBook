package net.fp.backBook.services;

import java.io.Serializable;
import java.util.List;

public interface BasicCrudService<E extends Serializable, Id> {
    E getById(Id id);
    List<E> getAll();
    void delete(Id id);
    E add(E offer);
    E modify(E offer);
}
