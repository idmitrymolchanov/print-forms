package com.example.printforms.service.storage;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StorageService {

    public String save(byte[] report) {
        //todo -> save report
        return UUID.randomUUID().toString();
    }
}
