package com.asr.example.mock.server.api.mapper;

import com.asr.example.mock.server.api.entity.ResponseEntity;
import com.asr.example.mock.server.api.model.request.ResponseRequest;
import com.asr.example.mock.server.api.model.response.ResponseResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.http.HttpStatusCode;

@Mapper(
    componentModel = "spring"
)
public interface ResponseMapper {

    @Mapping(target = "statusCode", source = "httpStatus")
    ResponseResponse mapResponse(ResponseEntity entity);

    @Mapping(target = "responseBody", source = "body")
    @Mapping(target = "responseHeaders", source = "headers")
    @Mapping(target = "httpStatus", source = "status")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    ResponseEntity mapEntity(ResponseRequest request);

    @Mapping(target = "responseBody", source = "body")
    @Mapping(target = "responseHeaders", source = "headers")
    @Mapping(target = "httpStatus", source = "status")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void mapEntity(ResponseRequest request, @MappingTarget ResponseEntity entity);

    default HttpStatusCode mapHttpStatusCode(Integer status) {
        return HttpStatusCode.valueOf(status);
    }

    default Integer mapHttpStatusCode(HttpStatusCode status) {
        return status != null ? status.value() : null;
    }
}
