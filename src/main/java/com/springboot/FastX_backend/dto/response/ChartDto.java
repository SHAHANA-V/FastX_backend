package com.springboot.FastX_backend.dto.response;

import java.util.List;

public record ChartDto(
        List<String> labels,
        List<Long> values
) {
}
