package com.lucia.api.model.dto.Contact;

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
public class ContactListResponseDTO {

    private List<ContactResponseDTO> data;

    private PageMetaDTO meta;
}
