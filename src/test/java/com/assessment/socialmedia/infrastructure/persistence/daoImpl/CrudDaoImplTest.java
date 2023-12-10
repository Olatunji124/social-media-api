package com.assessment.socialmedia.infrastructure.persistence.daoImpl;

import com.assessment.socialmedia.usecases.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrudDaoImplTest {

    @Mock
    private JpaRepository<String, String> mockRepository;

    private CrudDaoImpl<String, String> crudDaoImplUnderTest;

    @BeforeEach
    void setUp() {
        crudDaoImplUnderTest = new CrudDaoImpl<>(mockRepository);
    }

    @Test
    void testFindById() {
        // Setup
        when(mockRepository.findById("id")).thenReturn(Optional.of("value"));

        // Run the test
        final Optional<String> result = crudDaoImplUnderTest.findById("id");

        // Verify the results
        assertThat(result).isEqualTo(Optional.of("value"));
    }

    @Test
    void testFindById_JpaRepositoryReturnsAbsent() {
        // Setup
        when(mockRepository.findById("id")).thenReturn(Optional.empty());

        // Run the test
        final Optional<String> result = crudDaoImplUnderTest.findById("id");

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testGetRecordById() {
        // Setup
        when(mockRepository.findById("id")).thenReturn(Optional.of("result"));

        // Run the test
        final String result = crudDaoImplUnderTest.getRecordById("id");

        // Verify the results
        assertThat(result).isEqualTo("result");
    }

    @Test
    void testGetRecordById_JpaRepositoryReturnsAbsent() {
        // Setup
        when(mockRepository.findById("id")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> crudDaoImplUnderTest.getRecordById("id")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testSaveRecord() {
        // Setup
        when(mockRepository.saveAndFlush("record")).thenReturn("result");

        // Run the test
        final String result = crudDaoImplUnderTest.saveRecord("record");

        // Verify the results
        assertThat(result).isEqualTo("result");
    }
}
