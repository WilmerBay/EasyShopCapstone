package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController
{
    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao)
    {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    public Profile getProfile(Principal principal)
    {
        try
        {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            return profileDao.getByUserId(user.getId());
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch profile.");
        }
    }

    @PutMapping
    public void updateProfile(@RequestBody Profile profile, Principal principal)
    {
        try
        {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            profile.setUserId(user.getId());
            profileDao.update(profile);
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update profile.");
        }
    }
}
