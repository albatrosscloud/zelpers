package pe.albatross.zelpers.dao;

import java.io.Serializable;
import java.util.List;

public interface Crud<T extends Serializable> {

    List<T> all();

    List<T> all(List<Long> ids);

    List<T> all(SqlUtil sqlUtil);

    T find(final long id);

    T find(SqlUtil sqlUtil);

    Long count(SqlUtil sqlUtil);

    void save(final T entity);

    void update(final T entity);

    void delete(final T entity);

    void delete(final long id);

}
