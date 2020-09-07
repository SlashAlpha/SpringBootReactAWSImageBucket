package com.slash.aws.upload.datastore;


import com.slash.aws.upload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileData {

    private static final List<UserProfile> USER_PROFILES=new ArrayList<>();

    static {

        USER_PROFILES.add(new UserProfile(UUID.fromString("53e633ae-48a1-464c-819f-b8d1e7933c84"),"BernardJackson","pixa.jpg-057196cf-3176-4e81-a7d7-bdfef5afacb5"));
        USER_PROFILES.add(new UserProfile(UUID.fromString("0f721edb-8660-432d-a4c9-ca4c7f708fe6"),"MicheleMinaj","pixie.jpg-e030016a-8acf-4dfd-bad3-0c4975a59b45"));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
