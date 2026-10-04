package com.ritangkar.seogenerator.web;

import com.ritangkar.seogenerator.model.ProductInputData;
import com.ritangkar.seogenerator.model.SeoContentResult;
import com.ritangkar.seogenerator.service.SeoContentPipelineService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeoContentController {

    private final SeoContentPipelineService pipelineService;

    public SeoContentController(SeoContentPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    /** POST /api/seo-content/generate — runs the full seven-stage pipeline. */
    @PostMapping("/api/seo-content/generate")
    public SeoContentResult generate(@Valid @RequestBody ProductInputData input) {
        return pipelineService.run(input);
    }
}
