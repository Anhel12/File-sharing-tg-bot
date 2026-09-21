package com.example.dao;

import com.example.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppPhotoDao extends JpaRepository<AppPhoto, Long> {
}
