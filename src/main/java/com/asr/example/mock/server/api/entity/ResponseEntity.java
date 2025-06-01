package com.asr.example.mock.server.api.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseEntity implements Persistable<Long> {

    @Id
    Long responseId;
    Long endpointId;
    Long priority;
    Boolean isActive;
    Integer httpStatus;
    String responseBody;
    String responseHeaders;
    @CreatedBy
    String createdBy;
    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedBy
    String updatedBy;
    @LastModifiedDate
    LocalDateTime updatedAt;
    @Version
    Long version;
    @Transient
    boolean isNew = true;

    @Override
    public Long getId() {
        return this.responseId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}