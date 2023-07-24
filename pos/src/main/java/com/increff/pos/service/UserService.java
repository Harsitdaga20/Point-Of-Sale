package com.increff.pos.service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	@Value("${supervisor.email}")
	private String supervisorEmail;

	@Transactional(rollbackFor = ApiException.class)
	public void addUser(UserPojo userPojo){
		getRole(userPojo);
		userDao.insert(userPojo);
	}

	@Transactional
	public UserPojo getCheckUserByEmail(String email){
		return userDao.selectUsers( email, null,null);
	}

	@Transactional
	public UserPojo getCheckUser(UserPojo userPojo)  {
		return userDao.selectUsers(userPojo.getEmail(), userPojo.getPassword(), null);

	}

	private void getRole(UserPojo userPojo){
		String[] superVisorEmails=supervisorEmail.split(",");
		for (String supervisorEmail : superVisorEmails) {
			if (supervisorEmail.trim().equalsIgnoreCase(userPojo.getEmail().trim())) {
				userPojo.setRole("supervisor");
				return;
			}
		}
		userPojo.setRole("operator");
	}
}
