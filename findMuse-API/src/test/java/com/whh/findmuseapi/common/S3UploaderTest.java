package com.whh.findmuseapi.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.whh.findmuseapi.common.util.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class S3UploaderTest {
    
    @Mock
    private AmazonS3 amazonS3;
    
    private S3Uploader s3Uploader;
    
    private final String BUCKET_NAME = "test-bucket";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        s3Uploader = new S3Uploader(amazonS3, BUCKET_NAME);
    }
    
    @Test
    void 파일_업로드_테스트() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile("test.txt", "test.txt", "text/plain", "Hello, World!".getBytes());
        String dirName = "testDir";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/testDir/test.txt";
        
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(null);
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL(expectedUrl));
        
        // when
        String result = s3Uploader.upload(multipartFile, dirName);
        
        // then
        assertEquals(expectedUrl, result);
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(1)).getUrl(eq(BUCKET_NAME), anyString());
    }
    
    @Test
    void 파일_삭제_테스트() {
        // given
        String fileName = "testDir/test.txt";
        
        // when
        s3Uploader.deleteFile(fileName);
        
        // then
        verify(amazonS3, times(1)).deleteObject(eq(BUCKET_NAME), eq(fileName));
    }
    
    @Test
    void 파일_업데이트_테스트() throws IOException {
        // given
        MultipartFile newFile = new MockMultipartFile("newTest.txt", "newTest.txt", "text/plain", "New content".getBytes());
        String oldFileName = "testDir/oldTest.txt";
        String dirName = "testDir";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/testDir/newTest.txt";
        
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(null);
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL(expectedUrl));
        
        // when
        String result = s3Uploader.updateFile(newFile, oldFileName, dirName);
        
        // then
        assertEquals(expectedUrl, result);
        verify(amazonS3, times(1)).deleteObject(eq(BUCKET_NAME), eq(oldFileName));
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(1)).getUrl(eq(BUCKET_NAME), anyString());
    }
}
