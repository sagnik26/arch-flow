package com.archflow.archigen.api.response;

import com.archflow.archigen.domain.model.LinkItem;
import lombok.Data;

import java.util.List;

@Data
public class TrendingLinksResponse {
    private List<LinkItem> links;
}
