package com.lucia.api.model.dto.Call;

import com.lucia.api.model.dto.common.PageMetaDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallListResponseDTO {

    private List<CallResponseDTO> data;

    private PageMetaDTO meta;
}
