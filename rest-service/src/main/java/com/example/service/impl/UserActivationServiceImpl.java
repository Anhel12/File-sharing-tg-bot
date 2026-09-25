package com.example.service.impl;

import com.example.dao.AppUserDao;
import com.example.service.UserActivationService;
import com.example.utils.UserCryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserDao appUserDao;
    private final UserCryptoTool userCryptoTool;

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = userCryptoTool.idOf(cryptoUserId);
        var optional = appUserDao.findById(userId);
        if(optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUserDao.save(user);
            return true;
        }
        return false;
    }
}
