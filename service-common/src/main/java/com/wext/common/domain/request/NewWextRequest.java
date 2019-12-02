package com.wext.common.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewWextRequest implements Serializable {
    private static final long serialVersionUID = -6718755449592748773L;

    @NotNull(message = "Content can not be null.")
    @Size(min = 1, max = 3000, message = "Comment too long")
    private String content;

    @Size(max = 1, message = "Only up to 1 picture are allowed.")
    private List<String> picURLs;

    @NotNull(message = "Path can not null.")
    private String path;
}
