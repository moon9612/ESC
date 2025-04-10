package com.esc.wmg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esc.wmg.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>{

    // select * from member where email = ? and pw = ?
	public UserEntity findByEmailAndPw(String email, String pw);
}
