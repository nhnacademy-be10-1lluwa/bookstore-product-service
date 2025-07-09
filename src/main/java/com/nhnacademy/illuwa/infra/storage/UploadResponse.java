package com.nhnacademy.illuwa.infra.storage;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadResponse {
    private String objectName;
    private String url;
    private String changedName;
}
