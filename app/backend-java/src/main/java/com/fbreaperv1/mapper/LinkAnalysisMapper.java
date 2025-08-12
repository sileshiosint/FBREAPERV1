package com.fbreaperv1.mapper;

import com.fbreaperv1.dto.LinkAnalysisResultDTO;
import com.fbreaperv1.model.LinkAnalysisResult;

public class LinkAnalysisMapper {
    public static LinkAnalysisResultDTO toDTO(LinkAnalysisResult entity) {
        if (entity == null) return null;
        return new LinkAnalysisResultDTO(
            entity.getNodes(),
            entity.getEdges(),
            entity.getMetrics()
        );
    }
}
