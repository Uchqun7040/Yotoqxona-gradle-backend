package com.example.Yotoqxonagradle.service;

import com.example.Yotoqxonagradle.entity.FileStatus;
import com.example.Yotoqxonagradle.entity.FileStorage;
import com.example.Yotoqxonagradle.repository.FileStorageRepository;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class FileStorageService {
    @Autowired
    FileStorageRepository fileStorageRepository;

    private final Hashids hashids = new Hashids(getClass().getName(),6);

    @Value("${upload.folder}")
    private String uploadFolder;

    public void save(MultipartFile multiFile){
        FileStorage fileStorage = new FileStorage();
        fileStorage.setName(multiFile.getOriginalFilename());
        fileStorage.setExtension(ext(multiFile.getOriginalFilename()));
        fileStorage.setFileSize(multiFile.getSize());
        fileStorage.setContentType(multiFile.getContentType());
        fileStorage.setStatus(FileStatus.DRAFT);
        fileStorage = fileStorageRepository.save(fileStorage);


        LocalDate now = LocalDate.now();
        File uploadFolder = new File(String.format("%s/upload_files/%d/%d/%d/",
                this.uploadFolder,
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth()));
        if (uploadFolder.mkdirs()){

        }
        fileStorage.setHashId(hashids.encode(fileStorage.getId()));
        fileStorage.setUploadPath(String.format("upload_files/%d/%d/%d/%s.%s",
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                fileStorage.getHashId(),
                fileStorage.getExtension()));
        fileStorageRepository.save(fileStorage);
        uploadFolder = uploadFolder.getAbsoluteFile();
        File file = new File(uploadFolder,String.format("%s.%s",fileStorage.getHashId(),fileStorage.getExtension()));
        try{
            multiFile.transferTo(file);
        }catch(IOException e){
            System.out.println("Fayl saqlanmadi");
        }
    }
    @Transactional
    public FileStorage findByHashId(String hashId){
        return fileStorageRepository.findByHashId(hashId);
    }

    public void delete(String hashId){
        FileStorage fileStorage = fileStorageRepository.findByHashId(hashId);
        File file = new File(String.format("%s/%s",this.uploadFolder,fileStorage.getUploadPath()));
        if (file.delete()){
            fileStorageRepository.delete(fileStorage);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllDraft(){
        List<FileStorage> fileStorages = fileStorageRepository.findAllByStatus(FileStatus.DRAFT);
        for (FileStorage fileStorage : fileStorages) {
            delete(fileStorage.getHashId());
        }
    }

    protected String ext(String fullName){
        String ext = null;
        if(fullName != null && !fullName.isEmpty()){
            int n = fullName.lastIndexOf(".");
            if (n > 0 && n < fullName.length()-1){
                ext = fullName.substring(n+1);
            }
        }
        return ext;
    }
}
