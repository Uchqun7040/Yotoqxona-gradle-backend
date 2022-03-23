package com.example.Yotoqxonagradle.repository;

import com.example.Yotoqxonagradle.entity.FileStatus;
import com.example.Yotoqxonagradle.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage,Long> {
    FileStorage findByHashId(String hashId);
    List<FileStorage> findAllByStatus(FileStatus status);
}
