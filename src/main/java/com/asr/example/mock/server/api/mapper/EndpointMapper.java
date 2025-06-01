package com.asr.example.mock.server.api.mapper;

import com.asr.example.mock.server.api.entity.EndpointEntity;
import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.response.EndpointResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring"
)
public interface EndpointMapper {

    EndpointResponse mapResponse(EndpointEntity entity);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    EndpointEntity mapEntity(EndpointRequest request);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void mapEntity(EndpointRequest request, @MappingTarget EndpointEntity entity);

}
