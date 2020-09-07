package com.slash.aws.upload.profile;

import com.amazonaws.services.imagebuilder.model.Image;
import com.amazonaws.services.s3.AmazonS3;
import com.slash.aws.upload.bucket.BucketName;
import com.slash.aws.upload.config.AmazonConfig;
import com.slash.aws.upload.datastore.FakeUserProfileData;
import com.slash.aws.upload.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;


    private final FileStore fileStore;




    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;

    }

    List<UserProfile>getUserProfiles(){
        return userProfileDataAccessService.getUserProfiles();
   }

     void uploadUserProfileManager(UUID userProfileId, MultipartFile file) {


         isFileEmpty(file);
         isImage(file);
         UserProfile user = isUser(userProfileId);
         Map<String,String> metadata = new HashMap<>();
       metadata.put("content-type",  file.getContentType());
       metadata.put("content-length", String.valueOf(file.getSize()));
    String path=  getPathStyleResourcePath(BucketName.PROFILE_IMAGE.getBucketName(),getHostStyleResourcePath(user.getUserProfileId().toString()));
            //String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),user.getUserProfileId());
         System.out.println(path);
  String filename=  String.format("%s-%s",file.getOriginalFilename(),UUID.randomUUID());
         System.out.println(filename);


        try{
        fileStore.save(path,filename,Optional.of(metadata),file.getInputStream());
        user.setUserProfileImageLink(filename);
        }
        catch (IOException e){
            e.printStackTrace();
        }

}


    private UserProfile isUser(UUID userProfileId) {
        UserProfile user= userProfileDataAccessService
              .getUserProfiles()
              .stream()
              .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException(String.format("User profile %s", userProfileId)));
        return user;
    }

    private void isImage(MultipartFile file) {
        if(!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(),ContentType.IMAGE_PNG.getMimeType(),ContentType.IMAGE_GIF.getMimeType())
               .contains(file.getContentType())){
           throw new  IllegalStateException("File must be an image");}
    }

    private void isFileEmpty(MultipartFile file) {
        if(file.isEmpty()){ throw new IllegalStateException("cannot upload empty file");}
    }

    private String getHostStyleResourcePath(String key) {
        String resourcePath = key;
        if (key != null && key.startsWith("/")) {
            resourcePath = "/" + key;
        }

        return resourcePath;
    }

    private String getPathStyleResourcePath(String bucketName,String key) {
        return bucketName == null ? key : bucketName + "/" + (key != null ? key : "");
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user=isUser(userProfileId);
        String fullpath =  String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),user.getUserProfileId());
//        user.getUserProfileImageLink()
//                .map(key -> fileStore.download(path,key))
//                .orElse(new byte[0]);
      //  System.out.println(path);
        System.out.println(user.getUsername());
        String fullkey= user.getUserProfileImageLink();
        System.out.println(fullkey);
     return fileStore.download(fullpath,fullkey);


    }
}
