package com.assessment.socialmedia.infrastructure.persistence.daoImpl;




import com.assessment.socialmedia.domain.dao.CrudDao;
import com.assessment.socialmedia.usecases.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public class CrudDaoImpl<T, ID> implements CrudDao<T, ID> {

    protected JpaRepository<T, ID> repository;
    public CrudDaoImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public T getRecordById(ID id) throws RuntimeException {
        return findById(id).orElseThrow(() -> new NotFoundException("Not found. record with id: "+id));
    }

    @Override
    public T saveRecord(T record) {
        return repository.saveAndFlush(record);
    }
}
